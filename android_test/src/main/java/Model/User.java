package Model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Guodetm on 2016/9/22.
 */
public class User {
    private String username;
    private String message;
    private String friend;
    private String flag;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;
    //1: 确认登录 -1：登录失败 2：消息转发

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public User(String username, String message, String friend,String flag){
        this.username = username;
        this.message = message;
        this.friend=friend;
        this.flag=flag;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public User(){
    }
    public static String ConvertToJson(User user) {
        String jsonStr = "";
        JSONObject JsonObject = new JSONObject();
        try {
            JsonObject.put("username", user.getUsername());
            JsonObject.put("message", user.getMessage());
            JsonObject.put("friend", user.getFriend());
            JsonObject.put("flag", user.getFlag());
            jsonStr = JsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonStr;
    }
}
