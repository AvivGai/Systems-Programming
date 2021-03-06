//
// Created by alma on 14/11/18.
//

#ifndef CUSTOMER_H
#define CUSTOMER_H

#include <vector>
#include <string>
#include "Dish.h"

class Customer{
public:
    Customer(std::string c_name, int c_id);
    virtual std::vector<int> order(const std::vector<Dish> &menu)=0;
    virtual std::string toString() const = 0;
    std::string getName() const;
    int getId() const;
    virtual Customer* clone() =0;
    virtual ~Customer(); //destructor
    std::vector<int>getDishOrdered() const;
    std::vector<int>getLastOrder()const;

private:
    const std::string name;
    const int id;
protected:
    std::vector<int> lastOrder;
    std::vector<int> dishesOrdered;

};


class VegetarianCustomer : public Customer {
public:
    VegetarianCustomer(std::string name, int id);
    VegetarianCustomer* clone();
    virtual ~VegetarianCustomer();
    std::vector<int> order(const std::vector<Dish> &menu);
    std::string toString() const;
private:
};


class CheapCustomer : public Customer {
public:
    CheapCustomer(std::string name, int id);
    CheapCustomer* clone();
    virtual ~CheapCustomer();
    std::vector<int> order(const std::vector<Dish> &menu);
    std::string toString() const;
private:
};


class SpicyCustomer : public Customer {
public:
    SpicyCustomer(std::string name, int id);
    SpicyCustomer* clone();
    virtual ~SpicyCustomer();
    std::vector<int> order(const std::vector<Dish> &menu);
    std::string toString() const;
private:
};


class AlchoholicCustomer : public Customer {
public:
    AlchoholicCustomer(std::string name, int id);
    AlchoholicCustomer* clone();
    virtual ~AlchoholicCustomer();
    std::vector<int> order(const std::vector<Dish> &menu);
    std::string toString() const;
private:
};
#endif
