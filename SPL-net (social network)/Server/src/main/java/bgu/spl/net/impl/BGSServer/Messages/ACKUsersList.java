package bgu.spl.net.impl.BGSServer.Messages;

import java.util.List;

public class ACKUsersList extends ACK {

    private short numOfUsers;
    private List<String> users;

    public ACKUsersList(short opcode, short userListOpcode, short numOfUsers, List<String> users) {
        super(opcode, userListOpcode);
        this.numOfUsers = numOfUsers;
        this.users = users;
    }

    public short getNumOfUsers(){return numOfUsers;}

    public List<String> getUsers(){return users;}
}
