package bgu.spl.net.impl.BGSServer.DB;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DataBase {
    private ConcurrentHashMap<String,User> usersList;


    public DataBase() {
        this.usersList= new ConcurrentHashMap<>();
    }

    public boolean containsUserName(String s){
        synchronized (usersList){
            return usersList.containsKey(s);}
    }

    public void addUser(String s,User u){
        synchronized (usersList){
            usersList.put(s,u);}
    }

    public User getUserByName(String s){
        //maybe synchronized
        return usersList.get(s);
    }

    public User getUserById(int id){
        synchronized (usersList){
        Iterator it = usersList.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) it.next();
            User u = (User)pair.getValue();
            if(u.getId()==id){
                return u;
            }
        }
        return null;
    }}

//    public int getNumOfUsers(){
//        synchronized (usersList) {
//            return usersList.size();
//        }
//    }

    public List<String> getUserNameList() {
        List<String> userNameList = new LinkedList<>();
        synchronized (usersList){
        Iterator it = usersList.keySet().iterator();
        while (it.hasNext()) {
            String name = (String) it.next();
            userNameList.add(name);
        }
        return userNameList;
    }}

}
