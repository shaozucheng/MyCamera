package com.mycamera;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.mycamera.util.ImageEnviromentUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import io.reactivex.functions.Consumer;

/**
 * 说明:
 * Author shaozucheng
 * Time:16/4/11 上午11:44
 */
public class CameraPhotoHelper {
    public final static int REQUEST_CODE_CAMERA = 10000;// 拍照
    public final static int REQUEST_CODE_ALBUM = 10001;//从相册中选择
    public final static String INTENT_KEY_NEED_SELECT_AMOUNT = "needSelectAmount";
    public final static String INTENT_KEY_ALBUM = "album";

    private Activity mContext;
    private String cameraPicturePath;//相机的图片路径
    private File mCameraFile;

    public CameraPhotoHelper(Activity context) {
        mContext = context;
        setActivityRequestedOrientation();
    }

    /**
     * 配置configChanges
     */
    public void onConfigurationChanged(Configuration newConfig) {
        newConfig.hardKeyboardHidden = Configuration.HARDKEYBOARDHIDDEN_YES;
        newConfig.orientation = Configuration.ORIENTATION_PORTRAIT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            newConfig.setLayoutDirection(Locale.CHINA);
        }
    }

    /**
     * 忽略物理感应器
     */
    private void setActivityRequestedOrientation() {
        mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
    }

    private String getCameraPicturePath() {
        return cameraPicturePath;
    }

    private void setCameraPicturePath(String cameraPicturePath) {
        this.cameraPicturePath = cameraPicturePath;
    }

    public File getCameraFile() {
        return mCameraFile;
    }

    public void setCameraFile(File cameraFile) {
        mCameraFile = cameraFile;
    }

    /**
     * 拍照
     */
    public void takePhotoFromCamera() {
        new RxPermissions(mContext).request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (!aBoolean) return;
                        String status = Environment.getExternalStorageState();
                        if (status.equals(Environment.MEDIA_MOUNTED)) {
                            try {
                                File filePicScreenshot = ImageEnviromentUtil.getScreenshot();
                                String localTempImageFileName = ImageEnviromentUtil.getFileName();
                                if (!filePicScreenshot.exists()) {
                                    filePicScreenshot.mkdirs();
                                }
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                File f = new File(filePicScreenshot, localTempImageFileName);
                                Uri u;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    u = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", f);
                                } else {
                                    u = Uri.fromFile(f);
                                }
                                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
                                mContext.startActivityForResult(intent, REQUEST_CODE_CAMERA);
                                String imagePath = f.getAbsolutePath();
                                setCameraPicturePath(imagePath);
                                setCameraFile(f);
                            } catch (ActivityNotFoundException ignored) {
                            }
                        } else {
                            Toast.makeText(mContext, "请插入SD后，再试一下", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * 从相册中选择单张图片
     */
    public void selectSingleFormAlbum() {
        selectMoreFormAlbum(1);
    }

    /***
     * 从相册中选择
     *
     * @param needSelectAmount 需要选择的图片张数
     */
    public void selectMoreFormAlbum(final int needSelectAmount) {
        new RxPermissions(mContext).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (!aBoolean) return;
                        Intent intent = new Intent(mContext, AlbumActivity.class);
                        intent.putExtra(INTENT_KEY_NEED_SELECT_AMOUNT, needSelectAmount);
                        mContext.startActivityForResult(intent, REQUEST_CODE_ALBUM);
                    }
                });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data, CameraPhotoCallBack mCameraPhotoCallBack) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CAMERA://拍照
                    String imagePath = getCameraPicturePath();
                    mCameraPhotoCallBack.takePictureFromCamera(imagePath);
                    break;
                case REQUEST_CODE_ALBUM://从相册中选择图片
                    ArrayList<String> selectPhoto = data.getStringArrayListExtra(INTENT_KEY_ALBUM);
                    if (selectPhoto != null && selectPhoto.size() > 0) {
                        mCameraPhotoCallBack.takePictureFromGallery(selectPhoto);
                    }
                    break;
            }
        }
    }


    public interface CameraPhotoCallBack {
        void takePictureFromCamera(String imagePath);

        void takePictureFromGallery(ArrayList<String> imagePathList);
    }


}
