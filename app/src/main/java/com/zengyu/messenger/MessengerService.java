package com.zengyu.messenger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;


public class MessengerService extends Service {


    public static final int MSG_FROM_SERVER = 0x01;
    public static final String SERVER_KEY = "reply";

    /**
     * 服务端接收消息第一步：创建 MessengerHandler
     */
    private static class MessengerHandler extends Handler {

        private Context context;

        public MessengerHandler(Context context) {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MessengerActivity.MSG_FROM_CLIENT:
                    /**
                     * 服务端接收消息第四步，从 msg 获取消息内容
                     */
                    String receive = msg.getData().getString(MessengerActivity.CLIENT_KEY);
                    // 由于不同进程看打印日志需要切换不方便，因此通过 Toast 来提示
                    Toast.makeText(context, "service:receive from client="+receive, Toast.LENGTH_LONG).show();

                    // 服务端发送消息第一步：从 msg 的 replyTo 获取 Messenger
                    Messenger messenger = msg.replyTo;
                    // 服务端发送消息第二步：构建 Message
                    Message replyMsg = Message.obtain(null, MSG_FROM_SERVER);
                    Bundle bundle = new Bundle();
                    bundle.putString(SERVER_KEY, "i had receive your msg");
                    replyMsg.setData(bundle);

                    try {
                        // 服务端发送消息第三步：发送消息
                        messenger.send(replyMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMessenger = new Messenger(new MessengerHandler(getApplicationContext()));
    }

    /**
     * 服务端接收消息第二步：创建 Messenger 对象
     */
    private Messenger mMessenger ;

    @Override
    public IBinder onBind(Intent intent) {
        /**
         * 服务端接收消息第三步：返回 Messenger 里的 binder 对象
         */
        return mMessenger.getBinder();
    }
}
