package com.mycamera.camera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mycamera.CameraPhotoHelper;
import com.mycamera.R;
import com.mycamera.adapter.AlbumRecycleAdapter;
import com.mycamera.adapter.OnItemClickListener;
import com.mycamera.cameralibrary.ImageData;
import com.mycamera.cameralibrary.ImageFolder;
import com.mycamera.cameralibrary.OnImageDirSelected;
import com.mycamera.cameralibrary.ScanPictureData;
import com.mycamera.cameralibrary.ScanPictureTask;
import com.mycamera.cameralibrary.util.FileUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 说明:从相册中选择图片
 * Author shaozucheng
 * Time:16/4/8 上午11:06
 */
public class AlbumActivity extends Activity implements OnImageDirSelected {
    private ImageView mLeftImageView;
    private TextView mCompleteTextView;
    private RecyclerView mRecyclerView;
    private TextView mChooseDir;//目录
    private TextView mImageCount;//图片数量
    private RelativeLayout mBottomLayout;//底部布局

    /**
     * 扫描拿到所有的图片文件夹
     */
    private List<ImageFolder> mImageFolders = new ArrayList<>();
    /**
     * 存放图片的文件夹
     */
    private File mImgDir;
    /**
     * 图片文件夹下所有的图片
     */
    private List<String> mImgDirPicture;

