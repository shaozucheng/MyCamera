package com.mycamera.camera;

import android.content.Context;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.mycamera.R;
import com.mycamera.adapter.ImageDirRecycleAdapter;
import com.mycamera.adapter.OnItemClickListener;
import com.mycamera.cameralibrary.ImageFolder;
import com.mycamera.cameralibrary.OnImageDirSelected;
import com.mycamera.util.DialogUtils;

import java.util.List;


/**
 * 说明:
 * Author shaozucheng
 * Time:16/4/12 上午10:10
 */
public class ImageDirListDialog {
    private Context mContext;
    private AppCompatDialog mDialog;
    private OnImageDirSelected mImageDirSelected;
    private RecyclerView mRecyclerView;
    private ImageDirRecycleAdapter mImageDirRecycleAdapter;

    public ImageDirListDialog(Context context) {
        mContext = context;
        mDialog = new AppCompatDialog(context, R.style.camera_dialog_no_screen);
        DialogUtils.resetDialogScreenPosition(mDialog, Gravity.BOTTOM, 0, 0, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);

        Window window = mDialog.getWindow();
        window.setContentView(R.layout.list_dir);
        window.setWindowAnimations(R.style.camera_dialog_enter_exit);  //添加dialog进入和退出的动画

        mRecyclerView = window.findViewById(R.id.image_dir_RecyclerView);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        mImageDirRecycleAdapter = new ImageDirRecycleAdapter(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mImageDirRecycleAdapter);

    }

    public void setImageFolders(final List<ImageFolder> mImageFolders) {
        mImageDirRecycleAdapter.setDatas(mImageFolders);
        mImageDirRecycleAdapter.notifyDataSetChanged();
        mImageDirRecycleAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mImageDirSelected != null) {
                    ImageFolder imageFolder = mImageDirRecycleAdapter.getDatas().get(position);
                    for (int i = 0; i < mImageFolders.size(); i++) {
                        if (imageFolder.getPath().equals(mImageFolders.get(i).getPath())) {
                            mImageFolders.get(i).setSelect(true);
                        } else {
                            mImageFolders.get(i).setSelect(false);
                        }
                    }
                    mImageDirRecycleAdapter.notifyDataSetChanged();
                    mImageDirSelected.selectedImageFolder(mImageDirRecycleAdapter.getDatas().get(position));
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        mDialog.show();
    }


    public void setOnImageDirSelected(OnImageDirSelected mImageDirSelected) {
        this.mImageDirSelected = mImageDirSelected;
    }


    /**
     * 设置是否可以关闭
     *
     * @param cancelable true或false
     */
    public void setCancelable(boolean cancelable) {
        mDialog.setCancelable(cancelable);
    }

    /**
     * 设置是否可以点击dialog外面关闭dialog
     *
     * @param canceledOnTouchOutside true或false
     */
    public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        mDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        mDialog.dismiss();
    }


}
