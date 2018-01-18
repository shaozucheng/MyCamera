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
import android.widget.Toast;

import com.mycamera.R;
import com.mycamera.cameralibrary.ImageLoader;

import java.util.ArrayList;

/**
 * 作者： chengcheng
 * 时间： 18/1/17- 下午4:01
 * 描述：
 */

public class AlbumRecycleAdapter extends BaseRecycleAdapter<String> {

    /**
     * 文件夹路径
     */
    private String mDirPath;
    /**
     * 最大选择数量
     */
    private int mNeedSelectAmount;

    /**
     * 用户选择的图片，存储为图片的完整路径
     */
    private ArrayList<String> mSelectedImage = new ArrayList<>();

    public AlbumRecycleAdapter(Context context) {
        super(context);
    }

    public ArrayList<String> getSelectedImage() {
        return mSelectedImage;
    }


    public String getDirPath() {
        return mDirPath;
    }

    public void setDirPath(String dirPath) {
        mDirPath = dirPath;
    }

    public int getNeedSelectAmount() {
        return mNeedSelectAmount;
    }

    public void setNeedSelectAmount(int needSelectAmount) {
        mNeedSelectAmount = needSelectAmount;
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, String data) {
        AlbumViewHolder albumViewHolder = (AlbumViewHolder) viewHolder;
        albumViewHolder.bindViews(data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.grid_item, parent, false);
        return new AlbumViewHolder(view);
    }

    public class AlbumViewHolder extends BaseRecycleViewHolder<String> {

        private ImageView mImageView;
        private ImageButton mImageButton;

        public AlbumViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.id_item_image);
            mImageButton = itemView.findViewById(R.id.id_item_select);
        }

        @Override
        public void bindViews(String object) {
            //设置图片
           final String url = mDirPath + "/" + object;
            if (!TextUtils.isEmpty(object)) {
                ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(url, mImageView);
            }
            /**
             * 已经选择过的图片，显示出选择过的效果
             */
            if (mSelectedImage.contains(url)) {
                mImageButton.setImageResource(R.drawable.pictures_selected);
                mImageView.setColorFilter(Color.parseColor("#77000000"));
            }

            mImageView.setColorFilter(null);
            //设置ImageView的点击事件
            mImageView.setOnClickListener(new View.OnClickListener() {
                //选择，则将图片变暗，反之则反之
                @Override
                public void onClick(View v) {
                    // 已经选择过该图片
                    if (mSelectedImage.contains(url)) {
                        mSelectedImage.remove(url);
                        mImageButton.setImageResource(R.drawable.picture_unselected);
                        mImageView.setColorFilter(null);
                    } else {// 未选择该图片
                        if (mSelectedImage.size() >= mNeedSelectAmount) {
                            Toast.makeText(mContext, "最多可选择" + mNeedSelectAmount + "张图片", Toast.LENGTH_LONG).show();
                        }else {
                            mSelectedImage.add(url);
                            mImageButton.setImageResource(R.drawable.pictures_selected);
                            mImageView.setColorFilter(Color.parseColor("#77000000"));
                        }
                    }

                }
            });
        }
    }
}
