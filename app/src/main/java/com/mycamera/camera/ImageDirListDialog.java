package com.mycamera.camera;

import android.content.Context;
import android.support.v7.app.AppCompatDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mycamera.R;
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
    private ListView mListDir;
    private OnImageDirSelected mImageDirSelected;
    private static DisplayMetrics sDisplayMetrics = null;
    private static final float ROUND_DIFFERENCE = 0.5f;
    private ImageDirAdapter imageDirAdapter;

    public ImageDirListDialog(Context context, final List<ImageFolder> mImageFolders) {
        mContext = context;
        mDialog = new AppCompatDialog(context, R.style.camera_dialog_no_screen);
        DialogUtils.resetDialogScreenPosition(mDialog, Gravity.BOTTOM, 0, 0, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
        Window window = mDialog.getWindow();
        window.setContentView(R.layout.list_dir);
        window.setWindowAnimations(R.style.camera_dialog_enter_exit);  //添加dialog进入和退出的动画

        mListDir = (ListView) window.findViewById(R.id.id_list_dir);

        imageDirAdapter = new ImageDirAdapter(context);
        imageDirAdapter.setData(mImageFolders);
        mListDir.setAdapter(imageDirAdapter);

        mListDir.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mImageDirSelected != null) {
                    mImageDirSelected.selectedImageFolder(mImageFolders.get(position));
                }
            }
        });
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
