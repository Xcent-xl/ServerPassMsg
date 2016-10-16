package Model;

/**
 * Created by chen on 2016/9/27.
 */
public class friend {
    private String username;
    private String friend;
    public int getOnLineFlag() {
        return onLineFlag;
    }

    public void setOnLineFlag(int onLineFlag) {
        this.onLineFlag = onLineFlag;
    }

    private int onLineFlag;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }
    public friend(String username,String friend,int onLineFlag)
    {
        this.username=username;
        this.friend=friend;
        this.onLineFlag=onLineFlag;
    }
}
