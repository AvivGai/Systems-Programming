package bgu.spl.net.impl.BGSServer.DB;

import bgu.spl.net.impl.BGSServer.Messages.Notification;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class User {
    private int id;
    private String userName;
    private String password;
    private boolean loggedIn;
    private List<String> following;
    private List<String> followers;
    private Queue<Notification> unsentNotification;
    private Integer numOfPosts;

    public User(String userName,String password){
        this.userName=userName;
        this.password=password;
        this.loggedIn=false;
        following= new LinkedList();
        followers= new LinkedList<>();
        unsentNotification= new LinkedBlockingQueue<>();
        numOfPosts = 0;
        this.id=-1;
    }

    public boolean isLoggedIn(){
        return this.loggedIn;
    }

    public void logIn(int id){
        loggedIn=true;
        this.id=id;
    }

    public void logOut(){loggedIn = false;
    this.id=-1;}

    public boolean isUserOnMyList(String user){
        return following.contains(user);
    }

    public void addToFollow(String user){
        following.add(user);
    }

    public void addFollower(String user){
        synchronized (followers){
        followers.add(user);}}

    public void removeFollowing(String s){
        following.remove(s);
    }

    public void removeFollower(String s){
        synchronized (followers){
        followers.remove(s);
    }}

    public String getPassword(){
        return this.password;
    }

    public String getUserName(){
        return this.userName;
    }

    public int getId(){
        return  id;
    }

    public List getFollowers(){
        synchronized (followers){
        return this.followers;
    }}

    public void addNotification(Notification n){
        synchronized (unsentNotification){
        unsentNotification.add(n);
    }}

    public Queue<Notification> getUnsentNotification(){
        return this.unsentNotification;
    }

    public void increasNumOfPosts(){
        synchronized (numOfPosts){
        numOfPosts++;}
    }

    public int getNumOfPosts(){
        synchronized (numOfPosts){
        return numOfPosts;
    }}

    public int getNumOfFollowing() {
        return following.size();
    }

    public int getNumOfFollowers() {
        synchronized (followers){
        return followers.size();}
    }
}


