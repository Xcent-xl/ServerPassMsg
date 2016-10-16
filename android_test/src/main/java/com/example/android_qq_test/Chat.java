package com.example.android_qq_test;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.User;
import Model.toModel;
import ProjectData.Data;
import SQLite.DBManager;
import SQLite.SqlBean;

/**
 * Created by Guodetm on 2016/9/22.
 */
public class Chat extends Activity implements View.OnClickListener{
    private EditText editMessage;
    private Button btnMessage;
    private static final String TAG = "Chat";
    private User user=new User();
    private OutputStream outStream;
    private String friend="",username="";
    private ApplicationData data;

    //test write
    private ListView test;
    private SimpleAdapter adapter = null;
    SocketReceiver receiver;
    private DBManager dbm;
    ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data=(ApplicationData)getApplication();
        Log.e(TAG,"In Chat");
        setContentView(R.layout.chat);
        Intent intent = getIntent();
        friend=intent.getStringExtra("friend");
        username=intent.getStringExtra("username");
        data.setNowFriend(friend);
        Log.e(TAG, "username:"+username+"/friend:"+friend);
        user.setUsername(username);
        user.setFriend(friend);
        user.setFlag("3");
        initView();
        setOnclick();
        test=(ListView) findViewById(R.id.messageList);
        receiver=new SocketReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Data.SOCKER_ACTION);
        registerReceiver(receiver, filter);

        dbm = new DBManager(this);

        SqlBean sb = new SqlBean(username,friend);
        addMessageFromSql(sb);
    }
    /**
     * 获取广播数据
     *
     * @author chen
     *
     */
    public class SocketReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            Log.e(TAG, "onReceive");
            Log.e(TAG, intent.getStringExtra("msg"));
            String msg = intent.getStringExtra("msg");
            if(toModel.UserModelFromJson(msg).getFlag().equals("3")) {
                User userReceiver = toModel.UserModelFromJson(msg);

                SqlBean sb = new SqlBean(data.getUsername(), userReceiver.getUsername());
                sb.setFlag(2);
                sb.setMsg(userReceiver.getMessage());
                sb.setTime(userReceiver.getDate());
                dbm.add(sb);


                if (userReceiver.getFriend().equals(username)) addMessage(friend, userReceiver.getMessage());
                else if (userReceiver.getUsername().equals(username)) addMessage("系统消息", userReceiver.getMessage());
                else if (!userReceiver.getUsername().equals(friend)) Toast.makeText(getApplicationContext(), "有人来消息了！\n["+friend+"]: "+userReceiver.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
   Runnable sendThread = new Runnable() {
       @Override
       public void run() {
           Log.e(TAG,"sendThread");
           // TODO Auto-generated method stub
           byte[] sendBuffer = null;

           try {
               sendBuffer = User.ConvertToJson(user).getBytes("UTF-8");
               Log.e(TAG,"Chat send Data:"+User.ConvertToJson(user));
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
   };
    private void setOnclick() {
        btnMessage.setOnClickListener(this);
    }

    private void initView() {
        btnMessage = (Button) findViewById(R.id.send);
        editMessage = (EditText) findViewById(R.id.editMessage);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.send:
                if(!editMessage.getText().toString().equals("")) {
                    addMessage("我", editMessage.getText().toString());
                    user.setMessage(editMessage.getText().toString());
                    String time = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒").format(new Date());
                    user.setDate(time);
                    new Thread(sendThread).start();
                    SqlBean sb = new SqlBean(data.getUsername(), friend);
                    sb.setFlag(1);
                    sb.setMsg(editMessage.getText().toString());
                    sb.setTime(time);
                    dbm.add(sb);
                    editMessage.setText("");
                    break;
                }
        }
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        data.setNowFriend("");
        unregisterReceiver(receiver);
    }
    private void addMessage(String friend,String msg){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name",friend+":"+msg);
            map.put("info", new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒").format(new Date()));
            list.add(map);
        adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2,
                new String[]{"name", "info"}, new int[]{android.R.id.text1, android.R.id.text2});
        test.setAdapter(adapter);
    }
    private void addMessageFromSql(SqlBean sb){
        List<SqlBean> results = new ArrayList<SqlBean>();
        results = dbm.query(sb);
        for (SqlBean result:results){
            HashMap<String, String> map = new HashMap<String, String>();
            if (result.getFlag() == 2)//收到
            {
                map.put("name", result.getFriend() + ":" + result.getMsg());
                map.put("info", result.getTime());
                list.add(map);
            }else if (result.getFlag() == 1){
                map.put("name", "我:" + result.getMsg());
                map.put("info", result.getTime());
                list.add(map);
            }
            adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2,
                    new String[]{"name", "info"}, new int[]{android.R.id.text1, android.R.id.text2});
            test.setAdapter(adapter);
        }

    }
}


