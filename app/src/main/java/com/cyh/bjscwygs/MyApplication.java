package com.cyh.bjscwygs;

import android.app.Application;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.jiguang.share.android.api.JShareInterface;

/**
 * Created by cyh on 2017/11/4.
 */

public class MyApplication extends Application {

    public static String ImagePath;
    public static String VideoPath;

    @Override
    public void onCreate() {
        super.onCreate();
        JShareInterface.setDebugMode(true);
        JShareInterface.init(this);
        System.out.println("Start");
        new Thread() {
            @Override
            public void run() {
                File imageFile = copyResurces("jiguang_test_img.png", "test_img.png", 0);
                File videoFile = copyResurces("jiguang.mp4", "jiguang.mp4", 0);
                if (imageFile != null) {
                    ImagePath = imageFile.getAbsolutePath();
                }

                if (videoFile != null) {
                    VideoPath = videoFile.getAbsolutePath();
                }

                super.run();
            }
        }.start();
    }

    private File copyResurces(String src, String dest, int flag) {
        File filesDir = null;
        try {
            if (flag == 0) {//copy to sdcard
                filesDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/jiguang/" + dest);
                File parentDir = filesDir.getParentFile();
                if (!parentDir.exists()) {
                    parentDir.mkdirs();
                }
            } else {//copy to data
                filesDir = new File(getFilesDir(), dest);
            }
            if (!filesDir.exists()) {
                filesDir.createNewFile();
                InputStream open = getAssets().open(src);
                FileOutputStream fileOutputStream = new FileOutputStream(filesDir);
                byte[] buffer = new byte[4 * 1024];
                int len = 0;
                while ((len = open.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, len);
                }
                open.close();
                fileOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (flag == 0) {
                filesDir = copyResurces(src, dest, 1);
            }
        }
        return filesDir;
    }
}
