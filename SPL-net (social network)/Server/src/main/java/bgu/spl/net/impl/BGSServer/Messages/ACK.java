package bgu.spl.net.impl.BGSServer.Messages;

public class ACK extends Message{
    protected short opcode;
    protected short msgOpcode;

   public ACK(short opcode, short msgOpcode){
        this.opcode=opcode;
        this.msgOpcode = msgOpcode;
    }

    public short getOpcode(){
       return this.opcode;
    }

    public short getMsgOpcode(){
       return this.msgOpcode;
    }








}
