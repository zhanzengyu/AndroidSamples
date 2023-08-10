package com.zengyu.sample;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.zengyu.permission.PermissionHelper;
import com.zengyu.permission.callback.OnPermissionCallback;
import com.zengyu.sample.databinding.ActivityMainBinding;
import com.zengyu.sample.util.AudioRecordUtil;
import com.zengyu.sample.util.AudioTrackUtil;
import com.zengyu.sample.util.FileUtils;
import com.zengyu.sample.util.ObjectUtil;

import java.io.File;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnPermissionCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding mBinding;
    private AudioRecordUtil mAudioRecordUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRecord();
        AudioTrackUtil.getInstance().stop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            AudioTrackUtil.getInstance().release();
        }
    }

    private String mPcmFilePath;

    private void init() {
        mAudioRecordUtil = new AudioRecordUtil();
        setListener();

        mPcmFilePath = ObjectUtil.wrapObjectToString(getExternalCacheDir().getAbsolutePath(), File.separator, "zengyu.pcm");

        Log.d(TAG, ObjectUtil.wrapObjectToString("PCM store loc:", mPcmFilePath));

        FileUtils.getInstance().setFilePath(mPcmFilePath);
        AudioTrackUtil.getInstance().init(mPcmFilePath);
    }

    private void setListener() {
        mBinding.btnDelFile.setOnClickListener(this);
        mBinding.btnStart.setOnClickListener(this);
        mBinding.btnStop.setOnClickListener(this);
        mBinding.btnPlay.setOnClickListener(this);

        mAudioRecordUtil.setOnAudioFrameCaptureListener(audioData -> {
            Log.d(TAG, "back=" + Arrays.toString(audioData));
            FileUtils.getInstance().writePCM(audioData);
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_del_file) {
            File file = new File(mPcmFilePath);
            if (file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }

            Toast.makeText(this, "操作完成", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.btn_start) {
            PermissionHelper.getInstance(this).request(Manifest.permission.RECORD_AUDIO);
        } else if (id == R.id.btn_stop) {
            stopRecord();
        } else if (id == R.id.btn_play) {
            try {
                AudioTrackUtil.getInstance().start();
            } catch (Exception e) {
                Log.e(TAG, "exception:", e);
            }
        }
    }

    private void stopRecord() {
        mAudioRecordUtil.stopCapture();
    }

    private void recordAudio() {
        Log.d(TAG, "recordAudio");
        mAudioRecordUtil.startCapture();
    }

    @Override
    public void onPermissionGranted(@NonNull String[] permissionName) {
        String lastPermission = permissionName[permissionName.length - 1];
        if (Manifest.permission.RECORD_AUDIO.equals(lastPermission)) {
            recordAudio();
        }
    }

    @Override
    public void onPermissionDeclined(@NonNull String[] permissionName) {

    }

    @Override
    public void onPermissionPreGranted(@NonNull String permissionsName) {
        if (Manifest.permission.RECORD_AUDIO.equals(permissionsName)) {
            recordAudio();
        }
    }

    @Override
    public void onPermissionNeedExplanation(@NonNull String permissionName) {
        PermissionHelper.getInstance(this).requestAfterExplanation(permissionName);
    }

    @Override
    public void onPermissionReallyDeclined(@NonNull String permissionName) {

    }

    @Override
    public void onNoPermissionNeeded() {
        recordAudio();
    }
}