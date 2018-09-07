package com.android.bsb.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.bsb.GlideApp;

import java.util.ArrayList;
import java.util.List;

public class PreViewAdapter extends PagerAdapter {


    public Context mContext;

    private List<String> images = new ArrayList<>();

    private List<ImageView> imageViews = new ArrayList<>();

    private Callback mCallback;

    public PreViewAdapter(Context context,List list){
        mContext = context;
        images = list;

        for (int i =0;i < images.size();i++){
            ImageView imageView = new ImageView(mContext);
            GlideApp.with(mContext).asDrawable().load(images.get(i)).fitCenter().into(imageView);
            imageViews.add(imageView);
        }
    }


    public void setCallback(Callback callback){
        this.mCallback = callback;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return  view == object;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        ImageView view = imageViews.get(position);
        if(mCallback!=null){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onClick();
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mCallback.onOptions(position);
                    return true;
                }
            });
        }
        container.addView(view);
        return view;
    }



    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //super.destroyItem(container, position, object);
        container.removeView(imageViews.get(position));
    }

    public interface Callback{
        void onClick();
        void onOptions(int position);
    }
}
