package com.mycamera.cameralibrary.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 作者： chengcheng
 * 时间： 18/1/19- 下午6:04
 * 描述：
 */

public class FileUtil {

    /**
     * 获取指定文件大小
     *
     * @param
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(file);
                size = fis.available();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return 0;
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return size;
    }

}
