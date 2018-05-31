package com.mycamera.cameralibrary;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：成成
 * 描述：加载手机图片实现类
 */
public class ImageDataSource implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOADER_ALL = 0;         //加载所有图片
    public static final int LOADER_CATEGORY = 1;    //分类加载图片
    private final String[] IMAGE_PROJECTION = {     //查询图片需要的数据列
            MediaStore.Images.Media.DISPLAY_NAME,   //图片的显示名称  aaa.jpg
            MediaStore.Images.Media.DATA,           //图片的绝对路径
            MediaStore.Images.Media.SIZE,           //图片的大小，long型  132492
            MediaStore.Images.Media.WIDTH,          //图片的宽度，int型  1920
            MediaStore.Images.Media.HEIGHT,         //图片的高度，int型  1080
            MediaStore.Images.Media.MIME_TYPE,      //图片的类型     image/jpeg
            MediaStore.Images.Media.DATE_ADDED};    //图片被添加的时间，long型  1450518608

    private FragmentActivity activity;
    private ArrayList<ImageFolder> imageFolderList = new ArrayList<>();   //所有的图片文件夹

    ScanPictureCallBack mScanPictureCallBack;//扫描完成回调

    /**
     * @param activity            用于初始化LoaderManager，需要兼容到2.3
     * @param path                指定扫描的文件夹目录，可以为 null，表示扫描所有图片
     * @param scanPictureCallBack 图片加载完成的监听
     */
    public ImageDataSource(FragmentActivity activity, String path, ScanPictureCallBack scanPictureCallBack) {
        this.activity = activity;
        this.mScanPictureCallBack = scanPictureCallBack;
        LoaderManager loaderManager = activity.getSupportLoaderManager();
        if (path == null) {
            loaderManager.initLoader(LOADER_ALL, null, this);//加载所有的图片
        } else {
            //加载指定目录的图片
            Bundle bundle = new Bundle();
            bundle.putString("path", path);
            loaderManager.initLoader(LOADER_CATEGORY, bundle, this);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        //扫描所有图片
        if (id == LOADER_ALL) {
            cursorLoader = new CursorLoader(activity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, null, null, IMAGE_PROJECTION[6] + " DESC");
        }
        //扫描某个图片文件夹
        if (id == LOADER_CATEGORY) {
            cursorLoader = new CursorLoader(activity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, IMAGE_PROJECTION[1] + " like '%" + args.getString("path") + "%'", null, IMAGE_PROJECTION[6] + " DESC");
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            imageFolderList.clear();
            ArrayList<ImageData> allImages = new ArrayList<>();   //所有图片的集合,不分文件夹
            while (data.moveToNext()) {
                //查询数据
                String imageName = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                String imagePath = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));

                File file = new File(imagePath);
                if (!file.exists() || file.length() <= 0) {
                    continue;
                }

                long imageSize = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                int imageWidth = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                int imageHeight = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
                String imageMimeType = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[5]));
                long imageAddTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[6]));
                Long fileSize = imageSize / 1024; //将图片转成kb
                Log.i("ImageDataSource", "图片大小 = " + fileSize + "");
                if (fileSize > 50L && !imageMimeType.endsWith(".9.png") && imageWidth > 150 && imageHeight > 150) {//过滤掉图片非常小的图片
                    //封装实体
                    ImageData imageItem = new ImageData();
                    imageItem.name = imageName;
                    imageItem.path = imagePath;
                    imageItem.size = fileSize;
                    imageItem.width = imageWidth;
                    imageItem.height = imageHeight;
                    imageItem.mimeType = imageMimeType;
                    imageItem.addTime = imageAddTime;
                    imageItem.isSelect = false;
                    allImages.add(imageItem);
                    //根据父路径分类存放图片
                    File imageFile = new File(imagePath);
                    File imageParentFile = imageFile.getParentFile();
                    ImageFolder imageFolder = new ImageFolder();
                    imageFolder.setName(imageParentFile.getName());
                    imageFolder.setSelect(false);
                    imageFolder.setPath(imageParentFile.getAbsolutePath());

                    if (!imageFolderList.contains(imageFolder)) {
                        ArrayList<ImageData> images = new ArrayList<>();
                        images.add(imageItem);
                        imageFolder.setCover(imageItem);
                        imageFolder.setImages(images);
                        imageFolderList.add(imageFolder);
                    } else {
                        imageFolderList.get(imageFolderList.indexOf(imageFolder)).getImages().add(imageItem);
                    }
                }
            }
            //防止没有图片报异常
            if (data.getCount() > 0 && allImages.size() > 0) {
                //构造所有图片的集合
                ImageFolder allImagesFolder = new ImageFolder();
                allImagesFolder.setName("所有图片");
                allImagesFolder.setPath("/");
                allImagesFolder.setSelect(true);
                allImagesFolder.setCover(allImages.get(0));
                allImagesFolder.setImages(allImages);
                imageFolderList.add(0, allImagesFolder);  //确保第一条是所有图片
            }
            //回调接口，通知图片数据准备完成
            if (imageFolderList.size() > 0) {
                mScanPictureCallBack.ScanPictureComplete(imageFolderList);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        System.out.println("--------");
    }


    public interface ScanPictureCallBack {
        void ScanPictureComplete(List<ImageFolder> imageFolders);
    }
}
