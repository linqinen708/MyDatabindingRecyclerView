package com.linqinen708.mydatabinding.recyclerview.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.linqinen708.mydatabinding.recyclerview.adapter.BaseBindingAdapter;
import com.linqinen708.mydatabinding.recyclerview.utils.LogT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Ian on 2019/12/26.
 */
public class LoadMoreRecyclerView extends RecyclerView {

    private LinearLayoutManager mLayoutManager;

    private int lastVisibleItemPosition;

    private int lastY;

    /**
     * 是否正在上拉加载更多
     */
    private boolean isLoadingMore;

    public boolean isLoadingMore() {
        return isLoadingMore;
    }

    public void setLoadingMore(boolean loadingMore) {
        isLoadingMore = loadingMore;
    }

    private LoadMoreListener mLoadMoreListener;

    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        init();
        mLoadMoreListener = loadMoreListener;
    }

    public interface LoadMoreListener {
        void loadMore();
    }

    public LoadMoreRecyclerView(@NonNull Context context) {
        super(context);
    }

    public LoadMoreRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadMoreRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        LogT.i("初始化:" + getLayoutManager());
        if (getLayoutManager() != null && getLayoutManager() instanceof LinearLayoutManager) {
            mLayoutManager = (LinearLayoutManager) getLayoutManager();
        }
        if (mLayoutManager != null) {
            addOnScrollListener(new RecyclerView.OnScrollListener() {
                /**
                 * 当显示的item数量不够多，无法撑满屏幕高度时
                 * 无论是上拉还是下拉，都会触发该方法，
                 * 所以下拉需要满足条件
                 * 否则下拉也会触发加载更多
                 * 所以当 visibleItemCount < totalItemCount 才触发加载更多
                 * 即 item的数量够多，并超过屏幕高度时，才触发加载更多
                 * */
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    LogT.i("停止滑动:");
                        int visibleItemCount = mLayoutManager.getChildCount();
                        int totalItemCount = mLayoutManager.getItemCount();
//                        LogT.i(":" + getAdapter().getItemViewType(0));
//                        LogT.i("visibleItemCount:" + visibleItemCount + ", totalItemCount:" + totalItemCount + ",lastVisibleItemPosition:" + lastVisibleItemPosition);
                        if (!isLoadingMore && visibleItemCount < totalItemCount && lastVisibleItemPosition == totalItemCount - 1) {
                            isLoadingMore = true;
                            if (getAdapter() != null && getAdapter() instanceof BaseBindingAdapter) {
//                                LogT.i("加载更多:");
                                ((BaseBindingAdapter) getAdapter()).setLoadMoreEnable(true);
                                ((BaseBindingAdapter) getAdapter()).showLoadMore(true);
                            }
                            if (mLoadMoreListener != null) {
                                mLoadMoreListener.loadMore();
                            }
                        }
                    }
                }

                /**仅对LinearLayoutManager 有效，其他LayoutManager未验证*/
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                    LogT.i("dx:" +dx + ", dy:"  + dy);
                    lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

                }
            });
        }
    }
}
