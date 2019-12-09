package bgu.spl.net.impl.BGSServer.Messages;

public class Stat extends Message {
    String userName;

    public Stat(String userName){
        this.userName=userName;
    }

    public String getUserName(){
        return userName;
    }

}
