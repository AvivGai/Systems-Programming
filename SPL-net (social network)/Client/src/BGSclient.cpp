//
// Created by avga@wincs.cs.bgu.ac.il on 12/28/18.
//

#include <iostream>
#include <thread>
#include <boost/algorithm/string.hpp>
#include <connectionHandler.h>
#include <mutex>
#include <condition_variable>
#include "thread"

std::mutex mtx;
std::condition_variable cv;
bool shouldStop=false;

class ReadFromSocket {
private:
    ConnectionHandler* connections_;

public:
    ReadFromSocket(ConnectionHandler* c): connections_(c){}
    void run(){
        while(1) {
            std::string message;
            short shortOpcode = nextTwoBytes();
            if (shortOpcode == 9) {
                char *publicPrivate= new char[1];
                connections_->getBytes(publicPrivate, 1);
                std::string privacy;
                if (publicPrivate[0] ==0) {
                    privacy = "PM ";
                } else {
                    privacy = "Public ";
                }
                std::string postingUser;
                connections_->getFrameAscii(postingUser, 0);
                std::string content;
                connections_->getFrameAscii(content, 0);
                message = "NOTIFICATION " + privacy + postingUser + content.substr(0, content.length() - 1);
            }
            if (shortOpcode == 10) {
                short msgShortOpcode = nextTwoBytes();
                if (msgShortOpcode == 1 || msgShortOpcode == 2 || msgShortOpcode == 5 || msgShortOpcode == 6) {
                    message = "ACK " + std::to_string(msgShortOpcode);
                }
                if (msgShortOpcode == 3) {
                    message = "ACK " + std::to_string(msgShortOpcode);
                    std::cout << message << std::endl;
                    connections_->close();
                    shouldStop=true;
                    std::unique_lock<std::mutex> lck(mtx);
                    cv.notify_one();
                    break;
                }
                if (msgShortOpcode == 4 || msgShortOpcode == 7) {
                    short shortNumOfUsers = nextTwoBytes();
                    std::string userNameList;
                    for(int i=0; i<shortNumOfUsers; i++){
                        connections_->getFrameAscii(userNameList, (char)0);
                    }
                    message = "ACK " + std::to_string(msgShortOpcode) + " " + std::to_string(shortNumOfUsers) + " " +
                              userNameList.substr(0, userNameList.length()-1);
                }
                if (msgShortOpcode == 8) {
                    short shortNumOfPosts = nextTwoBytes();
                    short shortNumOfFollowers = nextTwoBytes();
                    short shortNumOfFollowing = nextTwoBytes();
                    message = "ACK " + std::to_string(msgShortOpcode) + " " + std::to_string(shortNumOfPosts) + " " +
                              std::to_string(shortNumOfFollowers) + " " + std::to_string(shortNumOfFollowing);
                }
            }
            if (shortOpcode == 11) {
                short msgShortOpcode = nextTwoBytes();
                message = "ERROR " + std::to_string(msgShortOpcode);
                if(msgShortOpcode==3){
                    shouldStop=true;
                    std::unique_lock<std::mutex> lck(mtx);
                    cv.notify_one();
                }
            }
            if (shortOpcode != 3) {
                std::cout << message << std::endl;
            }
        }
    }
    short bytesToShort(char* bytesArr){
        short result = (short)((bytesArr[0] & 0xff) << 8);
        result += (short)(bytesArr[1] & 0xff);
        return result;
    }
    short nextTwoBytes(){
        char* toGet;
        toGet= new char[2];
        connections_->getBytes(toGet,2);
        short shortToGet= bytesToShort(toGet);
        return shortToGet;
    };
};


class ReadFromKeyboard {
private:
    ConnectionHandler* handler;
    void shortToBytes(short num, char* bytesArr){
        bytesArr[0] = ((num >> 8) & 0xFF);
        bytesArr[1] = (num & 0xFF);
    }

public:
    ReadFromKeyboard(ConnectionHandler* h) : handler(h) {}
    void run(){
        while(handler->isConnected()){
            const short bufsize = 1024;
            char buf[bufsize];
            std::cin.getline(buf, bufsize);
            std::string line(buf);
            std::vector<std::string> result;
            boost::split(result, line, [](char c){return c == ' ';});
            char* opCodeArray=new char[2];
            if(result.front()=="REGISTER" || result.front()=="LOGIN"){
                if(result.front()=="REGISTER"){
                    shortToBytes((short)1,opCodeArray);
                }
                if(result.front()=="LOGIN"){
                    shortToBytes((short)2,opCodeArray);
                }
                std::string userName = result.at(1);
                std::string password = result.at(2);
                handler->sendBytes(opCodeArray,2);
                handler->sendFrameAscii(userName,'\0');
                handler->sendFrameAscii(password,'\0');
            }
            if(result.front()=="LOGOUT"){
                shortToBytes((short)3,opCodeArray);
                handler->sendBytes(opCodeArray,2);
                std::unique_lock<std::mutex> lck(mtx);
                while (!shouldStop) cv.wait(lck);

            }
            if(result.front()=="FOLLOW"){
                shortToBytes((short)4,opCodeArray);
                std::string followOrUnfollow = result.at(1);
                char* followUnfollow = new char[1];
                followUnfollow[0] = followOrUnfollow.at(0)-'0';
                std::string numOfUsers = result.at(2);
                short shortNumOfUsers=(short) std::stoi(numOfUsers);
                char* numOfUsersArray=new char[2];
                shortToBytes(shortNumOfUsers,numOfUsersArray);
                handler->sendBytes(opCodeArray,2);
                handler->sendBytes(followUnfollow,1);
                handler->sendBytes(numOfUsersArray,2);
                for(unsigned int i=3; i<result.size(); i++){
                    std::string user = result.at(i);
                    handler->sendFrameAscii(user,'\0');
                }
            }
            if(result.front()=="POST"){
                shortToBytes((short)5,opCodeArray);
                std::string content="";
                for(unsigned int i=1; i<result.size(); i++){
                    content=content+result.at(i)+" ";}
                content=content.substr(0,content.length()-1);
                handler->sendBytes(opCodeArray,2);
                handler->sendFrameAscii(content,'\0');
            }
            if(result.front()=="PM"){
                shortToBytes((short)6,opCodeArray);
                std::string userName = result.at(1);
                std::string content="";
                for(unsigned int i=1; i<result.size(); i++){
                    content=content+result.at(i)+" ";}
                content=content.substr(0,content.length()-1);
                handler->sendBytes(opCodeArray,2);
                handler->sendFrameAscii(userName,'\0');
                handler->sendFrameAscii(content,'\0');
            }
            if(result.front()=="USERLIST"){
                shortToBytes((short)7,opCodeArray);
                handler->sendBytes(opCodeArray,2);
            }
            if(result.front()=="STAT"){
                shortToBytes((short)8,opCodeArray);
                std::string userName = result.at(1);
                handler->sendBytes(opCodeArray,2);
                handler->sendFrameAscii(userName,'\0');
            }
        }
    }
};

int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    ReadFromSocket socket(&connectionHandler);
    ReadFromKeyboard keyboard(&connectionHandler);
    std::thread th1(&ReadFromSocket:: run,&socket );
    std:: thread th2(&ReadFromKeyboard:: run, &keyboard);
    th1.join();
    th2.join();
}
