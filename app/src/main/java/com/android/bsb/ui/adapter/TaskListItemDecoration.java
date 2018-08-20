package com.android.bsb.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.bsb.ui.adapter.TaskGroupHolder;
import com.android.bsb.util.AppLogger;

public class TaskListItemDecoration extends RecyclerView.ItemDecoration {

    private String TAG = "TaskListItemDecoration";
    private Context mContext;

    private static final int[] ATTRS = new int[]{ android.R.attr.listDivider };

    private Drawable mDivider;

    private final Rect mBounds = new Rect();

    public TaskListItemDecoration(Context context){

        mContext = context;
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        if (mDivider == null) {
            Log.w(TAG, "@android:attr/listDivider was not set in the theme used for this "
                    + "DividerItemDecoration. Please set that attribute all call setDrawable()");
        }
        a.recycle();

    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        //super.onDraw(c, parent, state);

        //if(parent.getChildViewHolder())
        drawVertical(c,parent);

    }



    private void drawVertical(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final int left;
        final int right;
        //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            final int bottom = mBounds.bottom + Math.round(child.getTranslationY());
            final int top = bottom - mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
        canvas.restore();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        TaskGroupHolder holder = (TaskGroupHolder) parent.getChildViewHolder(view);
        if(holder instanceof TaskGroupHolder.ParentViewHolder){
            outRect.top = 0;
            outRect.bottom = 10;
            outRect.left = outRect.right = 5;

        }else if(holder instanceof TaskGroupHolder.ChildViewHolder){
            outRect.left = outRect.right = 10;
            outRect.top =0;
            outRect.bottom = mDivider.getIntrinsicHeight();
        }

        if (mDivider == null) {
            outRect.set(0, 0, 0, 0);
            return;
        }

       // super.getItemOffsets(outRect, view, parent, state);
    }



}