    private ArrayList<String> mSelectedImageList = new ArrayList<>();//选择后的图片集合
    private AlbumRecycleAdapter mAlbumRecycleAdapter;//展示图片适配器
    private int mNeedSelectAmount = 1;//需要选择的图片数量，默认最少为1张
    private ImageDirListDialog mImageDirListDialog;//图片文件夹对话框
    private ScanPictureTask scanPictureTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        mNeedSelectAmount = getIntent().getIntExtra(CameraPhotoHelper.INTENT_KEY_NEED_SELECT_AMOUNT, 1);
        initView();
        initAlbumAdapter();
        getImageData();
        initBottomEvent();
        completeOnClickListener();
    }

    //初始化组件
    private void initView() {
        mLeftImageView = (ImageView) findViewById(R.id.screen_left_btn);
        mCompleteTextView = (TextView) findViewById(R.id.tv_screen_complete);
        mRecyclerView = findViewById(R.id.album_RecyclerView);
        mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
        mImageCount = (TextView) findViewById(R.id.id_total_count);
        mBottomLayout = (RelativeLayout) findViewById(R.id.id_bottom_layout);
        leftFinishActivity();
    }

    private void leftFinishActivity() {
        mLeftImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initAlbumAdapter() {
        mAlbumRecycleAdapter = new AlbumRecycleAdapter(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setAdapter(mAlbumRecycleAdapter);
        mAlbumRecycleAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<ImageData> imageDataList = mAlbumRecycleAdapter.getDatas();
                ImageData imageData = imageDataList.get(position);
                if (!mSelectedImageList.contains(imageData.getImageLocalPath())) {
                    if (mSelectedImageList.size() >= mNeedSelectAmount) {
                        Toast.makeText(AlbumActivity.this, "最多可选择" + mNeedSelectAmount + "张图片", Toast.LENGTH_LONG).show();
                    } else {
                        mSelectedImageList.add(imageData.getImageLocalPath());
                        imageData.setSelect(true);
                        mAlbumRecycleAdapter.notifyItemChanged(position);
                    }
                } else {
                    mSelectedImageList.remove(imageData.getImageLocalPath());
                    imageData.setSelect(false);
                    mAlbumRecycleAdapter.notifyItemChanged(position);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private void completeOnClickListener() {
        mCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedImageList.size() == 0) {
                    Toast.makeText(AlbumActivity.this, "您还没有选择图片", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent mIntent = new Intent();
                mIntent.putStringArrayListExtra(CameraPhotoHelper.INTENT_KEY_ALBUM, mSelectedImageList);
                setResult(RESULT_OK, mIntent);
                finish();
            }
        });
    }


    /**
     * 获取图片数据
     */
    private void getImageData() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        scanPictureTask = new ScanPictureTask(AlbumActivity.this, new ScanPictureTask.ScanPictureCallBack() {
            @Override
            public void ScanPictureComplete(ScanPictureData scanPictureData) {
                mImageFolders = scanPictureData.getImageFolders();
                mImgDir = scanPictureData.getPictureMaximumDir();
                initGridViewData();
                scanPictureTask.cancel(true);
            }
        });
        scanPictureTask.execute();
    }

    /**
     * 为GirdViewView绑定数据
     */
    private void initGridViewData() {
        if (mImgDir == null) {
            Toast.makeText(getApplicationContext(), "没有扫描到任何图片", Toast.LENGTH_SHORT).show();
            return;
        }
        mImgDirPicture = Arrays.asList(mImgDir.list());
        //把排序翻转，按时间最新的排序
        Collections.reverse(mImgDirPicture);

        //可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
        List<ImageData> imageDataList = getImageDataList();
        mAlbumRecycleAdapter.setDatas(imageDataList);
        mAlbumRecycleAdapter.notifyDataSetChanged();
        mImageCount.setText(mImgDirPicture.size() + "张");
        mChooseDir.setText(mImgDir.getName());
    }


    /**
     * 为底部的布局设置点击事件，弹出dialog
     */
    private void initBottomEvent() {
        mBottomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initImageDirDialog();
            }
        });
    }

    /**
     * 初始化展示文件夹的dialog
     */
    private void initImageDirDialog() {
        if (mImageFolders == null || mImageFolders.size() == 0) return;
        List<ImageFolder> imageFolderList = new ArrayList<>();
        for (ImageFolder imageFolder : mImageFolders) {//过滤掉文件夹中没有图片的图片文件夹
            if (imageFolder.getCount() > 0) {
                imageFolderList.add(imageFolder);
            }
        }
        mImageDirListDialog = new ImageDirListDialog(AlbumActivity.this, imageFolderList);
        // 设置选择文件夹的回调
        mImageDirListDialog.setOnImageDirSelected(this);
    }

    @Override
    public void selectedImageFolder(ImageFolder folder) {
        mImgDir = new File(folder.getDir());
        mImgDirPicture = Arrays.asList(mImgDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if ((filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg")) && !filename.endsWith(".9.png")) {
                    File file = new File(mImgDir.getAbsolutePath() + "/" + filename);
                    // Log.i("album file path = ", file.getAbsolutePath());
                    Long size = FileUtil.getFileSize(file) / 1024;//将文件大小转成KB
                    if (size > 0) {
                        return true;
                    }
                }
                return false;
            }
        }));
        //把排序翻转，按时间最新的排序
        Collections.reverse(mImgDirPicture);
        //可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
        List<ImageData> imageDataList = getImageDataList();
        mAlbumRecycleAdapter.setDatas(imageDataList);
        mAlbumRecycleAdapter.notifyDataSetChanged();

        mImageCount.setText(folder.getCount() + "张");
        mChooseDir.setText(folder.getName());
        mImageDirListDialog.dismiss();
    }

    private List<ImageData> getImageDataList() {
        List<ImageData> imageDataList = new ArrayList<>();
        for (int i = 0; i < mImgDirPicture.size(); i++) {
            ImageData imageData = new ImageData();
            imageData.setImageLocalPath(mImgDir.getAbsolutePath() + "/" + mImgDirPicture.get(i));
            imageData.setSelect(false);
            imageDataList.add(imageData);
        }
        if (mSelectedImageList.size() > 0) {
            for (int i = 0; i < mSelectedImageList.size(); i++) {
                for (int j = 0; j < imageDataList.size(); j++) {
                    if (mSelectedImageList.get(i).equals(imageDataList.get(j).getImageLocalPath())) {
                        imageDataList.get(j).setSelect(true);
                    }
                }
            }
        }
        return imageDataList;
    }
}
