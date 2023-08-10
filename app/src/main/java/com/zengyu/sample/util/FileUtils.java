package com.zengyu.sample.util;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();

    private FileUtils() {
    }

    private static class Holder{
        private  static final FileUtils INSTANCE = new FileUtils();
    }

    public static FileUtils getInstance() {
        return Holder.INSTANCE;
    }

    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    public void writePCM(byte[] frame) {
        if (TextUtils.isEmpty(filePath)) {
            Log.e(TAG, "FilePath is empty");
            return;
        }

        mExecutorService.execute(() -> {
            File file = new File(filePath);
            FileOutputStream fos;
            try {
                if (!file.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    file.createNewFile();
                }
                // 每次都追加到末尾
                fos = new FileOutputStream(file, true);
                fos.write(frame);
                fos.close();
            } catch (IOException e) {
                Log.e(TAG, "write PCM exception:", e);
            }
        });
    }
}
