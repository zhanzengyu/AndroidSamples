package com.zengyu.sample.util;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioRecordUtil {
    private static final String TAG = AudioRecordUtil.class.getSimpleName();

    private static final int DEFAULT_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int DEFAULT_SAMPLE_RATE = 44100;
    private static final int DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private int mMinBufferSize = 0;
    private AudioRecord mAudioRecord;
    private OnAudioFrameCaptureListener mAudioFrameCaptureListener;

    public void setOnAudioFrameCaptureListener(OnAudioFrameCaptureListener audioFrameCaptureListener) {
        this.mAudioFrameCaptureListener = audioFrameCaptureListener;
    }

    private boolean mIsCaptureStarted = false;
    public boolean isCaptureStarted() {
        return mIsCaptureStarted;
    }

    public boolean startCapture() {
        return startCapture(DEFAULT_SOURCE, DEFAULT_SAMPLE_RATE, DEFAULT_CHANNEL_CONFIG, DEFAULT_AUDIO_FORMAT);
    }

    public boolean startCapture(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat) {
        if (mIsCaptureStarted) {
            Log.e(TAG, "Capture already started!");
            return false;
        }

        mMinBufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        if (mMinBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(TAG, "Invalid parameter!");
            return false;
        }

        Log.d(TAG, ObjectUtil.wrapObjectToString("mMinBufferSize = ", mMinBufferSize, " bytes!"));

        mAudioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, mMinBufferSize);
        if (mAudioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
            Log.e(TAG, "AudioRecord initialize failed!");
            return false;
        }

        mAudioRecord.startRecording();

        mLoopStop = false;
        mCaptureThread = new Thread(new AudioCaptureRunnable());
        mCaptureThread.start();
        mIsCaptureStarted = true;

        Log.d(TAG, "start audio capture success!");

        return true;
    }

    public void stopCapture() {
        if (!mIsCaptureStarted) {
            return;
        }

        mLoopStop = true;
        mCaptureThread.interrupt();
        try {
            mCaptureThread.join(1000);
        } catch (InterruptedException e) {
            Log.e(TAG, ObjectUtil.wrapObjectToString("InterruptedException e: ", e));
        }

        if (mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
            mAudioRecord.stop();
        }

        mAudioRecord.release();

        mIsCaptureStarted = false;

        Log.d(TAG, "stop audio capture success!");
    }

    private Thread mCaptureThread;
    private volatile boolean mLoopStop = false;
    private class AudioCaptureRunnable implements Runnable {

        @Override
        public void run() {
            while (!mLoopStop) {
                byte[] buffer = new byte[mMinBufferSize];
                int ret = mAudioRecord.read(buffer, 0, mMinBufferSize);
                if (ret < 0) {
                    Log.e(TAG, ObjectUtil.wrapObjectToString("Error ret: ", ret));
                } else {
                    if (mAudioFrameCaptureListener != null) {
                        mAudioFrameCaptureListener.onAudioFrameCapture(buffer);
                    }

                    Log.d(TAG, ObjectUtil.wrapObjectToString("OK, Capture ", ret, " bytes!"));
                }
            }
        }

    }

    public interface OnAudioFrameCaptureListener {
        void onAudioFrameCapture(byte[] audioData);
    }
}
