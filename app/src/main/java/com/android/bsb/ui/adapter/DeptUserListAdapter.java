package com.android.bsb.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.bsb.R;
import com.android.bsb.bean.User;
import com.android.bsb.util.AppLogger;
import com.android.bsb.util.Utils;
import com.android.bsb.widget.LeftSlideView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeptUserListAdapter extends RecyclerView.Adapter<DeptUserListAdapter.LeftSlideViewHolder> implements LeftSlideView.IonSlidingButtonListener{


    private Context mContext;
    private List<User> mUserList = new ArrayList<>();

    private LeftSlideView mMenu = null;

    private IonSlidingViewClickListener mListener;

    private boolean mShowCheckBox = false;

    private List<User> mSelectedList = new ArrayList<>();

    public DeptUserListAdapter(Context context){
        mContext = context;
    }


    public void setData(List<User> list){
        mUserList = list;
        notifyDataSetChanged();
    }

    public void setShowCheckBox(boolean value){
        mShowCheckBox = value;
    }

    public void addData(List<User> list){
        int position = mUserList.size();
        mUserList.addAll(list);
        notifyItemChanged(position);
    }

    public List<User> getSelectedList(){
        return mSelectedList;
    }

    public void setSelectedList(List list){
        mSelectedList.clear();
        mSelectedList = list;
    }

    public void setListener(IonSlidingViewClickListener listener){
        mListener = listener;
    }


    @Override
    public LeftSlideViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_deptuser_item,null);
        LeftSlideViewHolder holder = new LeftSlideViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(LeftSlideViewHolder holder, final int position) {
        int screenwidth = Utils.getScreenWidth(mContext);
        holder.mContentLaoyut.getLayoutParams().width = screenwidth;
        holder.mDeleteBtn.getLayoutParams().width = screenwidth / 5;
        holder.mUpdateBtn.getLayoutParams().width = screenwidth / 5;
        holder.mDetailBtn.getLayoutParams().width = screenwidth / 5;
        final User user = mUserList.get(position);
        AppLogger.LOGD("demo","size:"+mSelectedList.size()+",contains:"+mSelectedList.contains(user));
        holder.initLayout(user,mShowCheckBox,mSelectedList.contains(user));
        holder.mContentLaoyut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否有删除菜单打开
                if (menuIsOpen()) {
                    closeMenu();//关闭菜单
                } else {
                    if(mListener!=null)
                        mListener.onItemClick(user, position);
                }
            }
        });

        holder.mDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!=null)
                    mListener.onDetailBtn(user,position);
            }
        });

        holder.mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!=null)
                    mListener.onUpdateBtn(user,position);
            }
        });

        holder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!=null)
                    mListener.onDeleteBtnCilck(user,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }


    /**
     * 删除item
     * @param position
     */
    public void removeData(int position) {
        mUserList.remove(position);
        notifyItemRemoved(position);
    }



    /**
     * 删除菜单打开信息接收
     */
    @Override
    public void onMenuIsOpen(View view) {
        mMenu = (LeftSlideView) view;
    }


    /**
     * 滑动或者点击了Item监听
     *
     * @param leftSlideView
     */
    @Override
    public void onDownOrMove(LeftSlideView leftSlideView) {
        if (menuIsOpen()) {
            if (mMenu != leftSlideView) {
                closeMenu();
            }
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        mMenu.closeMenu();
        mMenu = null;

    }

    /**
     * 判断菜单是否打开
     *
     * @return
     */
    public Boolean menuIsOpen() {
        if (mMenu != null) {
            return true;
        }
        return false;
    }


    /**
     * 注册接口的方法：点击事件。在Mactivity.java实现这些方法。
     */
    public interface IonSlidingViewClickListener {
        void onItemClick(User user, int position);//点击item正文

        void onDeleteBtnCilck(User user, int position);

        void onUpdateBtn(User user, int position);

        void onDetailBtn(User user,int positon);
    }



    public  class LeftSlideViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.selectbox)
        CheckBox mCheckBox;
        @BindView(R.id.item_delete)
        TextView mDeleteBtn;
        @BindView(R.id.item_detail)
        TextView mDetailBtn;
        @BindView(R.id.item_update)
        TextView mUpdateBtn;
        @BindView(R.id.item_deptinfo)
        TextView mDeptInfo;
        @BindView(R.id.item_uname)
        TextView mUname;
        @BindView(R.id.content)
        LinearLayout mContentLaoyut;

        public LeftSlideViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            ((LeftSlideView) itemView).setSlidingButtonListener(DeptUserListAdapter.this);
        }


        public void initLayout(final User user, boolean showSelect, boolean ischecked){
            mUname.setText(user.getUname());
            mDeptInfo.setText(user.getDeptName());
            mCheckBox.setVisibility(showSelect ? View.VISIBLE : View.GONE);
            if(showSelect){
                mCheckBox.setChecked(ischecked);
                mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //Integer uid = Integer.valueOf(user.getUid());
                        if(mSelectedList.contains(user)){
                            mSelectedList.remove(user);
                        }else{
                            mSelectedList.add(user);
                        }
                    }
                });
            }
        }
    }
}


