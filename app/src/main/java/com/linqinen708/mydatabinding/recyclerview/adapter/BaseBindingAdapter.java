package com.linqinen708.mydatabinding.recyclerview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linqinen708.mydatabinding.recyclerview.R;
import com.linqinen708.mydatabinding.recyclerview.utils.LogT;

import java.util.Collection;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by Ian on 2019/10/16.
 */

public abstract class BaseBindingAdapter<M, B extends ViewDataBinding> extends RecyclerView.Adapter {
    protected Context mContext;
    protected ObservableArrayList<M> items;
    private ListChangedCallback itemsChangeCallback;

    private boolean isShowFootView = false;
    /**
     * 是否正在 加载更多
     */
    private boolean isLoadingMore = false;
    /**
     * 是否可以加载更多
     */
    private boolean loadMoreEnable = true;

    /**
     * 是否支持加载更多
     */
    public void setLoadMoreEnable(boolean loadMoreEnable) {
        this.loadMoreEnable = loadMoreEnable;
    }

    /**
     * 判断viewType 是不是FooterView
     */
    protected static final int TYPE_FOOTER_VIEW = 1;
    /**
     * 判断viewType 是不是HeaderView
     */
    protected static final int TYPE_HEADER_VIEW = 2;


    /**
     * 清除所有数据之后，要把footview 也关闭掉
     * 回到最初始状态
     */
    public void clearData() {
        items.clear();
//        isShowFootView = false;
    }

    /**
     * 改变底部footView状态
     * @param showFootView false 表示不展示 暂无更多数据 和 正在加载。。。
     */
    public void showFootView(boolean showFootView) {
        if (isShowFootView == showFootView) {
            LogT.i("未改变状态直接返回showFootView："+showFootView);
            return;
        }
        isShowFootView = showFootView;
    }

    /**
     * @param isLoadingMore 表示是否加载更多
     *                      false 暂无更多数据
     *                      true 正在加载...
     */
    public void showLoadMore(boolean isLoadingMore) {
//        LogT.i("isLoadingMore:" + isLoadingMore);
        if (this.isLoadingMore == isLoadingMore) {
            LogT.i("未改变状态直接返回isLoadingMore："+isLoadingMore);
            return;
        }
        isShowFootView = true;
        this.isLoadingMore = isLoadingMore;
        if (loadMoreEnable) {
            notifyItemChanged(getItemCount() - 1);
        }else{
            LogT.i("未启动加载更多功能");
        }
    }

    /**
     * 根据数据自动显示 footView（暂无更多数据）
     */
    public void toggleLoadMore(Collection<? extends M> collection) {
        if (collection != null) {
            if (collection.isEmpty()) {
                showLoadMore(false);
            } else {
                showLoadMore(true);
                items.addAll(collection);
            }
        } else {
            showLoadMore(true);
        }
    }

    public void toggleLoadMore(Collection<? extends M> collection, boolean isRefreshHeaderView) {
        if (isRefreshHeaderView) {
            refreshHeaderViewData();
        }
        toggleLoadMore(collection);
    }

    /**
     * 当items 没有数据的时候，如果需要动态设置HeaderView的数据
     * 需要刷新，否则数据无法展示出来
     */
    public void refreshHeaderViewData() {
        if (getHeaderViewLayoutResId() != 0 && items.isEmpty()) {
            LogT.i("刷新头部:");
            notifyItemChanged(0);//这样刷新头部居然没有效果
//            notifyDataSetChanged();
        }
    }

    public BaseBindingAdapter(Context context) {
        this.mContext = context;
        this.items = new ObservableArrayList<>();
        this.itemsChangeCallback = new ListChangedCallback();
    }

    public ObservableArrayList<M> getItems() {
        return items;
    }

    public static class BaseBindingViewHolder extends RecyclerView.ViewHolder {
        public BaseBindingViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class FootViewHolder extends RecyclerView.ViewHolder {
        public FootViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    protected OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener<M> mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;

    }

    public interface OnItemClickListener<M> {
        void onItemClick(M bean, int position);
    }


    /**
     * 重写id可以使刷新不闪烁
     */
    @Override
    public long getItemId(int position) {
        if (getHeaderViewLayoutResId() != 0) {
            if (position == 0) {
                return getHeaderViewLayoutResId();
            } else {
                position--;
            }
        }
        if (isShowFootView) {
            if (position == items.size()) {
                return R.layout.adapter_foot_view;
            } else {
                position--;
            }
        }
        /*当已经出现footer的时候，如果又上划到顶部
         * itemPosition 会出现-1的情况，应该修正为0
         * 也就是item的第一个值
         * */
        if (position < 0) {
            position = 0;
        }
        if (position >= items.size()) {
            LogT.e("越界了，position：" + position + ",mList.size():" + items.size());
            return System.currentTimeMillis();
        }
        return items.get(position).hashCode();
    }

    /**
     * 每次让items + 1 多一个footView
     */
    @Override
    public int getItemCount() {

        int count = items.size();

        /*如果有HeaderView,这数量+1*/
        if (getHeaderViewLayoutResId() != 0) {
            count++;
        }
        /*如果有FooterView,数量+1*/
        if (isShowFootView) {
            return ++count;
        } else {
            return count;
        }

    }

