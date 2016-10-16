package Model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen on 2016/9/27.
 */
public class toModel {
    public static ArrayList<friend> friendModelListFromJson(String modelJson)
    {
        Gson g=new Gson();
        Type lt=new TypeToken<List<friend>>(){}.getType();
        ArrayList<friend> l=g.fromJson(modelJson,lt);
        return l;
    }
    public static friend friendModelFromJson(String modelJson)
    {
        Gson g=new Gson();
        Type lt=new TypeToken<friend>(){}.getType();
        friend l=g.fromJson(modelJson,lt);
        return l;
    }
    public static List<User> userModelListFromJson(String modelJson)
    {
        Gson g=new Gson();
        Type lt=new TypeToken<List<User>>(){}.getType();
        List<User> l=g.fromJson(modelJson,lt);
        return l;
    }
    public static User UserModelFromJson(String modelJson)
    {
        Gson g=new Gson();
        Type lt=new TypeToken<User>(){}.getType();
        User l=g.fromJson(modelJson,lt);
        return l;
    }
}
