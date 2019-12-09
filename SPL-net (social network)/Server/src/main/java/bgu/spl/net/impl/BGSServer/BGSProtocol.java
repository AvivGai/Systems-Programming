package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DB.DataBase;
import bgu.spl.net.impl.BGSServer.DB.User;
import bgu.spl.net.impl.BGSServer.Messages.*;
import bgu.spl.net.impl.BGSServer.Messages.Error;

import java.util.LinkedList;
import java.util.List;

public class BGSProtocol<Message> implements BidiMessagingProtocol {
    private int id;
    private Connections connections;
    private DataBase DB;
    private boolean shouldTerminate;

    public BGSProtocol(DataBase DB){
        this.DB= DB;
    }

    @Override
    public void start(int connectionId, Connections connections) {
        this.id = connectionId;
        this.connections = connections;
        shouldTerminate = false;
    }

    @Override
    public void process(Object message) {
        Message m = (Message) message;
        if (m instanceof Register) {
            processRegister((Register) m);
        }
        if (m instanceof LogIn) {
            processLogIn((LogIn) m);
        }
        if (m instanceof LogOut) {
            processLogOut();
        }
        if (m instanceof Follow) {
            processFollow((Follow) m);
        }
        if (m instanceof Post) {
            processPost((Post) m);
        }
        if (m instanceof PM) {
            processPM((PM) m);
        }
        if (m instanceof UserList) {
            processUserList();
        }
        if (m instanceof Stat) {
            processStat((Stat) m);
        }

    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    private void processRegister(Register m) {
        synchronized (DB){ //so 2 threads or more wouldn't be able to register with the same userName
        if (DB.containsUserName(m.getUserName())) {
            connections.send(id, new Error((short) 11, (short) 1));
        } else {
            User u = new User(m.getUserName(), m.getPassword());
            DB.addUser(m.getUserName(), u);
            connections.send(id, new ACK((short) 10, (short) 1));
        }}
    }

    private void processLogIn(LogIn m) {
        String userName = m.getUserName();
        User u = DB.getUserByName(userName);
        if(u==null){
            connections.send(id, new Error((short) 11, (short) 2));
        }
        else {
            synchronized (u) { // so 2 threads won't log in with same user.
                if (DB.getUserById(id) != null || !(u.getPassword()).equals(m.getPassword()) || u.isLoggedIn()) {
                    connections.send(id, new Error((short) 11, (short) 2));
                } else {
                    u.logIn(id);
                    connections.send(id, new ACK((short) 10, (short) 2));
                    while (u.getUnsentNotification() != null && !u.getUnsentNotification().isEmpty() && u.isLoggedIn()) {
                        connections.send(id, u.getUnsentNotification().poll());
                    }
                }
            }
        }
    }

    private void processLogOut() {
        User u = DB.getUserById(id);
        if (u == null || !u.isLoggedIn()) {
            connections.send(id, new Error((short) 11, (short) 3));
        } else {
            u.logOut();
            this.shouldTerminate = true;
            connections.send(id, new ACK((short) 10, (short) 3));
            connections.disconnect(id);
        }
    }

    private void processFollow(Follow m) {
        User u = DB.getUserById(id);
        if (u == null || !u.isLoggedIn()) {
            connections.send(id, new Error((short) 11, (short) 4));
        }
        int numOfUsers = m.getNumOfUsers();
        short numOfSuccessful = 0;
        List usersNames = new LinkedList();
        List<String> names = m.getNameList();
        for (String name : names) {
            if (DB.getUserByName(name)!=null && m.getFollow() == 0 && !u.isUserOnMyList(name)) {
                u.addToFollow(name);
                numOfSuccessful++;
                usersNames.add(name);
                User following = DB.getUserByName(name);
                following.addFollower(u.getUserName());
            }
            if (DB.getUserByName(name)!=null && m.getFollow() == 1 && u.isUserOnMyList(name)) {
                u.removeFollowing(name);
                numOfSuccessful++;
                usersNames.add(name);
                User unfollowing = DB.getUserByName(name);
                unfollowing.removeFollower(u.getUserName());
            }
        }
        if (numOfSuccessful == 0 && numOfUsers != 0) {
            connections.send(id, new Error((short) 11, (short) 4));
        } else {
            connections.send(id, new ACKFollow((short) 10, (short) 4, numOfSuccessful, usersNames));
        }
    }


    private void processPost(Post m) {
        User u = DB.getUserById(id);
        if (u == null || !u.isLoggedIn()) {
            connections.send(id, new Error((short) 11, (short) 5));
        } else {
            String post = m.getContent();
            List<String> toSend = new LinkedList<>();
            int i = 0;
            while (i < post.length()) {
                int first = post.indexOf("@", i);
                int last = post.indexOf(" ", first);
                String s = "";
                if (last != -1) {
                    s = post.substring(first + 1, last);
                    i = last + 1;
                if(first==-1){
                    i=post.length();
                }
                } if(last==-1) {
                    s = post.substring(first + 1);
                    i=post.length();
                }
                if(!toSend.contains(s)){
                    toSend.add(s);}

            }
            List<String> followers = u.getFollowers();
            synchronized (followers) { // so other threads won't change the followers list before we send notifications
                for(String follower: followers){
                    if(!toSend.contains(follower)){
                        toSend.add(follower);
                    }
                }
                u.increasNumOfPosts();
                connections.send(id, new ACK((short) 10, (short) 5));
                for (String user : toSend) {
                    User recipient = DB.getUserByName(user);
                    if (recipient != null) {
                        Notification n = new Notification((short) 9, (byte)1, u.getUserName(), post);
                        synchronized (recipient){
                        boolean wasSent = connections.send(recipient.getId(), n);
                        if (!wasSent) {
                            recipient.addNotification(n);
                        }}
                    }
                }
            }
        }
    }

    private void processPM(PM m) {
        User u = DB.getUserById(id);
        User recipient = DB.getUserByName(m.getRecipient());
        if (u == null || !u.isLoggedIn() || recipient == null) {
            connections.send(id, new Error((short) 11, (short) 6));
        } else {
            connections.send(id, new ACK((short) 10, (short) 6));
            Notification n = new Notification((short) 9, (byte)0, u.getUserName(), m.getContent());
            synchronized (recipient){
            boolean wasSent = connections.send(recipient.getId(), n);
            if (!wasSent) {
                recipient.addNotification(n);
            }
        }}
    }

    private void processUserList() {
        User u = DB.getUserById(id);
        if (u == null || !u.isLoggedIn()) {
            connections.send(id, new Error((short) 11, (short) 7));
        } else {
            List<String> userNameList = DB.getUserNameList();
            short numOfUsers = (short)userNameList.size();
            connections.send(id, new ACKUsersList((short) 10, (short) 7, numOfUsers, userNameList));
        }
    }

    private void processStat(Stat m) {
        User requestingStat = DB.getUserById(id);
        User statAbout = DB.getUserByName(m.getUserName());
        if (requestingStat == null || !requestingStat.isLoggedIn() || statAbout==null) {
            connections.send(id, new Error((short) 11, (short) 8));
        } else {
            short numOfPosts = (short)statAbout.getNumOfPosts();
            short numOfFollowers = (short)statAbout.getNumOfFollowers();
            short numOfFollowing = (short)statAbout.getNumOfFollowing();
            connections.send(id, new ACKStat((short) 10, (short) 8, numOfPosts, numOfFollowers, numOfFollowing));
        }
    }
}
