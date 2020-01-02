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
 * Created by Ian on 2019/12/26.
 */

public abstract class BaseBindingAdapter<M, B extends ViewDataBinding> extends RecyclerView.Adapter {
    protected Context mContext;
    protected ObservableArrayList<M> items;
    private ListChangedCallback itemsChangeCallback;

    private boolean isShowFooterView = false;
    /**
     * 是否正在 加载更多
     */
    private boolean isLoadingMore = false;

    /**
     * 判断viewType 是不是FooterView
     */
    protected static final int TYPE_FOOTER_VIEW = 1;
    /**
     * 判断viewType 是不是HeaderView
     */
    protected static final int TYPE_HEADER_VIEW = 2;

    /**
     * 改变底部footView状态
     *
     * @param showFooterView false 表示不展示 暂无更多数据 和 正在加载。。。
     */
    public void showFooterView(boolean showFooterView) {
        if (isShowFooterView == showFooterView) {
            LogT.i("未改变状态直接返回showFootView：" + showFooterView);
            return;
        }
        isShowFooterView = showFooterView;
        notifyItemChanged(getItemCount() - 1);
    }

    /**
     * @param isLoadingMore 表示是否加载更多
     *                      false 暂无更多数据
     *                      true 正在加载...
     */
    public void showLoadMore(boolean isLoadingMore) {
        LogT.i("isLoadingMore:" + isLoadingMore);
        if (this.isLoadingMore == isLoadingMore) {
            LogT.i("未改变状态直接返回isLoadingMore：" + isLoadingMore);
            return;
        }
        isShowFooterView = true;
        this.isLoadingMore = isLoadingMore;
        notifyItemChanged(getItemCount() - 1);

    }

    public boolean toggleLoadMore(Collection<? extends M> collection) {
        if (collection != null) {
            if (collection.isEmpty()) {
                showLoadMore(false);
                return false;
            } else {
                showLoadMore(true);
                items.addAll(collection);
                return true;
            }
        } else {
            showLoadMore(true);
            return true;
        }
    }

    /**
     * 根据数据自动显示 footView（暂无更多数据）
     */
    public void toggleFootView(Collection<? extends M> collection) {
        if (collection != null) {
            if (collection.isEmpty()) {
                showFooterView(true);
            } else {
                showFooterView(false);
                items.addAll(collection);
            }
        } else {
            showFooterView(true);
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

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View itemView) {
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
        if (isShowFooterView) {
            if (position == items.size()) {
                return R.layout.adapter_default_footer_view;
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
        if (isShowFooterView) {
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
        if (isShowFooterView) {
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
            return new FooterViewHolder(LayoutInflater.from(mContext).inflate(
                    getFooterViewLayoutResId() == 0 ? R.layout.adapter_default_footer_view : getFooterViewLayoutResId(),
                    parent, false));
        } else if (viewType == TYPE_HEADER_VIEW) {
            if (getHeaderViewLayoutResId() != 0) {
                ViewDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), getHeaderViewLayoutResId(), parent, false);
                return new HeaderViewHolder(viewDataBinding.getRoot());
            }
            return new HeaderViewHolder(LayoutInflater.from(mContext).inflate(getHeaderViewLayoutResId(), parent, false));
        } else {
            B binding = DataBindingUtil.inflate(LayoutInflater.from(this.mContext), getLayoutResId(), parent, false);
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
            } else if (holder instanceof FooterViewHolder) {
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
    protected abstract int getLayoutResId();

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
    protected int getFooterViewLayoutResId() {
        return 0;
    }

    protected abstract void onBindItem(B binding, M bean, int position);
//    protected abstract void onBindItem(B binding, M bean);

    /**
     * 额外监听数据变化，这样就可以直接更新数据，
     * 而不用每次都调用notifyDataSetChanged()等方式更新数据
     * <p>
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
//            LogT.i("onItemRangeInserted:" + positionStart + "--" + itemCount + "," + newItems.toString());
            /*当有头部信息的时候，由于整体position偏移了，所以需要往后挪一位*/
            if (getHeaderViewLayoutResId() != 0) {
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
//            if (itemCount > 1) {
//
//                /*当有头部信息的时候，由于整体position偏移了，所以需要往后挪一位*/
            if (getHeaderViewLayoutResId() != 0) {
                positionStart++;
            }

            notifyItemRangeRemoved(positionStart, itemCount);
        }
    }
}
