package com.android.bsb.ui.tasklist;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.bsb.ui.adapter.BaseViewHolder;
import com.android.bsb.util.AppLogger;

public class TaskListItemDecoration extends RecyclerView.ItemDecoration {

    public TaskListItemDecoration(){


    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        BaseViewHolder holder = (BaseViewHolder) parent.getChildViewHolder(view);

        if(holder instanceof BaseViewHolder.ParentViewHolder){

            outRect.top = 0;
            outRect.bottom = 10;
            outRect.left = outRect.right = 5;


        }else if(holder instanceof BaseViewHolder.ChildViewHolder){
            outRect.left = outRect.right = 10;
            outRect.top =0;
            outRect.bottom = 5;
        }

       // super.getItemOffsets(outRect, view, parent, state);
    }
}
