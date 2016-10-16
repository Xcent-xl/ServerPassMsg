package com.example.android_qq_test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.User;
import Model.friend;
import Model.toModel;
import ProjectData.Data;
import SQLite.DBManager;

public class friendList extends AppCompatActivity {

    private ListView friendList;
    private SimpleAdapter adapter = null;
    private String TAG = "getFriendList";
    private String username = "";
    SocketReceiver receiver;
    private OutputStream outStream;
    private ApplicationData data;
    ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
    ArrayList<friend> nowFriendList = new ArrayList<friend>();
    ArrayList<friend> getOnLineFriendList= new ArrayList<friend>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        data = (ApplicationData) getApplication();
        initView();
        init();
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        receiver = new SocketReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Data.SOCKER_ACTION);
        registerReceiver(receiver, filter);
        //new Thread(sendThread).start();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        new Thread(sendThread).start();
    }

    public void set(View view) {

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "friendList Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.android_qq_test/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "friendList Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.android_qq_test/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    /**
     * 获取广播数据
     *
     * @author chen
     */
    public class SocketReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            Log.e(TAG, "onReceive");
            Log.e(TAG, intent.getStringExtra("msg"));
            String msg = intent.getStringExtra("msg");
            if (toModel.UserModelFromJson(msg).getFlag().equals("2")) {
                Log.e(TAG, "in flag 2");
                getOnLineFriendList=toModel.friendModelListFromJson(toModel.UserModelFromJson(msg).getMessage());
                //getOnLineFriendList=toModel.friendModelListFromJson(toModel.UserModelFromJson(msg).getMessage());
                initList();
            }
            if (toModel.UserModelFromJson(msg).getFlag().equals("4")) {
                updateList(toModel.UserModelFromJson(msg).getUsername(),"4");
            }
            if (toModel.UserModelFromJson(msg).getFlag().equals("5")) {
                updateList(toModel.UserModelFromJson(msg).getUsername(),"5");
            }
            if (toModel.UserModelFromJson(msg).getFlag().equals("3")) {
                User userReceiver = toModel.UserModelFromJson(msg);
                if (!userReceiver.getUsername().equals(username)) {
                    Toast.makeText(getApplicationContext(), "有人来消息了！\n[" + userReceiver.getUsername() + "]: " + userReceiver.getMessage(), Toast.LENGTH_SHORT).show();
               }
            }
        }
    }

    Runnable sendThread = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG, "sendThread");
            // TODO Auto-generated method stub
            byte[] sendBuffer = null;

            try {
                User order = new User();
                order.setUsername(username);
                order.setFlag("2");
                order.setFriend("");
                order.setMessage("");
                sendBuffer = User.ConvertToJson(order).getBytes("UTF-8");
                Log.e(TAG, "getFriendList: " + User.ConvertToJson(order));
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
                Log.e(TAG, "sendThread IOException: ", e);
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };
    private void init() {
        friendList = (ListView) findViewById(R.id.friendList);
        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Map<String, String> map = (Map<String, String>) friendList.this.adapter
                        .getItem(position);
                final String info = map.get("info");
                Intent intent = new Intent();
                intent.setClass(friendList.this, Chat.class);
                //Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
                intent.putExtra("username", username);
                intent.putExtra("friend", info);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        friendList = (ListView) findViewById(R.id.friendList);
        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Map<String, String> map = (Map<String, String>) friendList.this.adapter
                        .getItem(position);
                final String info = map.get("info");
                Intent intent = new Intent();
                intent.setClass(friendList.this, Chat.class);
                //Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
                intent.putExtra("username", username);
                intent.putExtra("friend", info);
                startActivity(intent);
            }
        });
    }
    private boolean getExistOnLineFriend(String friend){
        for(friend f:getOnLineFriendList)
        {
            Log.e(TAG, "on line好友:" + f.getFriend());
            //Log.d("HUOQV", "on line好友:" + f.getFriend());
            if(f.getFriend().equals(friend)) {
                getOnLineFriendList.remove(f);
                return true;
            }
        }
        return false;
    }
    private void initList() {
        DBManager dbm = new DBManager(this);
        friend n;
        List<String> localFriend = dbm.queryFriend();
        Log.e(TAG, "查询了本地好友");
        for (String s:localFriend) {
            Log.e(TAG, "本地好友:" + s);
        }
        for (int i = 0;i<localFriend.size();i++) {
            if (getExistOnLineFriend(localFriend.get(i))) {
                nowFriendList.add(new friend(data.getUsername(),localFriend.get(i),1));
            }
            else{
                nowFriendList.add(new friend(data.getUsername(),localFriend.get(i),0));
            }
        }
        for(friend f:getOnLineFriendList)
        {
            Log.e(TAG, "on line好友:" + f.getFriend());
            nowFriendList.add(new friend(data.getUsername(),f.getFriend(),1));
        }
        list.clear();
        int number = 1;
        HashMap<String, String> me = new HashMap<String, String>();
        me.put("name", "本机账号");
        me.put("info", username);
        list.add(me);
        for (friend f : nowFriendList) {
            if (!f.getFriend().equals(username)) {
                HashMap<String, String> map = new HashMap<String, String>();
                if (f.getOnLineFlag() == 1){
                    map.put("name", "好友" + number+"(在线)");
                }else if (f.getOnLineFlag() == 0){
                    map.put("name", "好友" + number+"(离线)");
                }else{
                    map.put("name", "好友" + number);
                }
                map.put("info", f.getFriend());
                list.add(map);
                number++;
            }
        }
        adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2,
                new String[]{"name", "info"}, new int[]{android.R.id.text1, android.R.id.text2});
        friendList.setAdapter(adapter);
    }
    private void updateList(String username,String lineFlag)
    {
        //4:onLine 5:offLine
        DBManager dbm = new DBManager(this);
        int flag = 0;
            for (int i = 0;i<nowFriendList.size();i++) {
                if (username.equals(nowFriendList.get(i))) ;
                flag = 1;
                break;
            }

        HashMap<String, String> map = new HashMap<String, String>();
        if (flag == 1) {
            map.put("name", "好友" + list.size() + "(在线)");

        }else{
            map.put("name", "好友" + list.size() + ":");

        }
        map.put("info", username);
        int addFlag = 1;
        for (int i = 0;i<list.size();i++){
            if (map.get("info").equals(list.get(i).get("info"))){
                if (lineFlag.equals("4"))
                    list.get(i).put("name","好友"+i+"(在线)");
                else if (lineFlag.equals("5"))
                    list.get(i).put("name","好友"+i+"(离线)");
                addFlag = 0;
            }
        }
        if (addFlag == 1)
            list.add(map);
        adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2,
                new String[]{"name", "info"}, new int[]{android.R.id.text1, android.R.id.text2});
        friendList.setAdapter(adapter);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "socket service destroy!");
        try {
            data.getmSocket().close();
            unregisterReceiver(receiver);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
