//
// Created by AVIV on 11/8/2018.
//
#include <vector>
#include "Customer.h"
#include "Dish.h"
#include "Table.h"
#include "utility"

//constructor
Table:: Table(int t_capacity): capacity(t_capacity), open(false){}

int Table:: getCapacity() const {
    return capacity;
}

void Table:: addCustomer(Customer* customer) {
    customersList.push_back(customer);
}

void Table:: removeCustomer(int id) {
    for ( unsigned int i = 0; i < customersList.size(); ++i) {
        if(customersList[i]->getId()==id) {
            customersList.erase(customersList.begin() + i);
            break;
        }
    }
}

Customer* Table:: getCustomer(int id) {
    for (unsigned int i = 0; i < customersList.size(); ++i) {
        if(customersList[i]->getId()==id) {
            return customersList[i];
        }
    }
    return nullptr;
}

std::vector<Customer*>& Table:: getCustomers() {
    return customersList;
}

std::vector<OrderPair>& Table:: getOrders() {
    return orderList;
}

void Table:: order(const std::vector<Dish> &menu) {
    std::vector<Customer *> customers = this->getCustomers();
    for (unsigned int i = 0; i < customers.size(); ++i) {
        std::vector<int> dishes=customers[i]->order(menu);
        for (unsigned int j = 0; j < dishes.size(); ++j){
            int dishID= dishes[j];
            OrderPair toAdd= OrderPair(customers[i]->getId(), menu[dishID]);
            orderList.push_back(toAdd);
        }
    }
}

void Table:: openTable() {
    open=true;
}

void Table:: closeTable() {
    open=false;
    for(unsigned int i=0; i<customersList.size(); i++){
        delete customersList[i];
    }
    customersList.clear();
    orderList.clear();
}

int Table:: getBill() {
    int sum=0;
    for (unsigned int i = 0; i < orderList.size(); ++i) {
        sum = sum + std::get<1>(orderList[i]).getPrice();
    }
    return sum;
}

bool Table:: isOpen() {
    return open;
}

//copy constructor
Table::Table(const Table& aTable){
    capacity=aTable.capacity;
    open=aTable.open;
    customersList= std::vector<Customer*>();
    for(unsigned int i=0; i<aTable.customersList.size(); i++){
        customersList.push_back(aTable.customersList[i]->clone());
    }
    orderList= std::vector<OrderPair>() ;
    for(unsigned int i=0; i<aTable.orderList.size(); i++){
        orderList.push_back(aTable.orderList[i]);
    }
}

//copy assignment operator
Table & Table::operator=(const Table &aTable){
    if(this==&aTable)
        return *this;
    std::vector<Customer*> temp1;
    for(unsigned int i=0; i<aTable.customersList.size(); i++){
        temp1.push_back(aTable.customersList[i]->clone());
    }
    std::vector<OrderPair> temp2;
    for(unsigned int i=0; i<aTable.orderList.size(); i++){
        temp2.push_back(aTable.orderList[i]);
    }
    clear();
    customersList=temp1;
    capacity=aTable.capacity;
    open=aTable.open;
    for(unsigned int i=0; i<temp2.size(); i++){
        orderList.push_back(temp2[i]);
    }
    return *this;
}

void Table::clear(){
    for(unsigned int i=0; i<customersList.size(); i++){
        delete customersList[i];
    }
    customersList.clear();
    orderList.clear();
    open= false;
    capacity=0;
}

//DESTRUCTOR
Table::~Table(){
    clear();
}

// Move Constructor
Table:: Table(Table &&other): capacity(other.capacity), open(other.open), customersList(other.customersList){
    for(unsigned int i=0; i<other.customersList.size(); i++) {
        other.customersList[i] = nullptr;
    }
}

// Move Assignment
Table& Table:: operator=(Table &&other){
    if(this!=&other){
        clear();
        capacity=other.capacity;
        open=other.open;
        customersList= other.customersList;
        for(unsigned int i=0; i<other.orderList.size(); i++) {
            orderList.push_back(other.orderList[i]);
        }
        for(unsigned int i=0; i<other.customersList.size(); i++) {
            other.customersList[i] = nullptr;
        }

    }
    return *this;
}



