package SQLite;

/**
 * Created by Neoy on 16/10/15.
 */

public class SqlBean {
    private String userName;
    private String friend;
    private String time;
    private String Msg;

    public SqlBean(){
    }
    public SqlBean(String userName,String friend){
        this.userName = userName;
        this.friend = friend;
    }
    public int getFlag() {
        return Flag;
    }

    public void setFlag(int flag) {
        Flag = flag;
    }

    private int Flag;



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }
}
