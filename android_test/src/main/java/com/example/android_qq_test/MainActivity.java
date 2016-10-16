package com.example.android_qq_test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Login";

    private ApplicationData data;
    //定义登录界面用到的View变量
    private Button mLogin;
    private EditText mCount;

private String strCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setOnclick();
        data=(ApplicationData)getApplication();


    }
    protected void onStart(){
        super.onStart();
        if(data.getLogin()>0) {stopService(new Intent(MainActivity.this, SocketService.class));data.setLogin(data.getLogin()-1);}
    }
    /**
     * 设置View对象的点击事件
     */
    private void setOnclick() {
        mLogin.setOnClickListener(this);
    }

    /**
     * 初始化各种View对象
     */
    private void initView() {
        mLogin = (Button) findViewById(R.id.btn_login);
        mCount = (EditText) findViewById(R.id.edit_count);
        mCount.setText(MainActivity.this.getSharedPreferences("SP", MODE_PRIVATE).getString("username", ""));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                Log.v(TAG, "in btn_login");
                strCount = mCount.getText().toString();
                if(!strCount.equals("")) {
                    data.setUsername(strCount);
                    data.setLogin(data.getLogin() + 1);

                    SharedPreferences sp = MainActivity.this.getSharedPreferences("SP", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("username",data.getUsername());
                    editor.commit();

                    Log.e(TAG, "startService");
                    startService(new Intent(MainActivity.this, SocketService.class));

                }
                else Toast.makeText(getApplicationContext(), "登录名不能为空", Toast.LENGTH_SHORT).show();
                break;
        }
    }







}
