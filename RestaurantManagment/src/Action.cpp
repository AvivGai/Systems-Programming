#include "Action.h"//
// Created by AVIV on 11/8/2018.
//
#include "Action.h"
#include <string>
#include <iostream>
#include "Customer.h"
#include "Table.h"
#include <utility>
#include "Restaurant.h"
#include <vector>

BaseAction:: BaseAction(): errorMsg(), status(){}
ActionStatus BaseAction::getStatus() const {
    return status;
}
void BaseAction:: complete(){
    status=ActionStatus::COMPLETED;
}
void BaseAction:: error(std::string errorMsg){
    status=ActionStatus::ERROR;
    this->errorMsg=errorMsg;
}
std::string BaseAction:: getErrorMsg() const{
    return errorMsg;
}

BaseAction::~BaseAction(){}

//constructor
OpenTable:: OpenTable(int id, std::vector<Customer *> &customersList): tableId(id),customers(customersList){}

void OpenTable:: act(Restaurant &restaurant){
    Table* t = restaurant.getTable(tableId);
    if((t== nullptr) | (t->isOpen())){
        error( "Table does not exist or is already open");
        std::cout<<"Error: "+getErrorMsg()<<std::endl;
    }
    else{
        t->openTable();
        for(unsigned int i=0; i<customers.size(); i++){
          t->addCustomer(customers[i]->clone());
        }
        complete();
    }
}

OpenTable::OpenTable(const OpenTable &other): tableId(other.tableId), customers(){//copy constructor
    customers= std::vector<Customer*>();
    for(unsigned int i=0; i<other.customers.size(); i++){
        customers.push_back(other.customers[i]->clone());
    }
}

OpenTable:: ~OpenTable(){//destructor
    clear();
}

void OpenTable:: clear(){
    for(unsigned int i=0; i<customers.size(); i++){
        delete customers[i];
    }
    customers.clear();
}

OpenTable::OpenTable(OpenTable&& other):tableId(other.tableId),customers(other.customers){//move constructor
    for(unsigned int i=0; i<other.customers.size(); i++) {
        other.customers[i] = nullptr;
    }
}

OpenTable* OpenTable::clone(){
    OpenTable* o= new OpenTable(*this);
    return o;
}

std::string OpenTable:: toString() const {
    std::string l = getCustomersList();
    std::string s = "open "+std::to_string(tableId)+" "+l+" ";
    if(getStatus()==ActionStatus::ERROR)
        s=s+"Error: "+getErrorMsg();
    else
        s=s+"Completed";
    return s;
}

std::string OpenTable::getCustomersList() const {
    std::string s = "";
    for (unsigned int i=0; i<customers.size(); i++){
        s =  s +customers[i]->toString()+" ";
    }
    s = s.substr(0, s.size()-1);
    return s;
}


//costructor
Order :: Order(int id): tableId(id){}

Order::~Order(){}

void Order::act(Restaurant &restaurant){
    Table* t = restaurant.getTable(tableId);
    if ((!t->isOpen()) | (t== nullptr)) {
        error("Table does not exist or is not open");
        std::cout<<"Error: "+getErrorMsg()<<std::endl;
    }
    else{

        for(unsigned int i=0; i < t->getCustomers().size(); i++){
            std:: string name = t->getCustomers()[i]->getName();
            for (unsigned int j=0; j < t->getCustomers()[i]->getLastOrder().size(); j++){
                std::cout<< name+" "+"ordered "+restaurant.getMenu()[t->getCustomers()[i]->getLastOrder()[j]].getName()<<std::endl;
            }
        }
        complete();
    }
}

Order* Order::clone(){
    Order* o= new Order(*this);
    return o;
}

std::string Order:: toString() const{
    std::string s = "order "+std::to_string(tableId)+" ";
    if(getStatus()==ActionStatus::ERROR)
        s=s+"Error: "+getErrorMsg();
    else
        s=s+"Completed";
    return s;
}


//constructor
MoveCustomer :: MoveCustomer(int src, int dst, int customerId): srcTable(src), dstTable(dst), id(customerId){}

MoveCustomer::~MoveCustomer() {}

