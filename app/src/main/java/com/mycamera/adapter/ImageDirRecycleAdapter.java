package com.mycamera.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycamera.R;
import com.mycamera.cameralibrary.ImageFolder;
import com.mycamera.cameralibrary.ImageLoader;
import com.mycamera.util.ImageEnviromentUtil;


/**
 * 作者： chengcheng
 * 时间： 18/1/17- 下午2:20
 * 描述：
 */

public class ImageDirRecycleAdapter extends BaseRecycleAdapter<ImageFolder> {

    public ImageDirRecycleAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, ImageFolder data) {
        ImageDirViewHolder imageDirViewHolder = (ImageDirViewHolder) viewHolder;
        imageDirViewHolder.bindViews(data);
    }

    @Override
    public ImageDirViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_dir_item, parent, false);
        return new ImageDirViewHolder(view);
    }

    public class ImageDirViewHolder extends BaseRecycleViewHolder<ImageFolder> {
        ImageView mImageView;//第一张图片
        TextView mDirNameTextView;//文件夹名称
        TextView mDirCountTextView;//图片数量

        public ImageDirViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.dir_item_image);
            mDirNameTextView = (TextView) itemView.findViewById(R.id.dir_item_name);
            mDirCountTextView = (TextView) itemView.findViewById(R.id.dir_item_count);
        }

        @Override
        public void bindViews(ImageFolder object) {
            String firstImagePath = object.getFirstImagePath();
            if (!TextUtils.isEmpty(firstImagePath)) {
                ImageLoader.getInstance().loadImage(firstImagePath, mImageView);
            }
            mDirNameTextView.setText(object.getName());
            mDirCountTextView.setText(object.getCount() + "张");
        }
    }
}
