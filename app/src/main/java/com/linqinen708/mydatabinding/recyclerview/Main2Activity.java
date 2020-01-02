package com.linqinen708.mydatabinding.recyclerview;

import android.os.Bundle;
import android.os.Handler;

import com.linqinen708.mydatabinding.recyclerview.adapter.MyAdapter;
import com.linqinen708.mydatabinding.recyclerview.bean.MyBean;
import com.linqinen708.mydatabinding.recyclerview.utils.LogT;
import com.linqinen708.mydatabinding.recyclerview.widget.LoadMoreRecyclerView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * 该Activity 只是 对RecyclerView 进行封装
 * 让其具备上拉加载更多功能的LoadMoreRecyclerView
 */
public class Main2Activity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private LoadMoreRecyclerView mRecyclerView;

    private MyAdapter mAdapter;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new MyAdapter(this);

        initData();

        mRecyclerView.setAdapter(mAdapter);

        /**因为自定义的LoadMoreListener 需要LinearLayoutManager支持
         * 所以setLoadMoreListener 需要在setLayoutManager 之后 设置
         * */
        mRecyclerView.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void loadMore() {
                httpRequest();
            }
        });

    }

    private void initData() {
        mAdapter.getItems().add(new MyBean("谷歌1", 25, R.drawable.ic_launcher_foreground, true));
        mAdapter.getItems().add(new MyBean("谷歌2", 21, R.drawable.ic_launcher_background, true));
        mAdapter.getItems().add(new MyBean("谷歌3", 35, R.mipmap.ic_launcher, true));
        mAdapter.getItems().add(new MyBean("谷歌4", 48, R.mipmap.ic_launcher_round, true));
        mAdapter.getItems().add(new MyBean("谷歌5", 48, R.mipmap.ic_launcher_round, false));
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.getItems().clear();
                initData();
                /*如果下拉刷新后，mRecyclerView 自动加载到底部，则让其返回到顶部*/
                mRecyclerView.scrollToPosition(0);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1500);

    }

    /**
     * 网络请求数据
     */
    private void httpRequest() {
        LogT.i("请求数据");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAdapter.getItems().size() > 7) {
                    LogT.i("暂无数据:");
                    mAdapter.showLoadMore(false);
                } else {
                    mAdapter.getItems().add(new MyBean("谷歌" + (mAdapter.getItems().size() + 1), 9999, R.mipmap.ic_launcher_round, true));
                }
                mRecyclerView.completeLoadMore();
            }
        }, 1000);
    }
}
