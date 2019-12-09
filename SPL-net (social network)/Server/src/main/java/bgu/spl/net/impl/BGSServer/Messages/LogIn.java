package bgu.spl.net.impl.BGSServer.Messages;

public class LogIn extends Message{
    private String userName;
    private String password;

    public LogIn(String userName,String password){
        this.userName=userName;
        this.password=password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
