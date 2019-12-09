#ifndef ACTION_H_
#define ACTION_H_

#include <string>
#include <iostream>
#include "Customer.h"
#include <vector>

enum ActionStatus{
    PENDING, COMPLETED, ERROR
};

//Forward declaration
class Restaurant;

class BaseAction{
public:
    BaseAction();
    ActionStatus getStatus() const;
    virtual void act(Restaurant& restaurant)=0;
    virtual std::string toString() const=0;
    virtual ~BaseAction();
    virtual BaseAction* clone()=0;
protected:
    void complete();
    void error(std::string errorMsg);
    std::string getErrorMsg() const;
private:
    std::string errorMsg;
    ActionStatus status;
};


class OpenTable : public BaseAction {
public:
    OpenTable(int id, std::vector<Customer*> &customersList); //constructor
	OpenTable(const OpenTable &other); //copy constructor
	virtual ~OpenTable(); //destructor
	OpenTable(OpenTable&& other); //move constructor
    void act(Restaurant &restaurant);
    std::string toString() const;
	OpenTable* clone();
	void clear();

private:
	const int tableId;
	std::vector<Customer*> customers;
    std::string getCustomersList() const;
};


class Order : public BaseAction {
public:
    Order(int id);
	virtual ~Order();//destructor
    void act(Restaurant &restaurant);
    std::string toString() const;
	Order* clone();
private:
    const int tableId;
};


class MoveCustomer : public BaseAction {
public:
    MoveCustomer(int src, int dst, int customerId);
	virtual ~MoveCustomer();//destructor
    void act(Restaurant &restaurant);
    std::string toString() const;
	MoveCustomer* clone();
private:
    const int srcTable;
    const int dstTable;
    const int id;
};


class Close : public BaseAction {
public:
    Close(int id);
    virtual ~Close();//destructor
    void act(Restaurant &restaurant);
    std::string toString() const;
	Close* clone();
private:
    const int tableId;
};


class CloseAll : public BaseAction {
public:
    CloseAll();
    virtual ~CloseAll();//destructor
    void act(Restaurant &restaurant);
    std::string toString() const;
	CloseAll* clone();
private:
};


class PrintMenu : public BaseAction {
public:
    PrintMenu();
    virtual ~PrintMenu();//destructor
    void act(Restaurant &restaurant);
    std::string toString() const;
	PrintMenu* clone();
private:
};


class PrintTableStatus : public BaseAction {
public:
    PrintTableStatus(int id);
    virtual ~PrintTableStatus();//destructor
    void act(Restaurant &restaurant);
    std::string toString() const;
	PrintTableStatus* clone();
private:
    const int tableId;
};


class PrintActionsLog : public BaseAction {
public:
    PrintActionsLog();
    virtual ~PrintActionsLog(); //destructor
    void act(Restaurant &restaurant);
    std::string toString() const;
	PrintActionsLog* clone();
private:
};


class BackupRestaurant : public BaseAction {
public:
    BackupRestaurant();
    virtual ~BackupRestaurant(); //destructor
    void act(Restaurant &restaurant);
    std::string toString() const;
	BackupRestaurant* clone();
private:
};


class RestoreResturant : public BaseAction {
public:
    RestoreResturant();
    virtual ~RestoreResturant();//destructor
    void act(Restaurant &restaurant);
    std::string toString() const;
	RestoreResturant* clone();

};


#endif