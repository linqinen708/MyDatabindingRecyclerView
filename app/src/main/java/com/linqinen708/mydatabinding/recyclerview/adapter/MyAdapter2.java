package com.linqinen708.mydatabinding.recyclerview.adapter;

import android.content.Context;

import com.linqinen708.mydatabinding.recyclerview.R;
import com.linqinen708.mydatabinding.recyclerview.bean.MyBean;
import com.linqinen708.mydatabinding.recyclerview.bean.viewmodel.MyViewModel;
import com.linqinen708.mydatabinding.recyclerview.databinding.AdapterLayoutBinding;

/**
 * Created by Ian on 2019/12/25.
 */
public class MyAdapter2 extends BaseBindingAdapter<MyBean, AdapterLayoutBinding> {
    public MyAdapter2(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.adapter_layout;
    }


    @Override
    protected void onBindItem(AdapterLayoutBinding binding, MyBean bean, int position) {
        binding.setBean(bean);
        binding.setViewModel(new MyViewModel(bean));
    }
}
