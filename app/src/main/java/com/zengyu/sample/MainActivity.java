package com.zengyu.sample;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(accessory != null){
                            //call method to set up accessory communication
                            Log.d(TAG, "permit accessory "+accessory);
                            openAccessory();
                        }
                    } else {
                        Log.d(TAG, "permission denied for accessory " + accessory);
                    }
                }
            } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                Log.d(TAG, "ACTION_USB_ACCESSORY_DETACHED");
                UsbAccessory accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                if (accessory != null) {
                    // call your method that cleans up and closes communication with the accessory
                    releaseRes();
                }
            }
        }
    };

    private void releaseRes() {
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
        }
        if (fileDescriptor != null) {
            try {
                fileDescriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                fileDescriptor = null;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            releaseRes();
        }
    }

    private ParcelFileDescriptor fileDescriptor;
    private FileInputStream inputStream;
    private FileOutputStream outputStream;
    private Thread thread;

    private void openAccessory() {
        Log.d(TAG, "openAccessory: " + accessory);
        fileDescriptor = usbManager.openAccessory(accessory);
        if (fileDescriptor != null) {
            FileDescriptor fd = fileDescriptor.getFileDescriptor();
            inputStream = new FileInputStream(fd);
            outputStream = new FileOutputStream(fd);
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!thread.isInterrupted()) {
                        byte[] data = new byte[16384];
                        try {
                            int readLen = inputStream.read(data);
                            Log.d(TAG, "readLen="+readLen);
                            if (readLen > 0) {
                                byte[] realData = new byte[readLen];
                                System.arraycopy(data, 0, realData, 0, readLen);
                                Log.d(TAG, "data="+Arrays.toString(realData));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }, "AccessoryThread");
            thread.start();
        }
    }

    private UsbManager usbManager;
    private UsbAccessory accessory;
    private PendingIntent permissionIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);

        //动态广播注册
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        registerReceiver(usbReceiver, filter);
    }

    public void getPermission(View view) {
        //获取 Usb 配件
        UsbAccessory[] accessoryList = usbManager.getAccessoryList();
        if (accessoryList != null && accessoryList.length > 0) {
            accessory = accessoryList[0];
            Log.d(TAG, "get accessory "+accessory);
        }
        usbManager.requestPermission(accessory, permissionIntent);
    }
}