    @Override
    public int getItemViewType(int position) {
        /*如果是第一个item，则判断是否是headerView*/
        if (position == 0 && getHeaderViewLayoutResId() != 0) {
            return TYPE_HEADER_VIEW;
        }

        if (getHeaderViewLayoutResId() != 0 && position > 0) {
            position--;
        }

//        LogT.i("getItemViewType:"+position );
        if (isShowFootView) {
            if (position == items.size()) {
                return TYPE_FOOTER_VIEW;
            }
            /*super.getItemViewType(position) 实际上默认返回值就是0*/
            return super.getItemViewType(position);
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        LogT.i("viewType:" + viewType + ",getHeaderViewLayoutResId():" + getHeaderViewLayoutResId());
        if (viewType == TYPE_FOOTER_VIEW) {
//            LogT.i("加载footview");
            return new FootViewHolder(LayoutInflater.from(mContext).inflate(
                    getFootViewLayoutResId() == 0 ? R.layout.adapter_foot_view : getFootViewLayoutResId(),
                    parent, false));
        } else if (viewType == TYPE_HEADER_VIEW) {
            if (getHeaderViewLayoutResId() != 0) {
                ViewDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), getHeaderViewLayoutResId(), parent, false);
                return new HeaderViewHolder(viewDataBinding.getRoot());
            }
            return new HeaderViewHolder(LayoutInflater.from(mContext).inflate(getHeaderViewLayoutResId(), parent, false));
        } else {
            B binding = DataBindingUtil.inflate(LayoutInflater.from(this.mContext), this.getLayoutResId(viewType), parent, false);
            return new BaseBindingViewHolder(binding.getRoot());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        try {
            if (holder instanceof BaseBindingViewHolder) {

                if (getHeaderViewLayoutResId() != 0 && position > 0) {
                    position--;
                }
                final M bean = items.get(position);
                B binding = DataBindingUtil.getBinding(holder.itemView);
                if (binding != null) {
                    if (mOnItemClickListener != null) {
                        binding.getRoot().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mOnItemClickListener.onItemClick(bean, holder.getAdapterPosition());
                            }
                        });
                    }
                }
                this.onBindItem(binding, bean, position);
//                this.onBindItem(binding, this.items.get(position));
            } else if (holder instanceof FootViewHolder) {
                TextView tvFootView = holder.itemView.findViewById(R.id.tv_foot_view);
                if (isLoadingMore) {
                    tvFootView.setText("正在加载...");
                } else {
                    tvFootView.setText("暂无更多数据");
                }
//                LogT.i("isLoadingMore:" + isLoadingMore + "," + tvFootView.getText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.items.addOnListChangedCallback(itemsChangeCallback);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.items.removeOnListChangedCallback(itemsChangeCallback);
    }


    protected void resetItems(ObservableArrayList<M> newItems) {
        this.items = newItems;
    }

    //endregion
    @LayoutRes
    protected abstract int getLayoutResId(int viewType);

    /**
     * 使用HeaderView，默认为0，表示没有，如果有，则重写该方法，返回R.layout.xxx
     */
    @LayoutRes
    protected int getHeaderViewLayoutResId() {
        return 0;
    }

    /**
     * 使用FootView，默认为0，表示没有，如果有，则重写该方法，返回R.layout.xxx
     */
    @LayoutRes
    protected int getFootViewLayoutResId() {
        return 0;
    }

    protected abstract void onBindItem(B binding, M bean, int position);
//    protected abstract void onBindItem(B binding, M bean);

    /**
     * 额外监听数据变化，这样就可以直接更新数据，
     * 而不用每次都调用notifyDataSetChanged()等方式更新数据
     *
     * 我只对插入和移除做了监听处理，其他方式没有处理
     * 小伙伴可以根据自己的具体需要做额外处理
     */
    private class ListChangedCallback extends ObservableArrayList.OnListChangedCallback<ObservableArrayList<M>> {
        @Override
        public void onChanged(ObservableArrayList<M> newItems) {
            LogT.i("onChanged:" + newItems.toString());

        }

        @Override
        public void onItemRangeChanged(ObservableArrayList<M> newItems, int positionStart, int itemCount) {
            LogT.i("onItemRangeChanged:" + positionStart + "--" + itemCount + "," + newItems.toString());
        }

        @Override
        public void onItemRangeInserted(ObservableArrayList<M> newItems, int positionStart, int itemCount) {
            LogT.i("onItemRangeInserted:" + positionStart + "--" + itemCount + "," + newItems.toString());
            /*当有头部信息的时候，由于整体position便宜了，所以需要往后挪一位*/
            if(getHeaderViewLayoutResId() !=0){
                positionStart++;
            }
            notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(ObservableArrayList<M> newItems, int fromPosition, int toPosition, int itemCount) {
            LogT.i("onItemRangeMoved:" + fromPosition + "--" + toPosition + ",itemCount:" + itemCount + "," + newItems.toString());
        }

        @Override
        public void onItemRangeRemoved(ObservableArrayList<M> newItems, int positionStart, int itemCount) {
            LogT.i("onItemRangeRemoved:" + positionStart + "--" + itemCount + "," + newItems.toString());
            /*当有头部信息的时候，由于整体position便宜了，所以需要往后挪一位*/
            if(getHeaderViewLayoutResId() !=0){
                positionStart++;
            }
            notifyItemRangeRemoved(positionStart, itemCount);
        }
    }
}
