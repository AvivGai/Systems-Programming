//
// Created by AVIV on 11/8/2018.
//

#include "Customer.h"
#include <vector>
#include <string>
#include <unordered_set>
#include "Dish.h"
#include <algorithm>
#include "Action.h"


Customer:: Customer(std::string c_name, int c_id): name(c_name), id(c_id){}

Customer::~Customer(){}

std::string Customer:: getName() const {
    return name;
}
int  Customer:: getId() const {
    return id;
}

std::vector<int>Customer::getDishOrdered() const{
    return this->dishesOrdered;
}

std::vector<int>Customer::getLastOrder()const{
    return this->lastOrder;
}

VegetarianCustomer ::  VegetarianCustomer(std::string name, int id): Customer(name, id){}

VegetarianCustomer::~VegetarianCustomer(){}//destructor

VegetarianCustomer* VegetarianCustomer::clone(){
    VegetarianCustomer *c=new VegetarianCustomer(*this);
    return c;
}


std::vector<int> VegetarianCustomer:: order(const std::vector<Dish> &menu) {
    lastOrder.clear();
    unsigned int smallestId=1000;
    int mostExp=-1;
    int mostExpId=-1;
    for(unsigned int i=0;i<menu.size();++i){
        if (menu[i].getType()==VEG){
            if(i<smallestId){
                smallestId = i;
            }
        }
        if(menu[i].getType()==BVG){
            int price = menu[i].getPrice();
            if(price>mostExp){
                mostExp = price;
                mostExpId = i;
            }
        }

    }
    if(smallestId!=1000 && mostExpId!=-1) {
        lastOrder.push_back(smallestId);
        lastOrder.push_back(mostExpId);
    }
    return lastOrder;
}

std::string VegetarianCustomer:: toString() const {
    return getName()+",veg";
}


CheapCustomer:: CheapCustomer(std::string name, int id):Customer(name, id){}

CheapCustomer::~CheapCustomer() {}//destructor

CheapCustomer* CheapCustomer::clone(){
    CheapCustomer *c= new CheapCustomer(*this);
    return c;
}

std::vector<int>  CheapCustomer:: order(const std::vector<Dish> &menu) {
    lastOrder.clear();
    int cheapDish=1000;
    int cheapDishID=-1;
    if(getDishOrdered().empty()) {
        for (unsigned int i = 0; i < menu.size(); ++i) {
            int price = menu[i].getPrice();
            if (price < cheapDish) {
                cheapDish = price;
                cheapDishID = menu[i].getId();
            }
        }
        if (cheapDishID != -1) {
            lastOrder.push_back((cheapDishID));
            dishesOrdered.push_back(cheapDishID);
        }
    }
    return lastOrder;
}

std::string  CheapCustomer:: toString() const {
    return getName()+",chp";
}


SpicyCustomer:: SpicyCustomer(std::string name, int id):Customer(name, id){}

SpicyCustomer::~SpicyCustomer() {}//destructor

SpicyCustomer* SpicyCustomer::clone(){
    SpicyCustomer *c=new SpicyCustomer(*this);
    return c;
}

std::vector<int> SpicyCustomer:: order(const std::vector<Dish> &menu) {
    lastOrder.clear();
    int mostExp = -1;
    int cheapDish = 1000;
    int dishID = -1;
    for (unsigned int i = 0; i < menu.size(); ++i) {
        if (getDishOrdered().empty() && menu[i].getType() == SPC) {
            int price = menu[i].getPrice();
            if (price > mostExp) {
                mostExp = price;
                dishID = menu[i].getId();
            }
        } else if (!getDishOrdered().empty() && menu[i].getType() == BVG) {
            int price = menu[i].getPrice();
            if (price < cheapDish) {
                cheapDish = price;
                dishID = menu[i].getId();
            }
        }
    }
    if (dishID != -1) {
    lastOrder.push_back(dishID);
    dishesOrdered.push_back(dishID);
    }
    return lastOrder;
}

std::string SpicyCustomer:: toString() const {
    return getName()+",spc";
}

AlchoholicCustomer:: AlchoholicCustomer(std::string name, int id):Customer(name, id){}

AlchoholicCustomer::~AlchoholicCustomer() {}//destructor

AlchoholicCustomer* AlchoholicCustomer::clone(){
    AlchoholicCustomer *c=new AlchoholicCustomer(*this);
    return c;
}
std::vector<int> AlchoholicCustomer::order(const std::vector<Dish> &menu) {
    lastOrder.clear();
    int cheapDish=10000;
    int dishID=-1;
    for(unsigned int i=0;i<menu.size();++i){
        std::vector<int>::iterator it = std::find(dishesOrdered.begin(),dishesOrdered.end(), menu[i].getId());
        if(menu[i].getType()==DishType::ALC&& it==dishesOrdered.end()){ //if element wasn't found in the vector
                int price = menu[i].getPrice();
                if (price < cheapDish) {
                    cheapDish = price;
                    dishID = menu[i].getId();
                }
        }
    }
    if(dishID!=-1){
        lastOrder.push_back(dishID);
        dishesOrdered.push_back(dishID);
    }
    return lastOrder;

}
std::string AlchoholicCustomer::toString() const {
    return getName()+",alc";
}
