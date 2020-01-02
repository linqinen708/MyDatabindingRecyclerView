package com.linqinen708.mydatabinding.recyclerview;

import android.os.Bundle;
import android.os.Handler;

import com.linqinen708.mydatabinding.recyclerview.adapter.MyAdapter;
import com.linqinen708.mydatabinding.recyclerview.bean.MyBean;
import com.linqinen708.mydatabinding.recyclerview.utils.LogT;
import com.linqinen708.mydatabinding.recyclerview.widget.MyRefreshLayout;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 直接将 LoadMoreRecyclerView 和 SwipeRefreshLayout 合二为一
 * 组建成 具备下拉刷新和上拉加载更多功能的MyRefreshLayout
 */
public class Main3Activity extends AppCompatActivity {

    private MyRefreshLayout mRefreshLayout;

    private MyAdapter mAdapter;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        mRefreshLayout = findViewById(R.id.refresh_layout);
        mAdapter = new MyAdapter(this);
        initData();
        initRefreshLayout();
    }

    private void initRefreshLayout() {
        mRefreshLayout.setAdapter(mAdapter);

        mRefreshLayout.setRefreshListener(new MyRefreshLayout.RefreshListener() {
            @Override
            public void loadMore() {
                httpRequest();
            }

            @Override
            public void refresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.getItems().clear();
                        mAdapter.showFooterView(false);
                        initData();
                        mRefreshLayout.completeRefresh();
                    }
                }, 500);
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
                /*加载成功之后，需要completeLoadMore，完成加载更多操作*/
                mRefreshLayout.completeLoadMore();
            }
        }, 1000);
    }
}
