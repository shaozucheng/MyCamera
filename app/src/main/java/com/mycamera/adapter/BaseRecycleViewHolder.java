package com.mycamera.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @param <T>
 */
public abstract class BaseRecycleViewHolder<T> extends RecyclerView.ViewHolder {


    /**
     * Constructor
     *
     * @param itemView Item Root View
     */
    public BaseRecycleViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * 绑定ItemView内容
     *
     * @param object BaseData泛型
     */
    public abstract void bindViews(T object);


}
