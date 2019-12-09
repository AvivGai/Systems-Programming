package bgu.spl.net.impl.BGSServer.Messages;

import java.util.List;

public class ACKFollow extends ACK {

    private short numOfUsers;
    private List<String> users;


    public ACKFollow(short opcode,short msgOpcode, short numOfUsers, List users){
        super(opcode,msgOpcode);
        this.numOfUsers=numOfUsers;
        this.users = users;
    }


    public short getNumOfUsers(){
        return this.numOfUsers;
    }

    public List<String> getUsers(){
    return users;
    }

}