void MoveCustomer:: act(Restaurant &restaurant) {
    Table *src = restaurant.getTable(srcTable);
    Table *dst = restaurant.getTable(dstTable);
    if (!src->isOpen() || !dst->isOpen() || unsigned (dst->getCapacity()) == unsigned (dst->getCustomers().size()) || src->getCustomer(id) ==
                                                                                                nullptr){
        error("Cannot move customer");
        std::cout << "Error: "+getErrorMsg() << std::endl;
        }
    else {
        std::vector<OrderPair> temp;
        dst->addCustomer(src->getCustomer(id));
        for (unsigned int j = 0; j < src->getOrders().size(); j++) {
            if (std::get<0>(src->getOrders()[j]) != id) {
                temp.push_back(src->getOrders()[j]);
            } else {
                dst->getOrders().push_back(OrderPair(id, std::get<1>(src->getOrders()[j])));
            }
        }
        src->getOrders().clear();
        for(unsigned int i=0;i<temp.size();i++){
            src->getOrders().push_back(temp[i]);
        }
        src->removeCustomer(id);
        if (src->getCustomers().size() == 0) {
            src->closeTable();
        }
        complete();
            }
        }

MoveCustomer* MoveCustomer::clone(){
    MoveCustomer* m= new MoveCustomer(*this);
    return m;
}

std::string MoveCustomer:: toString() const{
    std::string s = "move "+std::to_string(srcTable)+" "+std::to_string(dstTable)+" "+std::to_string(id)+" ";
    if(getStatus()==ActionStatus::ERROR)
        s=s+"Error: "+getErrorMsg();
    else
        s=s+"Completed";
    return s;
}


//constructor
Close :: Close(int id): tableId(id){}

Close::~Close(){}

void Close:: act(Restaurant &restaurant){
    if(tableId>restaurant.getNumOfTables()-1 || !restaurant.getTable(tableId)->isOpen()){
        error("Table does not exist or is not open");
        std::cout<<"Error: "+getErrorMsg()<<std::endl;
    }
    else{
        Table* t=restaurant.getTable(tableId);
        std::string s = "Table "+std::to_string(tableId)+" was closed. Bill "+std::to_string(t->getBill())+"NIS";
        std::cout << s <<std::endl;
        (*t).closeTable();
        complete();
    }
}

Close* Close::clone(){
    Close* c= new Close(*this);
    return c;
}

std::string Close:: toString() const{
    std::string s = "close "+std::to_string(tableId)+" ";
    if(getStatus()==ActionStatus::ERROR)
        s=s+"Error: "+getErrorMsg();
    else
        s=s+"Completed";
    return s;
}


//constructor
CloseAll :: CloseAll(){}

CloseAll::~CloseAll() {}

void CloseAll:: act(Restaurant &restaurant){
    for(unsigned int i=0; i<restaurant.getTables().size(); i++){
        Table* t = restaurant.getTables()[i];
        if(t->isOpen()){
            std::cout<<"Table "+std::to_string(i)+" was closed. Bill "+std::to_string(t->getBill())+"NIS"<<std::endl;
            t->closeTable();
        }
    }
    complete();
}

CloseAll* CloseAll::clone(){
    CloseAll* c= new CloseAll(*this);
    return c;
}

std::string CloseAll:: toString() const{
    std::string s = "close all ";
    if(getStatus()==ActionStatus::ERROR)
        s=s+"Error: "+getErrorMsg();
    else
        s=s+"Completed";
    return s;
}


//constructor
PrintMenu :: PrintMenu(){}

PrintMenu::~PrintMenu() {}

void PrintMenu::act(Restaurant &restaurant){
    for(unsigned int i=0; i<restaurant.getMenu().size(); i++) {
        std::string name = restaurant.getMenu()[i].getName();
        std::string type = restaurant.getMenu()[i].typeToString();
        std::string price=std::to_string(restaurant.getMenu()[i].getPrice());
        std::cout<<name+" "+type+" "+price+"NIS" << std::endl;
    }
    complete();
}

PrintMenu* PrintMenu::clone(){
    PrintMenu* p= new PrintMenu(*this);
    return p;
}

