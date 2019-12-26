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

public class LoadMoreActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private LoadMoreRecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;

    private MyAdapter mAdapter;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_more);

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = findViewById(R.id.recycler_view);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

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
        mAdapter.getItems().add(new MyBean("张三", 25, R.drawable.ic_launcher_foreground, true));
        mAdapter.getItems().add(new MyBean("李四", 21, R.drawable.ic_launcher_background, true));
        mAdapter.getItems().add(new MyBean("王五", 35, R.mipmap.ic_launcher, true));
        mAdapter.getItems().add(new MyBean("赵六", 48, R.mipmap.ic_launcher_round, true));
        mAdapter.getItems().add(new MyBean("横七", 48, R.mipmap.ic_launcher_round, true));
//        mAdapter.getItems().add(new MyBean("竖八", 48, R.mipmap.ic_launcher_round, true));

    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.clearData();
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
                    /*如果已经没有数据后，不想继续触发 加载更多，则关闭功能*/
                    mAdapter.setLoadMoreEnable(false);
                } else {
                    /*允许加载更多*/
                    mAdapter.setLoadMoreEnable(true);

                    mAdapter.getItems().add(new MyBean("谷歌" + mAdapter.getItems().size(), 9999, R.mipmap.ic_launcher_round, true));
                }
                mRecyclerView.setLoadingMore(false);
            }
        }, 1000);
    }
}
