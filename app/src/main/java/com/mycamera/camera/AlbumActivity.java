package com.mycamera.camera;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.mycamera.cameralibrary.ImageDataSource;
import com.mycamera.cameralibrary.ImageFolder;
import com.mycamera.cameralibrary.OnImageDirSelected;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明:从相册中选择图片
 * Author shaozucheng
 * Time:16/4/8 上午11:06
 */
public class AlbumActivity extends AppCompatActivity implements OnImageDirSelected {
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

    private ArrayList<String> mSelectedImageList = new ArrayList<>();//选择后的图片集合
    private AlbumRecycleAdapter mAlbumRecycleAdapter;//展示图片适配器
    private int mNeedSelectAmount = 1;//需要选择的图片数量，默认最少为1张
    private ImageDirListDialog mImageDirListDialog;//图片文件夹对话框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        mNeedSelectAmount = getIntent().getIntExtra(CameraPhotoHelper.INTENT_KEY_NEED_SELECT_AMOUNT, 1);
        initView();
        initAlbumAdapter();
        initBottomEvent();
        completeOnClickListener();
        getImageData();
    }

    //初始化组件
    private void initView() {
        mLeftImageView = findViewById(R.id.screen_left_btn);
        mCompleteTextView = findViewById(R.id.tv_screen_complete);
        mRecyclerView = findViewById(R.id.album_RecyclerView);
        mChooseDir = findViewById(R.id.id_choose_dir);
        mImageCount = findViewById(R.id.id_total_count);
        mBottomLayout = findViewById(R.id.id_bottom_layout);
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
                if (!mSelectedImageList.contains(imageData.path)) {
                    if (mSelectedImageList.size() >= mNeedSelectAmount) {
                        Toast.makeText(AlbumActivity.this, "最多可选择" + mNeedSelectAmount + "张图片", Toast.LENGTH_LONG).show();
                    } else {
                        mSelectedImageList.add(imageData.path);
                        imageData.isSelect = true;
                        mAlbumRecycleAdapter.notifyItemChanged(position);
                    }
                } else {
                    mSelectedImageList.remove(imageData.path);
                    imageData.isSelect = false;
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
        new ImageDataSource(this, null, new ImageDataSource.ScanPictureCallBack() {
            @Override
            public void ScanPictureComplete(List<ImageFolder> imageFolderList) {
                if (imageFolderList != null && imageFolderList.size() > 0) {
                    mImageFolders.clear();
                    mImageFolders.addAll(imageFolderList);
                    List<ImageData> imageDataList = mImageFolders.get(0).getImages();
                    String num = imageDataList.size() + "张";
                    String folderName = mImageFolders.get(0).getName();
                    setAlbumRecycleViewData(imageDataList, num, folderName);
                }
            }
        });
    }


    /**
     * 设置显示图片
     *
     * @param imageDataList 图片集合
     * @param num           数目
     * @param folderName    文件夹名称
     */
    private void setAlbumRecycleViewData(List<ImageData> imageDataList, String num, String folderName) {
        mAlbumRecycleAdapter.setDatas(imageDataList);
        mAlbumRecycleAdapter.notifyDataSetChanged();
        mImageCount.setText(num);
        mChooseDir.setText(folderName);
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
        if (mImageDirListDialog ==null){
            mImageDirListDialog = new ImageDirListDialog(this);
        }
        mImageDirListDialog.setImageFolders(mImageFolders);
        // 设置选择文件夹的回调
        mImageDirListDialog.setOnImageDirSelected(this);
    }

    @Override
    public void selectedImageFolder(ImageFolder folder) {
        List<ImageData> imageDataList = folder.getImages();
        String num = imageDataList.size() + "张";
        String folderName = folder.getName();
        setAlbumRecycleViewData(imageDataList, num, folderName);
        mImageDirListDialog.dismiss();
    }
}
