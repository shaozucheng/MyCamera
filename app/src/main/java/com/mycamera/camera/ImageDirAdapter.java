package com.mycamera.camera;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycamera.R;
import com.mycamera.cameralibrary.ImageFolder;


/**
 * 说明:图片文件夹适配器
 * Author shaozucheng
 * Time:16/5/30 下午6:02
 */
public class ImageDirAdapter extends HBaseAdapter<ImageFolder> {
    private Context mContext;

    public ImageDirAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected BaseViewHolder<ImageFolder> createViewHolder() {
        return new ImageDirViewHolder();
    }

    class ImageDirViewHolder extends BaseViewHolder<ImageFolder> {
        View mRootView;
        ImageView mImageView;//第一张图片
        TextView mDirNameTextView;//文件夹名称
        TextView mDirCountTextView;//图片数量


        @Override
        public View inflateItemView() {
            mRootView = LayoutInflater.from(mContext).inflate(R.layout.list_dir_item, null, false);
            mImageView = (ImageView) mRootView.findViewById(R.id.dir_item_image);
            mDirNameTextView = (TextView) mRootView.findViewById(R.id.dir_item_name);
            mDirCountTextView = (TextView) mRootView.findViewById(R.id.dir_item_count);
            return mRootView;
        }

        @Override
        public void bindViews(ImageFolder object) {
          //  ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(object.getFirstImagePath(), mImageView);
            mDirNameTextView.setText(object.getName());
            mDirCountTextView.setText(object.getCount() + "张");
        }
    }
}
