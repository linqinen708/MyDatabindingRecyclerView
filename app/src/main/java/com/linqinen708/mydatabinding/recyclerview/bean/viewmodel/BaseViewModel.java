package com.linqinen708.mydatabinding.recyclerview.bean.viewmodel;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

/**
 * Created by Ian on 2019/11/22.
 */
public class BaseViewModel {

    @BindingAdapter({"app:imgRes"})
    public static void setImageView(ImageView imageView, int res) {
        imageView.setImageResource(res);
    }
    /**如果需要网络加载图片，可以自己加载Glide包*/
//    @BindingAdapter({"app:imgUrl"})
//    public static void setImageView(ImageView imageView, String url) {
//        if(TextUtils.isEmpty(url)){
//            return;
//        }
//        Glide.with(imageView.getContext()).load(url).into(imageView);
//    }
}
