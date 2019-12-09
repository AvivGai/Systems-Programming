package bgu.spl.net.impl.BGSServer.Messages;

public class Error extends Message {
    private short opcode;
    private short msgOpcode;

    public Error(short opcode, short msgOpcode){
        this.msgOpcode = msgOpcode;
        this.opcode=opcode;
    }

    public short getOpcode(){
        return this.opcode;
    }

    public short getMsgOpcode(){
        return this.msgOpcode;
    }
}
