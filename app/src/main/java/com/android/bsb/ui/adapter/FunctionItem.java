package com.android.bsb.ui.adapter;

import android.graphics.drawable.Drawable;

public class FunctionItem {

    private int labelRes;

    private int iconRes;

    private int clickAction;

    public FunctionItem(int label,int res,int action){
        this.labelRes = label;
        iconRes = res;
        clickAction = action;
    }

    public FunctionItem(int res,int action){
        iconRes = res;
        clickAction = action;
    }

    public int getClickAction() {
        return clickAction;
    }

    public void setClickAction(int clickAction) {
        this.clickAction = clickAction;
    }

    public int getLabel() {
        return labelRes;
    }

    public void setLabel(int label) {
        this.labelRes = label;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }
}
