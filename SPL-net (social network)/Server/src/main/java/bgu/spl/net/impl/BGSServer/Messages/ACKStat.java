package bgu.spl.net.impl.BGSServer.Messages;

public class ACKStat extends ACK{

    private short numOfPosts;
    private short numOfFollowers;
    private short numOfFollowing;


    public ACKStat(short opcode, short msgOpcode, short numOfPosts, short numOfFollowers, short numOfFollowing){
        super(opcode,msgOpcode );
        this.numOfPosts = numOfPosts;
        this.numOfFollowers = numOfFollowers;
        this.numOfFollowing = numOfFollowing;
    }

    public short getNumOfPosts(){return numOfPosts;}

    public short getNumOfFollowers() {
        return numOfFollowers;
    }

    public short getNumOfFollowing() {
        return numOfFollowing;
    }
}
