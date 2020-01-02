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


    /**
     * 是否可以上拉加载更多
     * 比如当暂无更多数据之后，就没必要再支持上拉加载更多了
     * 可以关闭上拉功能
     */
    private boolean isLoadMoreEnable = true;

    public boolean isLoadMoreEnable() {
        return isLoadMoreEnable;
    }

    public void setLoadMoreEnable(boolean loadMoreEnable) {
        isLoadMoreEnable = loadMoreEnable;
    }

    /**
     * 是否正在上拉加载更多，
     * 如果不做额外判断，而用户连续快速上拉，则会出现多次请求
     */
    private boolean isLoadingMore;

    public boolean isLoadingMore() {
        return isLoadingMore;
    }

    public void completeLoadMore() {
        isLoadingMore = false;
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
        initView();
    }

    public LoadMoreRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LoadMoreRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**如果不取消动画，会导致数据不一致的报错
     * IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter
     * */
    private void initView(){
        setItemAnimator(null);
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
                    if (isLoadMoreEnable && newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    LogT.i("停止滑动:");
                        int visibleItemCount = mLayoutManager.getChildCount();
                        int totalItemCount = mLayoutManager.getItemCount();
//                        LogT.i(":" + getAdapter().getItemViewType(0));
//                        LogT.i("isLoadingMore:"+isLoadingMore + ",visibleItemCount:" + visibleItemCount + ", totalItemCount:" + totalItemCount + ",lastVisibleItemPosition:" + lastVisibleItemPosition);
                        if (!isLoadingMore && visibleItemCount < totalItemCount && lastVisibleItemPosition == totalItemCount - 1) {
                            isLoadingMore = true;
                            if (getAdapter() != null && getAdapter() instanceof BaseBindingAdapter) {
                                LogT.i("加载更多:");
                                ((BaseBindingAdapter) getAdapter()).showLoadMore(true);
                            }
                            LogT.i("mLoadMoreListener:" + mLoadMoreListener);
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
                    if (isLoadMoreEnable) {
                        lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
                    }

                }
            });
        }
    }
}
