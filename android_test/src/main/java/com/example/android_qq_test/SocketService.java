package com.example.android_qq_test;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import Model.User;
import ProjectData.Data;
import Model.toModel;
import SQLite.DBManager;
import SQLite.SqlBean;

public class SocketService extends Service {

    private OutputStream outStream;
    private ReceiveThread receiveThread = null;
    private boolean isReceive = false;
    private String TAG="socketService";
    private ApplicationData data;
    NotificationCompat.Builder mBuilder;
    /** Notification的ID */
    int notifyId = 100;
    NotificationManager mNotificationManager;
    private DBManager dbm;
    @Override
    //当使用startService()方法启动Service时，方法体内只需写return null
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        initNotify();
        data=(ApplicationData)getApplication();
        dbm = new DBManager(this);
        Log.e(TAG, "socket service created");
        new Thread(connectThread).start();
    }
    public void onStart(){
        Log.e(TAG, "socket service start");
    }
    Runnable connectThread = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG,"connectThread");
            try {
                data.setmSocket(new Socket(Data.socketServerIp,Data.socketServerPort));
                isReceive = true;

                receiveThread = new ReceiveThread(data.getmSocket());
                receiveThread.start();

                bindUser(data.getUsername());
            } catch (IOException e) {
                Log.e(TAG,"connectThread IOException: ",e);
                e.printStackTrace();
            }
        }
    };
    private void bindUser(String username)
    {
        Log.e(TAG,"bind username");
        byte[] sendBuffer = null;

        try {
            User user=new User();
            user.setUsername(username);
            user.setFlag("0");
            user.setFriend("");
            user.setMessage("");
            sendBuffer = User.ConvertToJson(user).getBytes("UTF-8");
            Log.e(TAG,"Bind Username"+User.ConvertToJson(user));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            outStream = data.getmSocket().getOutputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            outStream.write(sendBuffer);
        } catch (IOException e) {
            Log.e(TAG,"sendThread IOException: ",e);
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy() {
        Log.d(TAG, "socket service destroy!");
        try {
            data.getmSocket().close();
            isReceive=false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class ReceiveThread extends Thread{
        private InputStream inStream = null;

        private byte[] buffer;
        private String str = null;

        ReceiveThread(Socket socket){
            Log.e(TAG,"ReceiveThread");
            try {
                inStream = socket.getInputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        @Override
        public void run(){
            while(isReceive){
                buffer = new byte[512];
                try {
                    inStream.read(buffer);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    str = new String(buffer,"UTF-8").trim();
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    Log.e(TAG,"ReceiveThread IOException: ",e);
                    e.printStackTrace();
                }
                if(!(str==null||str.equals(""))) {
                    Log.e(TAG, "STR-> receive message:" + str);
                    if(toModel.UserModelFromJson(str).getFlag().equals("4")
                          &&toModel.UserModelFromJson(str).getFlag().equals("5")){
                        if(!toModel.UserModelFromJson(str).getUsername().equals(data.getUsername())) {
                            final String toast=toModel.UserModelFromJson(str).getMessage();
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), toast,Toast.LENGTH_SHORT).show();
                                }
                            });

                            Intent intent = new Intent();
                            intent.setAction(Data.SOCKER_ACTION);
                            intent.putExtra("msg", str);
                            sendBroadcast(intent);
                        }
                    }
                    else if (toModel.UserModelFromJson(str).getFlag().equals("1")) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "登陆服务器成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Intent dialogIntent = new Intent(getBaseContext(), friendList.class);
                        dialogIntent.putExtra("username", data.getUsername());
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplication().startActivity(dialogIntent);
                    }
                    else if (toModel.UserModelFromJson(str).getFlag().equals("-1")) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "用户名冲突，登录服务器失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        Log.e(TAG, "SOCKER_ACTION Flag:" + toModel.UserModelFromJson(str).getFlag());
                        Intent intent = new Intent();
                        intent.setAction(Data.SOCKER_ACTION);
                        intent.putExtra("msg", str);
                        sendBroadcast(intent);
                        if(toModel.UserModelFromJson(str).getFlag().equals("3")
                                &&!toModel.UserModelFromJson(str).getUsername().equals(data.getUsername())
                                &&!toModel.UserModelFromJson(str).getUsername().equals(data.getNowFriend()))
                        {
                            SqlBean sb = new SqlBean(data.getUsername(), toModel.UserModelFromJson(str).getUsername());
                            sb.setFlag(2);
                            sb.setMsg(toModel.UserModelFromJson(str).getMessage());
                            sb.setTime(new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒").format(new Date()));
                            dbm.add(sb);
                            showNotification(str);
                        }
                    }
                    str = null;
                }
            }
        }
    }
    public PendingIntent getDefalutIntent(int flags){
        PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(), flags);
        return pendingIntent;
    }
    private void initNotify(){
        Log.e(TAG, "initNotify");
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("")
                .setContentText("")
                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
//				.setNumber(number)//显示数量
                .setTicker("")//通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
//				.setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.drawable.icon);
    }
    private void showNotification(String str)
    {
        Log.e(TAG, "showNotification");
        mBuilder.setAutoCancel(true)//点击后让通知将消失
                .setContentTitle("用户["+ toModel.UserModelFromJson(str).getUsername()+"]发来消息")
                .setContentText(toModel.UserModelFromJson(str).getMessage())
                .setTicker("您有新的消息");
        //点击的意图ACTION是跳转到Intent
        Intent resultIntent = new Intent(this, Chat.class);
        resultIntent.putExtra("username", data.getUsername());
        resultIntent.putExtra("friend", toModel.UserModelFromJson(str).getUsername());
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(notifyId, mBuilder.build());
    }
}
