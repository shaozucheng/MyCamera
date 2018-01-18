package com.mycamera.adapter;

import android.view.View;

/**
 * 作者： chengcheng
 * 时间： 18/1/17- 下午2:53
 * 描述：
 */

public interface OnItemClickListener {
    void onItemClick(View view, int position);

    void onItemLongClick(View view, int position);
}
