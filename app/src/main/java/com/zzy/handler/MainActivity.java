package com.zzy.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.zzy.handler.databinding.ActivityMainBinding;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    ActivityMainBinding mBinding;
    private MyHandler myHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initViews();
    }

    ExecutorService executorService = Executors.newFixedThreadPool(1);
    private void initViews() {
        executorService.submit(() -> {
            Looper.prepare();
            myHandler = new MyHandler(MainActivity.this);
            Looper.loop();
        });
        mBinding.normalBtn.setOnClickListener(v -> {
            Log.i(TAG, "onClick normalBtn");
            myHandler.sendEmptyMessage(MSG_NORMAL);
        });
        mBinding.normalDelayBtn.setOnClickListener(v -> {
            Log.i(TAG, "onClick normalDelayBtn");
            myHandler.sendEmptyMessageDelayed(MSG_NORMAL_DELAY, 5000);
        });
        mBinding.barrierBtn.setOnClickListener(v -> {
            Log.i(TAG, "onClick barrierBtn");
            sendSyncBarrier();
        });
        mBinding.removeBarrierBtn.setOnClickListener(v -> {
            Log.i(TAG, "onClick removeBarrierBtn");
            removeSyncBarrier();
        });
        mBinding.asyncBtn.setOnClickListener(v -> {
            Log.i(TAG, "onClick asyncBtn");
            Message msg = myHandler.obtainMessage(MSG_ASYNC);
            msg.setAsynchronous(true);
            msg.sendToTarget();
        });
    }

    private void removeSyncBarrier() {
        Log.i(TAG, "removeSyncBarrier token:" + token);
        MessageQueue queue = myHandler.getLooper().getQueue();
        try {
            Method method = MessageQueue.class.getDeclaredMethod("removeSyncBarrier", int.class);
            method.setAccessible(true);
            method.invoke(queue, token);
        } catch (Exception e) {
            Log.e(TAG, "removeSyncBarrier error: " + e);
        }
    }

    int token;
    private void sendSyncBarrier() {
        MessageQueue queue = myHandler.getLooper().getQueue();
        try {
            Method method = MessageQueue.class.getDeclaredMethod("postSyncBarrier");
            method.setAccessible(true);
            token = (int) method.invoke(queue);
            Log.i(TAG, "postSyncBarrier token: " + token);
        } catch (Exception e) {
            Log.e(TAG, "postSyncBarrier error: " + e);
        }
    }

    private final static int MSG_NORMAL = 1;
    private final static int MSG_NORMAL_DELAY = 2;
    private final static int MSG_ASYNC = 3;
    private final static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;
        private final StringBuilder mStringBuilder;
        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
            mStringBuilder = new StringBuilder();
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            MainActivity activity = mActivity.get();
            if (activity == null) return;

            switch (msg.what) {
                case MSG_NORMAL:
                    mStringBuilder.append("Normal msg:").append(getCurrentTime());
                    break;
                case MSG_NORMAL_DELAY:
                    mStringBuilder.append("Delay msg:").append(getCurrentTime());
                    break;
                case MSG_ASYNC:
                    mStringBuilder.append("Async msg:").append(getCurrentTime());
                    break;
                default:
                    mStringBuilder.append("Never enter, check your code!!!!!!!!!!!!!!!!!!!!!!");
                    break;
            }
            activity.runOnUiThread(() -> activity.mBinding.contentTv.setText(mStringBuilder.toString()));
            mStringBuilder.append("\n");
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
        private String getCurrentTime() {
            return simpleDateFormat.format(new Date());
        }
    }
}