std::string PrintMenu:: toString() const{
    std::string s = "menu ";
    if(getStatus()==ActionStatus::ERROR)
        s=s+"Error: "+getErrorMsg();
    else
        s=s+"Completed";
    return s;
}


//constructor
PrintTableStatus:: PrintTableStatus(int id):tableId(id){}

PrintTableStatus::~PrintTableStatus() {}

void PrintTableStatus::act(Restaurant &restaurant) {
    Table *t = restaurant.getTable(tableId);
    if (!t->isOpen()) {
        std::cout << "Table " + std::to_string(tableId) + " status: closed" <<std::endl;
    } else {
        std::cout << "Table " + std::to_string(tableId )+ " status: open" << std::endl;
        std::cout << "Customers:" << std::endl;
        for (unsigned int i = 0; i<t->getCustomers().size(); i++) {
            std::cout << std::to_string(t->getCustomers()[i]->getId()) + " " + t->getCustomers()[i]->getName() << std::endl;
        }
        std::cout << "Orders:" << std::endl;
        for (unsigned int i = 0; i<t->getOrders().size(); i++) {
            OrderPair p = t->getOrders()[i];
            Dish d = std::get<1>(p);
            int cId = std::get<0>(p);
            std::cout << d.getName() + " " + std::to_string(d.getPrice()) + "NIS " + std::to_string(cId) << std::endl;
        }
        std::cout << "Current Bill: " + std::to_string(t->getBill()) + "NIS"<<std::endl;
     }
    complete();
}

PrintTableStatus* PrintTableStatus::clone(){
    PrintTableStatus* p= new PrintTableStatus(*this);
    return p;
}

std::string PrintTableStatus:: toString() const{
    std::string s = "status "+std::to_string(tableId)+" ";
    if(getStatus()==ActionStatus::ERROR)
        s=s+"Error: "+getErrorMsg();
    else
        s=s+"Completed";
    return s;
}


//constructor
PrintActionsLog:: PrintActionsLog(){}

PrintActionsLog::~PrintActionsLog() {}

void PrintActionsLog::act(Restaurant &restaurant){
    for(unsigned int i=0; i<restaurant.getActionsLog().size(); i++){
        std::string s = restaurant.getActionsLog()[i]->toString();
        std::cout<<s<<std::endl;
    }
    complete();
}

PrintActionsLog* PrintActionsLog::clone(){
    PrintActionsLog* p= new PrintActionsLog(*this);
    return p;
}

std::string PrintActionsLog:: toString() const{
    std::string s = "log ";
    if(getStatus()==ActionStatus::ERROR)
        s=s+"Error: "+getErrorMsg();
    else
        s=s+"Completed";
    return s;
}


//constructor
BackupRestaurant:: BackupRestaurant(){}

BackupRestaurant::~BackupRestaurant() {}

BackupRestaurant* BackupRestaurant::clone(){
    BackupRestaurant* b= new BackupRestaurant(*this);
    return b;
}

void BackupRestaurant:: act(Restaurant &restaurant){
    extern Restaurant* backup;
    if(backup == nullptr) {
        backup = new Restaurant(restaurant);
    }
    else{
        *backup = restaurant;
    }
}

std::string BackupRestaurant::toString() const{
    std::string s = "backup ";
    if(getStatus()==ActionStatus::ERROR)
        s=s+"Error: "+getErrorMsg();
    else
        s=s+"Completed";
    return s;
}


//constructor
RestoreResturant:: RestoreResturant(){}

RestoreResturant::~RestoreResturant() {}

RestoreResturant* RestoreResturant::clone(){
    RestoreResturant* r= new RestoreResturant(*this);
    return r;
}

void RestoreResturant:: act(Restaurant &restaurant){
    extern Restaurant* backup;
    if(backup== nullptr){
        error("No backup available");
        std::cout<<"Error: "+getErrorMsg()<<std::endl;
    }
    else{
        restaurant = *backup;
    }
}

std::string RestoreResturant:: toString() const{
    std::string s = "restore ";
    if(getStatus()==ActionStatus::ERROR)
        s=s+"Error: "+getErrorMsg();
    else
        s=s+"Completed";
    return s;

}
