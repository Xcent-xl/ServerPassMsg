package SQLite;

/**
 * Created by chen on 2016/9/7.
 */
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    /**
     * add persons
     * @param
     */
    public boolean add(SqlBean sb) {
        db.beginTransaction();  //开始事务
        boolean flag=false;
        try {

            db.execSQL("INSERT INTO Record VALUES(null, ?, ?, ?, ?, ?)"
                    , new Object[]{sb.getUserName(),sb.getFriend(),sb.getMsg(),sb.getTime(),sb.getFlag()});
            if (!existFriend(sb.getFriend())) {
                db.execSQL("INSERT INTO Friend VALUES(null, ?) "
                        , new Object[]{sb.getFriend()});
            }
                db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
        return flag;
    }
    public boolean existFriend(String friend){
        List<String> s = queryFriend();
        for (String f:s){
            if (friend.equals(f))
                return true;

        }
        return false;
    }
    public boolean deleteFriend(String friend) {
        db.beginTransaction();  //开始事务
        boolean flag=false;
        try {
            String[] whereArgs={friend};
            db.execSQL("DELETE FROM Friend WHERE userName=?",whereArgs);

            db.setTransactionSuccessful();  //设置事务成功完成
            flag = true;
        } finally {
            db.endTransaction();    //结束事务
        }
        return flag;
    }
    public boolean addFriend(String friend) {
        db.beginTransaction();  //开始事务
        boolean flag=false;
        try {

            db.execSQL("INSERT INTO Friend VALUES(null, ?)"
                    , new Object[]{friend});

            db.setTransactionSuccessful();  //设置事务成功完成
            flag = true;
        } finally {
            db.endTransaction();    //结束事务
        }
        return flag;
    }

    public List<String> queryFriend() {
        ArrayList<String> friends = new ArrayList<String>();
        Cursor c = queryTheCursorForFriend();
        while (c.moveToNext()) {
           friends.add((c.getString(c.getColumnIndex("userName"))));

        }
        c.close();
        return friends;
    }

    private Cursor queryTheCursorForFriend() {

        Cursor c = db.rawQuery("SELECT * FROM Friend",null);
        return c;
    }


    public List<SqlBean> query(SqlBean sb) {
        ArrayList<SqlBean> sbs = new ArrayList<SqlBean>();
        Cursor c = queryTheCursor(sb);
        while (c.moveToNext()) {
            SqlBean SB = new SqlBean();
            SB.setFriend((c.getString(c.getColumnIndex("Friend"))));
            SB.setUserName(c.getString(c.getColumnIndex("userName")));
            SB.setMsg(c.getString(c.getColumnIndex("Message")));
            SB.setTime(c.getString(c.getColumnIndex("Time")));
            SB.setFlag(c.getInt(c.getColumnIndex("Flag")));

            sbs.add(SB);
        }
        c.close();
        return sbs;
    }
    public Cursor queryTheCursor(SqlBean sb) {
        String[] whereArgs={String.valueOf(sb.getUserName()),String.valueOf(sb.getFriend())};
        Cursor c = db.rawQuery("SELECT * FROM Record WHERE userName =? AND friend =? ",whereArgs);
        return c;
    }

    public void closeDB() {
        db.close();
    }
}

