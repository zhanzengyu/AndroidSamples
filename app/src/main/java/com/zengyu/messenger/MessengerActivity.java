package com.zengyu.messenger;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MessengerActivity extends AppCompatActivity {

    public static final int MSG_FROM_CLIENT = 0x01;
    public static final String CLIENT_KEY = "msg";

    public static void actionStart(Activity activity) {
        Intent intent = new Intent(activity, MessengerActivity.class);
        activity.startActivity(intent);
    }


    /**
     * 客户端接收消息第一步：创建 MessengerHandler
     */
    private static class MessengerHander extends Handler {

        private Context context;

        public MessengerHander(Context context) {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MessengerService.MSG_FROM_SERVER:
                    /**
                     * 客户端接收消息第四步，从 msg 获取消息内容
                     */
                    String data = msg.getData().getString(MessengerService.SERVER_KEY);
                    // 由于不同进程看打印日志需要切换不方便，因此通过 Toast 来提示
                    Toast.makeText(context, "client:getDataFromService="+data, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    /**
     * 客户端接收消息第二步：创建 Messenger 对象
     */
    private Messenger mRecvMessenger;

    private Messenger mService;
    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 客户端发送消息第一步：先获取 Messenger
            mService = new Messenger(service);

            // 客户端发送消息第二步：构造要发送的 Message
            Message msg = Message.obtain(null, MSG_FROM_CLIENT);
            Bundle data = new Bundle();
            data.putString(CLIENT_KEY, "Hello, this is client.");
            msg.setData(data);

            /**
             * 客户端接收消息第三步：设置 Messenger 到 msg 的 replyTo 字段
             */
            msg.replyTo = mRecvMessenger;

            try {
                // 客户端发送消息第三步：通过 Messenger 发送 Message
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        mRecvMessenger = new Messenger(new MessengerHander(getApplicationContext()));
        Intent intent = new Intent(this, MessengerService.class);
        bindService(intent, mServiceConn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConn);
    }
}
