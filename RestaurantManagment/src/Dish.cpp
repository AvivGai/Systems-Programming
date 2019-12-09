//
// Created by AVIV on 11/8/2018.
//
#include "Dish.h"
#include <string>

Dish:: Dish(int d_id, std::string d_name, int d_price, DishType d_type): id(d_id), name(d_name), price(d_price), type(d_type){}

Dish& Dish:: operator=(const Dish&other){return *this;}

int Dish:: getId() const {
    return id;
}

std::string Dish:: getName() const {
    return name;
}

int Dish:: getPrice() const {
    return price;
}

DishType Dish:: getType() const {
    return type;
}

std::string Dish::typeToString() const{
    if(getType()==VEG){
        return "VEG";
    }
    if(getType()==BVG){
        return "BVG";
    }
    if(getType()==ALC){
        return "ALC";
    }
    else
        return "SPC";
}




