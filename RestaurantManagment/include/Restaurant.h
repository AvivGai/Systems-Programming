#ifndef RESTAURANT_H_
#define RESTAURANT_H_

#include <vector>
#include <string>
#include "Dish.h"
#include "Table.h"
#include "Action.h"


class Restaurant{		
public:
	Restaurant();
    Restaurant(const std::string &configFilePath); //constructor
    Restaurant (const Restaurant &other); //copy constructor
    virtual ~Restaurant(); //destructor
    Restaurant &operator=(const Restaurant& other);//copy assignment
    Restaurant(Restaurant&& other);//move constructor
    Restaurant &operator=(Restaurant &&other);//move assignment
	void clear();
    void start();
    int getNumOfTables();
    Table* getTable(int ind);
	const std::vector<BaseAction*>& getActionsLog() const; // Return a reference to the history of actions
    std::vector<Dish>& getMenu();
    std::vector<Table*> getTables() const;
    int getNumOfCust() const;
    void addCustomer();
	OpenTable* openTable(std::string s);
	Order* order(int i);
    MoveCustomer* moveCustomer(std::string s);

private:
    bool open;
    std::vector<Table*> tables;
    std::vector<Dish> menu;
    std::vector<BaseAction*> actionsLog;
	void iniDish(std::string s, int num);
	void iniTables(std::string s);
	int numOfTables;
	int numOfCustomers;
	DishType checkDishType(std::string);

};

#endif