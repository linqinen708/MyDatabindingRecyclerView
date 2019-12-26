package com.linqinen708.mydatabinding.recyclerview.bean.viewmodel;

import android.view.View;

import com.linqinen708.mydatabinding.recyclerview.bean.MyBean;

/**
 * Created by Ian on 2019/12/25.
 */
public class MyViewModel {

    private MyBean mBean;

    public MyViewModel(MyBean bean) {
        mBean = bean;
    }

    public int isVisible() {
        return mBean.isChecked() ? View.VISIBLE : View.GONE;
    }
}
