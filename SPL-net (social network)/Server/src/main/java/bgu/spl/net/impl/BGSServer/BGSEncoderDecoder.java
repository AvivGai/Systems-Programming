package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.BGSServer.Messages.*;
import bgu.spl.net.impl.BGSServer.Messages.Error;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BGSEncoderDecoder implements MessageEncoderDecoder<Message> {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    boolean endOfMessage = false;
    short opcode = 0;
    int counterOfZeros = 0;

    public BGSEncoderDecoder() {
    }


    @Override
    public Message decodeNextByte(byte nextByte) {
        pushByte(nextByte);
        if (len == 2) {
            this.opcode = bytesToShort(Arrays.copyOfRange(bytes, 1, 3));
        }
        if (opcode == (short) 1 || opcode == (short) 2 || opcode == (short) 6) {
            if (nextByte == '\0') {
                counterOfZeros++;
            }
            if (counterOfZeros == 2) {
                endOfMessage = true;
            }
        }
        if (opcode == 3 || opcode == 7) {
            endOfMessage = true;
        }
        if (opcode == 4) {
            if (len >= 6) {
                byte[] usersNum = Arrays.copyOfRange(bytes, 4, 6);
                short numOfUsers = bytesToShort(usersNum);
                if (nextByte == '\0') {
                    counterOfZeros++;
                }
                if (counterOfZeros == numOfUsers) {
                    endOfMessage = true;
                }
            }
        }
        if (opcode == 5 || opcode == 8) {
            if (nextByte == '\0') {
                counterOfZeros++;
            }
            if (counterOfZeros == 1) {
                endOfMessage = true;
            }
        }
        if (endOfMessage) {
            counterOfZeros = 0;
            return popString();
        } else
            return null; //not a message yet
    }

    @Override
    public byte[] encode(Message message) {
        byte[] messageArray = null;
        if (message instanceof Error) {
            messageArray = encodeError(message);
        }
        if (message instanceof Notification) {
            messageArray = encodeNotification(message);
        }
        if (message instanceof ACK) {
            messageArray = encodeACK(message);
        }
        return messageArray;
    }

    //ERROR MESSAGE
    private byte[] encodeError(Message message) {
        Error e = (Error) message;
        byte[] opcode = shortToBytes(e.getOpcode());
        byte[] msgOpcode = shortToBytes(e.getMsgOpcode());
        byte[] messageArray = new byte[4];
        System.arraycopy(opcode, 0, messageArray, 0, opcode.length);
        System.arraycopy(msgOpcode, 0, messageArray, opcode.length, msgOpcode.length);
        return messageArray;
    }

    //NOTIFICATION MESSAGE
    private byte[] encodeNotification(Message message) {
        Notification n = (Notification) message;
        byte[] opcode = shortToBytes(n.getOpcode());
        byte type = n.getPrivateOrPublic();
        byte[] user = n.getUser().getBytes();
        byte[] post = n.getPost().getBytes();
        byte[] messageArray = new byte[opcode.length + user.length + post.length + 3];
        System.arraycopy(opcode, 0, messageArray, 0, opcode.length);
        messageArray[2] = type;
        System.arraycopy(user, 0, messageArray, 3, user.length);
        messageArray[user.length + 3] = (byte) 0;
        System.arraycopy(post, 0, messageArray, user.length + 4, post.length);
        messageArray[messageArray.length - 1] = (byte) 0;
        return messageArray;
    }

    //ACK MESSAGE
    private byte[] encodeACK(Message message) {
        if (message instanceof ACKFollow) {
            ACKFollow a = (ACKFollow) message;
            byte[] opcode = shortToBytes(a.getOpcode());
            byte[] msgOpcode = shortToBytes(a.getMsgOpcode());
            byte[] numOfUsers = shortToBytes(a.getNumOfUsers());
            List<String> users = a.getUsers();
            byte[] usersArray = StringListToByteArray(users);
            byte[] messageArray = new byte[6 + usersArray.length];
            System.arraycopy(opcode, 0, messageArray, 0, opcode.length);
            System.arraycopy(msgOpcode, 0, messageArray, 2, msgOpcode.length);
            System.arraycopy(numOfUsers, 0, messageArray, 4, numOfUsers.length);
            System.arraycopy(usersArray, 0, messageArray, 6, usersArray.length);
            return messageArray;
        }
        if (message instanceof ACKStat) {
            ACKStat a = (ACKStat) message;
            byte[] opcode = shortToBytes(a.getOpcode());
            byte[] msgOpcode = shortToBytes(a.getMsgOpcode());
            byte[] numOfPosts = shortToBytes(a.getNumOfPosts());
            byte[] numOfFollowers = shortToBytes(a.getNumOfFollowers());
            byte[] numOfFollowing = shortToBytes(a.getNumOfFollowing());
            byte[] messageArray = new byte[10];
            System.arraycopy(opcode, 0, messageArray, 0, opcode.length);
            System.arraycopy(msgOpcode, 0, messageArray, 2, msgOpcode.length);
            System.arraycopy(numOfPosts, 0, messageArray, 4, numOfPosts.length);
            System.arraycopy(numOfFollowers, 0, messageArray, 6, numOfFollowers.length);
            System.arraycopy(numOfFollowing, 0, messageArray, 8, numOfFollowing.length);
            return messageArray;
        }
        if (message instanceof ACKUsersList) {
            ACKUsersList a = (ACKUsersList) message;
            byte[] opcode = shortToBytes(a.getOpcode());
            byte[] msgOpcode = shortToBytes(a.getMsgOpcode());
            byte[] numOfUsers = shortToBytes(a.getNumOfUsers());
            List<String> users = a.getUsers();
            byte[] usersArray = StringListToByteArray(users);
            byte[] messageArray = new byte[6 + usersArray.length];
            System.arraycopy(opcode, 0, messageArray, 0, opcode.length);
            System.arraycopy(msgOpcode, 0, messageArray, 2, msgOpcode.length);
            System.arraycopy(numOfUsers, 0, messageArray, 4, numOfUsers.length);
            System.arraycopy(usersArray, 0, messageArray, 6, usersArray.length);
            return messageArray;
        } else {
            ACK a = (ACK) message;
            byte[] opcode = shortToBytes(a.getOpcode());
            byte[] msgOpcode = shortToBytes(a.getMsgOpcode());
            byte[] messageArray = new byte[4];
            System.arraycopy(opcode, 0, messageArray, 0, opcode.length);
            System.arraycopy(msgOpcode, 0, messageArray, opcode.length, msgOpcode.length);
            return messageArray;
        }
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        len++;
        bytes[len] = nextByte;

    }

    private Message popString() {
        Message m = null;
        if (opcode == 1 || opcode == 2) { //Message type is Register or Log In
            m = popStringRegisterOrLogIn();
        }
        if (opcode == 3) { //Message type is Log Out
            m = new LogOut();
        }
        if (opcode == 4) { //Message type is Follow
            m = popStringFollow();
        }
        if (opcode == 5) { //Message type is Post Message
            String content = new String(bytes, 3, len - 3, StandardCharsets.UTF_8);
            m = new Post(content);
        }
        if (opcode == 6) { //Message type is PM
            m = popStringPM();
        }
        if (opcode == 7) { //Message type is User List
            m = new UserList();
        }
        if (opcode == 8) { //Message type is Stat
            String username = new String(bytes, 3, len - 3, StandardCharsets.UTF_8);
            m = new Stat(username);
        }
        len = 0;
        opcode = 0;
        endOfMessage = false;
        return m;
    }

    private Message popStringRegisterOrLogIn() {
        int i = findIndex((byte) '\0', 3);
        String userName = new String(bytes, 3, i - 3);
        String password = new String(bytes, i + 1, len - i - 1);
        if (opcode == 1) {
            return new Register(userName, password);
        } else {
            return new LogIn(userName, password);
        }
    }

    private Message popStringFollow() {
        byte follow= bytes[3];
//        String follow = new String(bytes, 3, 1, StandardCharsets.UTF_8);
        byte[] usersNum = Arrays.copyOfRange(bytes, 4, 6);
        short numOfUsers = bytesToShort(usersNum);
        List<String> userList = new LinkedList<>();
        String s = "";

        for (int i = 6; i <= len; i++) {
            if (bytes[i] == 0) {
                userList.add(s);
                s = "";
            } else {
                String c = new String(bytes, i, 1, StandardCharsets.UTF_8);
                s = s + c;
            }
        }
        return new Follow(follow, numOfUsers, userList);
    }

    private Message popStringPM() {
        int i = findIndex((byte) 0, 3);
        String username = new String(bytes, 3, i - 3, StandardCharsets.UTF_8);
        String content = new String(bytes, i + 1, len - i - 1, StandardCharsets.UTF_8);
        return new PM(username, content);
    }

    //converts list of users names to a byte array of those names, separated with 0.
    private byte[] StringListToByteArray(List<String> l) {
        int numBytes = 0;
        for (String str : l)
            numBytes += str.getBytes().length;
        List<Byte> byteList = new ArrayList<>();

        for (String str : l) {
            byte[] currentByteArr = str.getBytes();
            for (byte b : currentByteArr) {
                byteList.add(b);
            }
            byteList.add((byte) 0);
        }
        byte[] usersArray = new byte[byteList.size()];
        int i = 0;
        for (byte b : byteList) {
            usersArray[i] = b;
            i++;
        }
        return usersArray;
    }

    public short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }

    public byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte) ((num >> 8) & 0xFF);
        bytesArr[1] = (byte) (num & 0xFF);
        return bytesArr;
    }

    //find the first index of a specific byte in a byte array. return -1 if not found.
    private int findIndex(byte b, int indexFrom) {
        // find length of array
        int len = this.len;
        int i = indexFrom;

        // traverse in the array
        while (i <= len) {
            // if the i-th element is t
            // then return the index
            if (bytes[i] == b) {
                return i;
            } else {
                i = i + 1;
            }
        }
        return -1;
    }
}
