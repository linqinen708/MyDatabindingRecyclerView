package com.linqinen708.mydatabinding.recyclerview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.linqinen708.mydatabinding.recyclerview.adapter.BaseBindingAdapter;

import java.util.Collection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Created by Ian on 2019/12/27.
 * <p>
 * 这个是专门配合BaseBindingAdapter 使用的，所以默认的adapter 就是BaseBindingAdapter
 */
public class MyRefreshLayout extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener {

    private Context mContext;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private LoadMoreRecyclerView mRecyclerView;

    private BaseBindingAdapter mAdapter;

    private RefreshListener mRefreshListener;
    /**
     * 后台第一页 是 1开始 不是0
     */
    private int page = 1;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setRefreshListener(RefreshListener refreshListener) {
        mRefreshListener = refreshListener;
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void loadMore() {
                page++;
                if (mRefreshListener != null) {
                    mRefreshListener.loadMore();
                }
            }
        });
    }

    public interface RefreshListener {
        void loadMore();

        void refresh();
    }

    public MyRefreshLayout(@NonNull Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public MyRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public MyRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mSwipeRefreshLayout = new SwipeRefreshLayout(mContext);
        mSwipeRefreshLayout.setEnabled(false);
        mRecyclerView = new LoadMoreRecyclerView(mContext);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        /*因为自定义的LoadMoreListener 需要LinearLayoutManager支持
         * 所以setLoadMoreListener 需要在setLayoutManager 之后 设置
         * */


        mSwipeRefreshLayout.addView(mRecyclerView);

        addView(mSwipeRefreshLayout);
    }

    @Override
    public void onRefresh() {
        page = 1;
        if (mRefreshListener != null) {
            mRefreshListener.refresh();
        }
    }

    /**
     * 下拉刷新完成
     */
    public void completeRefresh() {
        /*如果下拉刷新后，mRecyclerView 自动加载到底部，则让其返回到顶部*/
        mRecyclerView.scrollToPosition(0);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * 上拉加载更多完成
     */
    public void completeLoadMore() {
        mRecyclerView.completeLoadMore();
    }

    public void complete() {
        completeLoadMore();
        completeRefresh();
    }

    public void completeHttpRequest(Collection collection) {
        if (mAdapter == null) {
            return;
        }
        if (page == 1) {
            mAdapter.getItems().clear();
            completeRefresh();
            mAdapter.toggleFootView(collection);
            setLoadMoreEnable(true);
        } else {
            completeLoadMore();
            setLoadMoreEnable(mAdapter.toggleLoadMore(collection));
        }
    }

    public void setAdapter(BaseBindingAdapter adapter) {
        mAdapter = adapter;
        mRecyclerView.setAdapter(adapter);
    }

    public void addItemDecoration(@NonNull RecyclerView.ItemDecoration decor) {
        mRecyclerView.addItemDecoration(decor);
    }

    public void setLoadMoreEnable(boolean loadMoreEnable) {
        mRecyclerView.setLoadMoreEnable(loadMoreEnable);
    }
}
