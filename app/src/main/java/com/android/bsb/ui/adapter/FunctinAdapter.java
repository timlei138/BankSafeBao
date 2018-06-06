package com.android.bsb.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.bsb.R;
import com.android.bsb.util.AppLogger;
import java.util.List;

public class FunctinAdapter extends BaseAdapter {


    private Context mContext;

    private List<FunctionItem> mList;

    public FunctinAdapter(Context context,List<FunctionItem> list){
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view =  inflater.inflate(R.layout.layout_table_item,null);
        ImageView icon = view.findViewById(R.id.function_icon);
        TextView label = view.findViewById(R.id.function_label);
        label.setText(mList.get(position).getLabel());
        icon.setBackgroundResource(mList.get(position).getIconRes());
        view.setTag(mList.get(position).getClickAction());
        return view;
    }
}
