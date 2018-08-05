package com.android.bsb.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.bsb.R;
import com.android.bsb.bean.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PendingUserAdapter extends BaseAdapter {


    private Context mContext;
    private ArrayList<User> mPendingUserList = new ArrayList<>();

    private LayoutInflater mLayoutInflater;

    private OnAddUserClick mListener;

    private int screenHeight;

    public PendingUserAdapter(Context context,OnAddUserClick listener){
        mContext = context;
        mListener = listener;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
    }

    public ArrayList<User> getPendingUserList(){
        return mPendingUserList;
    }

    public void setPendingList(List<User> list){
        mPendingUserList.clear();
        mPendingUserList.addAll(list);
        notifyDataSetChanged();
    }

    public void addPendItem(User user){
        mPendingUserList.add(user);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mPendingUserList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPendingUserList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.pending_user_item,null);
            convertView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,screenHeight/8));
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        User user = mPendingUserList.get(position);
        if(user.getUid() == -1){
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_add_36);
            drawable.setBounds(0,0,drawable.getIntrinsicHeight(),drawable.getIntrinsicWidth());
            holder.mUserName.setCompoundDrawables(null,drawable,null,null);
            holder.mDelBtn.setVisibility(View.GONE);
            holder.mUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener!=null){
                        mListener.onAddUser();
                    }

                }
            });

        }else{
            holder.mUserName.setOnClickListener(null);
            holder.mUserName.setCompoundDrawables(null,null,null,null);
            holder.mDelBtn.setVisibility(View.VISIBLE);
        }

        holder.mUserName.setText(user.getUname());
        holder.mDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPendingUserList.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }


    public interface OnAddUserClick{
        void onAddUser();
    }


    class ViewHolder{
        @BindView(R.id.item_uname)
        TextView mUserName;
        @BindView(R.id.item_del)
        ImageView mDelBtn;

        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }
}
