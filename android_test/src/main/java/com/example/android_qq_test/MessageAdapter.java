package com.example.android_qq_test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Guodetm on 2016/9/22.
 */
public class MessageAdapter extends BaseAdapter {
    public static boolean isMe = true;
    private Context context;
    private String message;
    public void MessageAdapter( Context context,String message){
        this.context = context;
        this.message = message;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.list_item,null);
            viewHolder.txt = (TextView) view.findViewById(R.id.txtMessage);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.txt.setText(message);
        return view;
    }
    public class ViewHolder{
        TextView txt;
    }
}
