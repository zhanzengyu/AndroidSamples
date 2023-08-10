package com.zengyu.sample.util;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudioTrackUtil {
    private static final String TAG = AudioTrackUtil.class.getSimpleName();
    // 44.1 kHz
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private int mBufferSizeInBytes = 0;
    private AudioTrack mAudioTrack;
    private volatile Status mStatus = Status.NO_READY;
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    private AudioTrackUtil() {
    }

    private static class Holder{
        private  static final AudioTrackUtil INSTANCE = new AudioTrackUtil();
    }

    public static AudioTrackUtil getInstance() {
        return Holder.INSTANCE;
    }

    private String mFilePath;
    public AudioTrackUtil init(String filePath) throws IllegalStateException {
        mFilePath = filePath;
        mBufferSizeInBytes = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);

        Log.d(TAG, ObjectUtil.wrapObjectToString("mBufferSizeInBytes: ", mBufferSizeInBytes));

        if (mBufferSizeInBytes <= 0) {
            throw new IllegalStateException(ObjectUtil.wrapObjectToString("AudioTrack is not available for mBufferSizeInBytes: ", mBufferSizeInBytes));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mAudioTrack = new AudioTrack.Builder()
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                            .build())
                    .setAudioFormat(new AudioFormat.Builder()
                            .setEncoding(AUDIO_FORMAT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(CHANNEL_CONFIG)
                            .build())
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .setBufferSizeInBytes(mBufferSizeInBytes)
                    .build();
        } else {
            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT,
                    mBufferSizeInBytes, AudioTrack.MODE_STREAM);
        }

        mStatus = Status.READY;

        return this;
    }

    public void start() throws IllegalStateException {
        Log.d(TAG, "----------start----------");

        if (mStatus == Status.NO_READY || mAudioTrack == null) {
            throw new IllegalStateException("播放器尚未初始化");
        }

        if (mStatus == Status.START) {
            throw new IllegalStateException("正在播放...");
        }

        if (!new File(mFilePath).exists()) {
            throw new IllegalStateException("文件不存在");
        }

        mExecutorService.execute(() -> {
            try {
                playAudioData();
            } catch (IOException e) {
                Log.e(TAG, ObjectUtil.wrapObjectToString("start play audio error: ", e));
            }
        });

        mStatus = Status.START;
    }

    private void playAudioData() throws IOException {
        Log.d(TAG, "----------playAudioData----------");
        try(InputStream is = new DataInputStream(new BufferedInputStream(new FileInputStream(mFilePath)))) {
            byte[] buffers = new byte[mBufferSizeInBytes];
            int length;

            if (mAudioTrack != null && mAudioTrack.getState() != AudioTrack.STATE_UNINITIALIZED && mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
                mAudioTrack.play();
            }

            while((length = is.read(buffers)) != -1 && mStatus == Status.START) {
                mAudioTrack.write(buffers, 0, length);
            }

            stop();
        }
    }


    public void stop() {
        if (mStatus != Status.START) {
            Log.d(TAG, "播放尚未开始");
        } else {
            Log.d(TAG, "----------stop----------");
            mStatus = Status.STOP;
            mAudioTrack.stop();
        }
    }

    public void release() {
        Log.d(TAG, "----------release----------");
        mStatus = Status.NO_READY;
        if (mAudioTrack != null) {
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }


    public enum Status {
        NO_READY, READY, START, STOP
    }
}
