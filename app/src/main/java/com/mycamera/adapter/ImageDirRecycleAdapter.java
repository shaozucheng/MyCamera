package com.mycamera.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mycamera.R;
import com.mycamera.cameralibrary.ImageFolder;


/**
 * 作者： chengcheng
 * 时间： 18/1/17- 下午2:20
 * 描述：
 */

public class ImageDirRecycleAdapter extends BaseRecycleAdapter<ImageFolder> {

    private int showImageSize;

    public ImageDirRecycleAdapter(Context context) {
        super(context);
        showImageSize = getImageItemWidth((Activity) context);
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
        ImageView mCheckImageView;

        public ImageDirViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.dir_item_image);
            mDirNameTextView = itemView.findViewById(R.id.dir_item_name);
            mDirCountTextView = itemView.findViewById(R.id.dir_item_count);
            mCheckImageView = itemView.findViewById(R.id.folder_check_image);
        }

        @Override
        public void bindViews(ImageFolder object) {
            String firstImagePath = object.getCover().path;
            if (!TextUtils.isEmpty(firstImagePath)) {
                Glide.with(mContext).applyDefaultRequestOptions(RequestOptions.overrideOf(showImageSize, showImageSize)).load(firstImagePath).into(mImageView);
            }
            mDirNameTextView.setText(object.getName());
            String num = "共" + object.getImages().size() + "张";
            mDirCountTextView.setText(num);
            if (object.isSelect()) {
                mCheckImageView.setVisibility(View.VISIBLE);
            } else {
                mCheckImageView.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 根据屏幕宽度与密度计算GridView显示的列数， 最少为三列，并获取Item宽度
     */
    public static int getImageItemWidth(Activity activity) {
        int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
        int densityDpi = activity.getResources().getDisplayMetrics().densityDpi;
        int cols = screenWidth / densityDpi;
        cols = cols < 3 ? 3 : cols;
        int columnSpace = (int) (2 * activity.getResources().getDisplayMetrics().density);
        return (screenWidth - columnSpace * (cols - 1)) / cols;
    }
}
