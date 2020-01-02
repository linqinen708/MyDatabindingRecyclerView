package com.linqinen708.mydatabinding.recyclerview.adapter;

import android.content.Context;

import com.linqinen708.mydatabinding.recyclerview.R;
import com.linqinen708.mydatabinding.recyclerview.bean.MyBean;
import com.linqinen708.mydatabinding.recyclerview.bean.viewmodel.MyViewModel;
import com.linqinen708.mydatabinding.recyclerview.databinding.AdapterHeadViewLayoutBinding;
import com.linqinen708.mydatabinding.recyclerview.databinding.AdapterLayoutBinding;
import com.linqinen708.mydatabinding.recyclerview.utils.LogT;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Ian on 2019/12/25.
 */
public class MyAdapter extends BaseBindingAdapter<MyBean, AdapterLayoutBinding> {
    public MyAdapter(Context context) {
        super(context);
    }

    /**头部HeaderView，如果没有，则不用重写方法*/
    @Override
    protected int getHeaderViewLayoutResId() {
        return R.layout.adapter_head_view_layout;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.adapter_layout;
    }

    /**
     * 如果需要动态改动HeaderView，需要重写onBindViewHolder方法
     * super.onBindViewHolder(holder, position) 不要删掉
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof HeaderViewHolder) {
            AdapterHeadViewLayoutBinding binding = DataBindingUtil.getBinding(holder.itemView);
            LogT.i("设置HeaderView");
            if (binding != null) {
                binding.tvHeaderView.setText("大家好，我是HeaderView");
//                binding.tvTime.getPaint().setFakeBoldText(true);
            }
        }
    }

    @Override
    protected void onBindItem(AdapterLayoutBinding binding, MyBean bean, int position) {
        binding.setBean(bean);
        binding.setViewModel(new MyViewModel(bean));
    }
}
