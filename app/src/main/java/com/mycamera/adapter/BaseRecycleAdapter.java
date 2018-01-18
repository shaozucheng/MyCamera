package com.mycamera.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者： chengcheng
 * 时间： 18/1/17- 下午3:12
 * 描述： RecycleView 适配器基类
 */

public abstract class BaseRecycleAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<T> mDatas = new ArrayList<>();
    protected OnItemClickListener mOnItemClickListener;
    protected Context mContext;

    public BaseRecycleAdapter(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public List<T> getDatas() {
        return mDatas;
    }

    public void setDatas(List<T> datas) {
        mDatas = datas;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final int pos = holder.getLayoutPosition();
        final T data = mDatas.get(pos);
        onBind(holder, pos, data);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemLongClick(holder.itemView, pos);
                }
                //表示此事件已经消费，不会触发单击事件
                return true;
            }
        });
    }

    public abstract void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, T data);
}
