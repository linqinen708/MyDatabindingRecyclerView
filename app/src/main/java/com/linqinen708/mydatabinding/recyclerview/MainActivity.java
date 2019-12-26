package com.linqinen708.mydatabinding.recyclerview;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.linqinen708.mydatabinding.recyclerview.adapter.BaseBindingAdapter;
import com.linqinen708.mydatabinding.recyclerview.adapter.MyAdapter;
import com.linqinen708.mydatabinding.recyclerview.bean.MyBean;
import com.linqinen708.mydatabinding.recyclerview.utils.LogT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * 上拉加载参考资料  https://blog.csdn.net/weixin_37577039/article/details/79214663
 */
public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;

    private MyAdapter mAdapter;

    private Handler mHandler = new Handler();

    private int lastVisibleItemPosition;

    /**
     * 是否正在上拉加载更多
     */
    private boolean isLoadingMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = findViewById(R.id.recycler_view);

        initRecyclerView();

    }

    private void initRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(this);

        initData();

        mRecyclerView.setAdapter(mAdapter);

        /*item的点击事件*/
        mAdapter.setOnItemClickListener(new BaseBindingAdapter.OnItemClickListener<MyBean>() {
            @Override
            public void onItemClick(MyBean bean, int position) {
                Toast.makeText(getBaseContext(), "点击第" + position + "个", Toast.LENGTH_SHORT).show();
            }
        });

        initLoadMoreListener();
    }

    private void initLoadMoreListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    LogT.i("停止滑动:");
                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    LogT.i("visibleItemCount:" + visibleItemCount + ", totalItemCount:" + totalItemCount + ",lastVisibleItemPosition:" + lastVisibleItemPosition);
                    if (!isLoadingMore && lastVisibleItemPosition == totalItemCount - 1) {
                        isLoadingMore = true;
                        mAdapter.showLoadMore(true);
                        httpRequest();
                    }
                }
            }

            /**仅对LinearLayoutManager 有效，其他LayoutManager未验证*/
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

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
                } else {
//                    mAdapter.showFootView(false);
                    mAdapter.getItems().add(new MyBean("谷歌" + mAdapter.getItems().size(), 9999, R.mipmap.ic_launcher_round, true));
                }
                isLoadingMore = false;
            }
        }, 1000);
    }
}
