package com.example.android_qq_test;

import android.app.Application;
import android.util.Log;

import java.net.Socket;
import java.util.logging.Handler;

/**
 * Created by chen on 2016/9/28.
 */
public class ApplicationData extends Application {
    public int login;
    private String username;
    private Socket mSocket;
    private String nowFriend;

    public String getNowFriend() {
        return nowFriend;
    }

    public void setNowFriend(String nowFriend) {
        this.nowFriend = nowFriend;
    }

    private static String TAG="ApplicationData";
    @Override
    public void onCreate() {login=0;username="";super.onCreate();}
    public int getLogin(){Log.e(TAG, "login:"+login);return login; }
    public void setLogin(int login){Log.e(TAG, "login:"+login);this.login=login;}
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public Socket getmSocket() {
        return mSocket;
    }
    public void setmSocket(Socket mSocket) {
        this.mSocket = mSocket;
    }
}
