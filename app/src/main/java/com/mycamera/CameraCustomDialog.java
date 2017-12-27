package com.mycamera;

import android.content.Context;
import android.support.v7.app.AppCompatDialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.mycamera.util.DialogUtils;


/**
 * 说明:选择拍照和从相册中选择图片对话框
 * Author shaozucheng
 * Time:16/3/4 上午9:36
 */
public class CameraCustomDialog {

    private Context mContext;
    private AppCompatDialog mDialog;
    private TextView mCancelButton;//取消按钮
    private TextView mPhotographTextView;//拍照
    private TextView mAlbumTextView;//从相册中选择

    public CameraCustomDialog(Context context) {
        this.mContext = context;
        mDialog = new AppCompatDialog(context, R.style.camera_dialog_no_screen);
        DialogUtils.resetDialogScreenPosition(mDialog, Gravity.BOTTOM, 0, 0, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
        Window window = mDialog.getWindow();
        window.setContentView(R.layout.dialog_select_camera);
        window.setWindowAnimations(R.style.camera_dialog_enter_exit);  //添加dialog进入和退出的动画

        mCancelButton = window.findViewById(R.id.dialog_cancel_btn);
        mPhotographTextView = window.findViewById(R.id.dialog_photograph);
        mAlbumTextView = window.findViewById(R.id.dialog_album);


    }

    public void setTextViewOnClickListener(View.OnClickListener listener) {
        if (mPhotographTextView != null) {
            mPhotographTextView.setOnClickListener(listener);
        }
        if (mAlbumTextView != null) {
            mAlbumTextView.setOnClickListener(listener);
        }
        if (mCancelButton != null) {
            mCancelButton.setOnClickListener(listener);
        }
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
