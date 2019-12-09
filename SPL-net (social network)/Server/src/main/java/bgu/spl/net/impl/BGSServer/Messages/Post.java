package bgu.spl.net.impl.BGSServer.Messages;

public class Post extends Message{
    String content;

    public Post(String content){
        this.content=content;
    }

    public String getContent(){
        return this.content;
    }
}
