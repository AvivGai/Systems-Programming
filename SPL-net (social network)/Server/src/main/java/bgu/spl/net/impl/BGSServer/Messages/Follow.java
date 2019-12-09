package bgu.spl.net.impl.BGSServer.Messages;

import java.util.List;

public class Follow extends Message {
    int follow;
    int numOfUsers;
    List nameList;

    public Follow(byte follow, int numOfUsers, List nameList){
        if(follow==0){
            this.follow=0;
        }
        else
            this.follow=1;
        this.numOfUsers=numOfUsers;
        this.nameList=nameList;
    }

    public int getFollow(){
        return this.follow;
    }

    public List getNameList(){
        return nameList;
    }

    public int getNumOfUsers(){
        return numOfUsers;
    }
}
