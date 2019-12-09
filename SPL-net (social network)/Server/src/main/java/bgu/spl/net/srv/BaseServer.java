package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;

public abstract class BaseServer<T> implements Server<T> {

    private final int port;
    private final Supplier<BidiMessagingProtocol<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> encdecFactory;
    private ServerSocket sock;
    private ConnectionsImpl<T> connections;

    public BaseServer(
            int port,
            Supplier<BidiMessagingProtocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> encdecFactory) {
        this.port = port;
        this.protocolFactory = protocolFactory;
        this.encdecFactory = encdecFactory;
		this.sock = null;
		this.connections=new ConnectionsImpl<>();
    }

    @Override
    public void serve() {

        try (ServerSocket serverSock = new ServerSocket(port)) {
            this.sock = serverSock; //just to be able to close
            int id=0;
            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSock = serverSock.accept();
                BlockingConnectionHandler<T> handler = new BlockingConnectionHandler<>(clientSock,encdecFactory.get(),protocolFactory.get(),connections,id);
                connections.add(id,handler);
                execute(handler);
                id++;
            }
        } catch (IOException ex) {
        }
    }

    @Override
    public void close() throws IOException {
		if (sock != null)
			sock.close();
    }

    protected abstract void execute(BlockingConnectionHandler<T>  handler);

    //for thread per client implementation
//    public static <T> BaseServer<T> threadPerClient(
//            int port,
//            Supplier<BidiMessagingProtocol<T>> protocolFactory,
//            Supplier<MessageEncoderDecoder<T>> encoderDecoderFactory) {
//
//        return new BaseServer<T>(port, protocolFactory, encoderDecoderFactory) {
//            @Override
//            protected void execute(BlockingConnectionHandler<T> handler) {
//                new Thread(handler).start();
//            }
//        };
//    }


}
