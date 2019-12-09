package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.BGSServer.DB.DataBase;
import bgu.spl.net.impl.BGSServer.Messages.Message;
import bgu.spl.net.srv.Server;

public class TPCMain {

    public static void main(String[] args) {
        DataBase DB= new DataBase();

        Server.threadPerClient(
               Integer.parseInt(args[0]), //port
                () -> new BGSProtocol<Message>(DB),//protocol factory
               BGSEncoderDecoder::new //message encoder decoder factory
        ).serve();
    }
}
