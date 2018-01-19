package com.mycamera.cameralibrary;

/**
 * 作者： chengcheng
 * 时间： 18/1/18- 下午3:41
 * 描述：
 */

public class ImageData {
    private String imageLocalPath;
    private boolean isSelect;

    public String getImageLocalPath() {
        return imageLocalPath;
    }

    public void setImageLocalPath(String imageLocalPath) {
        this.imageLocalPath = imageLocalPath;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
