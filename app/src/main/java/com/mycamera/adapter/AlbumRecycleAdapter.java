package com.mycamera.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mycamera.R;
import com.mycamera.cameralibrary.ImageData;
import com.mycamera.cameralibrary.ImageLoader;

/**
 * 作者： chengcheng
 * 时间： 18/1/17- 下午4:01
 * 描述：
 */

public class AlbumRecycleAdapter extends BaseRecycleAdapter<ImageData> {

    public AlbumRecycleAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, ImageData data) {
        AlbumViewHolder albumViewHolder = (AlbumViewHolder) viewHolder;
        albumViewHolder.bindViews(data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.grid_item, parent, false);
        return new AlbumViewHolder(view);
    }

    public class AlbumViewHolder extends BaseRecycleViewHolder<ImageData> {

        private ImageView mImageView;
        private ImageButton mImageButton;

        public AlbumViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.id_item_image);
            mImageButton = itemView.findViewById(R.id.id_item_select);
        }

        @Override
        public void bindViews(ImageData object) {
            //设置图片
            final String url = object.path;
            if (!TextUtils.isEmpty(url)) {
                Glide.with(getContext()).load(url).into(mImageView);
            }
            /**
             * 已经选择过的图片，显示出选择过的效果
             */
            if (object.isSelect) {
                mImageButton.setImageResource(R.drawable.pictures_selected);
                mImageView.setColorFilter(Color.parseColor("#77000000"));
            } else {
                mImageButton.setImageResource(R.drawable.picture_unselected);
                mImageView.setColorFilter(null);
            }
        }
    }
}
