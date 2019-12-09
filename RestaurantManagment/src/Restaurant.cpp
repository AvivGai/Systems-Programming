#include "Restaurant.h"
#include <vector>
#include <string>
#include "Dish.h"
#include "Table.h"
#include "Action.h"
#include "Restaurant.h"
#include <fstream>
#include <sstream>
#include <zconf.h>

Restaurant:: Restaurant(){} //empty constructor

    Restaurant::Restaurant(const std::string &configFilePath){
    std::ifstream infile;
    infile.open(configFilePath);
    if(!infile.good()){ //check if the path and file is ok
        std::cout<<"file is invalid";
        return;
    }
    std::string line;
    int counter=0;
    while (std::getline(infile, line)){
        if (line[0]!='#'&& line!="") {
            counter++;
            if (counter == 1) {
                this->numOfTables = std::stoi(line);
            }
            if (counter == 2) {
                iniTables(line);
            }
            if (counter >= 3) {
                iniDish(line, counter - 3);

            }
        }
    }
    numOfCustomers=0;
    }


    void Restaurant::iniTables(std::string s){
        std::vector<std::string> result;
        std::stringstream ss(s);
        while( ss.good() ){
            std::string substr;
            getline( ss, substr, ',' );
            result.push_back( substr );
        }

        for(unsigned int i=0;i<unsigned (getNumOfTables());i++){
            Table *t= new Table(std::stoi(result[i]));
            tables.push_back(t);
        }
    }

    void Restaurant::iniDish(std::string s, int num){
        std::vector<std::string> result;
        std::stringstream ss(s);
        while( ss.good() ){
            std::string substr;
            getline( ss, substr, ',' );
            result.push_back( substr );
        }
        Dish d(num, result[0],std::stoi(result[2]), checkDishType(result[1]));
        (this->menu).push_back(d);
        }

    Restaurant::Restaurant (const Restaurant &other) {//copy constructor
        open=other.open;
        numOfTables=other.numOfTables;
        numOfCustomers=other.getNumOfCust();
        tables= std::vector<Table*>();
        for(unsigned int i=0; i<other.tables.size(); i++){
            Table* t=new Table(*other.tables[i]);
            tables.push_back(t);
        }
        menu=std::vector<Dish>();
        for(unsigned int i=0;i<other.menu.size();i++) {
            menu.push_back(other.menu[i]);
        }
        actionsLog=std::vector<BaseAction*>();
        for(unsigned int i=0; i<other.getActionsLog().size();i++) {
            actionsLog.push_back(other.getActionsLog()[i]->clone());
        }
    }

    Restaurant::~Restaurant() { //destructor
    clear();
    }

    void Restaurant::clear() {
        for(unsigned int i=0; i<tables.size(); i++){
            delete tables[i];
        }
        tables.clear();
        numOfTables=0;
        numOfCustomers=0;
        for(unsigned int i=0; i<actionsLog.size(); i++){
            delete actionsLog[i];
        }
        actionsLog.clear();
        menu.clear();
        open= false;
    }

Restaurant &Restaurant::operator=(const Restaurant& other) {//copy assignment
        if(this==&other)
            return *this;
        std::vector<Table*> temp1;
        for(unsigned int i=0; i<other.getTables().size(); i++){
            Table* t=new Table(*other.getTables()[i]);
            temp1.push_back(t);
        }
        std::vector<BaseAction*> temp2;
        for(unsigned int i=0; i<other.getActionsLog().size();i++) {
            temp2.push_back(other.getActionsLog()[i]->clone());
        }
        std::vector<Dish> temp3;
        for(unsigned int i=0;i<other.menu.size();i++) {
        temp3.push_back(other.menu[i]);
        }
        clear();
        tables= temp1;
        actionsLog=temp2;
        menu=temp3;
        numOfTables=other.numOfTables;
        numOfCustomers=other.getNumOfCust();
        open=other.open;
        return *this;
}
    Restaurant::Restaurant(Restaurant&& other){//move constructor
        numOfTables=other.numOfTables;
        numOfCustomers=other.getNumOfCust();
        open=other.open;
        tables=other.tables;
        menu= other.menu;
        actionsLog=other.actionsLog;
        for(unsigned int i=0; i<other.tables.size(); i++) {
            other.tables[i] = nullptr;
        }
        for(unsigned int i=0;i<other.actionsLog.size();i++) {
            other.actionsLog[i]= nullptr;
        }
}


    Restaurant &Restaurant::operator=(Restaurant &&other) {//move assignment
        if(this!=&other){
            clear();
            numOfTables=other.numOfTables;
            numOfCustomers=other.getNumOfCust();
            open=other.open;
            tables=other.tables;
            menu= other.menu;
            actionsLog=other.actionsLog;
            for(unsigned int i=0; i<other.tables.size(); i++) {
                other.tables[i] = nullptr;
            }
            for(unsigned int i=0;i<other.actionsLog.size();i++) {
                other.actionsLog[i]= nullptr;
            }
        }
        return *this;
    }

    DishType Restaurant::checkDishType (std::string s){
        if (s=="VEG"){
            return VEG;
        }
        if (s=="SPC"){
            return SPC;
        }
        if (s=="ALC"){
            return ALC;
        }
        else
            return BVG;
    }

    void Restaurant::start() {
        std::cout << "Restaurant is now open!" <<std::endl;
        std::string input;
        do {
            getline(std::cin, input);
            std::string first = input.substr(0, input.find(" "));
            if (first == "open") {
                (openTable(input.substr(input.find(" ")+1)))->act(*this);
            }
            if (first == "order") {
                (order(std::stoi(input.substr(input.find(" ")+1))))->act(*this);
            }
            if (first == "move") {
                (moveCustomer(input.substr(5)))->act(*this);
            }
            if (first == "status") {
                PrintTableStatus* p = new PrintTableStatus(std::stoi(input.substr(7)));
                p->act(*this);
                actionsLog.push_back(p);
            }
            if (first == "log"){
                PrintActionsLog* p = new PrintActionsLog();
                p->act(*this);
                actionsLog.push_back(p);
            }
            if(first=="close") {
                Close* c = new Close(std::stoi(input.substr(6)));
                c->act(*this);
                actionsLog.push_back(c);
            }
            if(first=="menu") {
                PrintMenu* p = new PrintMenu();
                p->act(*this);
                actionsLog.push_back(p);
            }
            if(first=="backup") {
                BackupRestaurant* b = new BackupRestaurant();
                b->act(*this);
                actionsLog.push_back(b);
            }
            if(first=="restore") {
                RestoreResturant* r = new RestoreResturant();
                r->act(*this);
                actionsLog.push_back(r);
            }
        }
        while(input != "closeall");
        CloseAll().act(*this);
    }

    Order* Restaurant:: order(int i) {
    tables[i]->order(this->getMenu());
    Order* o = new Order(i);
    actionsLog.push_back(o);
    return o;
    }

    MoveCustomer* Restaurant:: moveCustomer(std::string s) {
        int tableId= std::stoi(s.substr(0,s.find(' ')));
        int destTable= std::stoi(s.substr(s.find(' ')+1,s.find_last_of(' ')-(s.find(' ')+1)));
        int customerID = std::stoi(s.substr(s.find_last_of(' ')+1));
        MoveCustomer* m = new MoveCustomer(tableId,destTable,customerID);
        actionsLog.push_back(m);
        return m;
    }

    std::vector<Table*> Restaurant::getTables() const{
        return tables;
    }

    OpenTable* Restaurant::openTable(std::string s){
        int id = std::stoi(s.substr(0,s.find(' ')));
        std::vector<Customer*> customers;
        std::vector<std::string> result;
        std::stringstream ss(s.substr(2));
        while (ss.good()) {
            std::string substr;
            getline(ss, substr, ' ');
            result.push_back(substr);
        }
        for (unsigned int i=0; i < result.size(); ++i) {
            int sperate = result[i].find(',');
            std::string type = result[i].substr(sperate + 1);
            if (type == "veg") {
                VegetarianCustomer *v= new VegetarianCustomer(result[i].substr(0, sperate),this->getNumOfCust());
                this->addCustomer();
                customers.push_back(v);
            }
            if (type== "chp") {
                CheapCustomer *c= new CheapCustomer(result[i].substr(0, sperate), this->getNumOfCust());
                this->addCustomer();
                customers.push_back(c);
            }
            if (type== "spc") {
                SpicyCustomer *s= new SpicyCustomer(result[i].substr(0, sperate), this->getNumOfCust());
                this->addCustomer();
                customers.push_back(s);
            }
            if (type=="alc") {
                AlchoholicCustomer *a= new AlchoholicCustomer(result[i].substr(0, sperate), this->getNumOfCust());
                this->addCustomer();
                customers.push_back(a);
            }
        }
        OpenTable* op = new OpenTable(id, customers);
        actionsLog.push_back(op);
        return op;
    }

    int Restaurant::getNumOfTables(){
        return this->numOfTables;
    }

    Table* Restaurant::getTable(int ind) {
        if (ind< getNumOfTables()) {
            return tables[ind];
        }
        return nullptr;
    }

    int Restaurant::getNumOfCust() const{
        return this->numOfCustomers;
    }
    void Restaurant::addCustomer() {
        this->numOfCustomers++;
    }


    const std::vector<BaseAction*>& Restaurant::getActionsLog() const { // Return a reference to the history of actions
        return (this->actionsLog);
    }

    std::vector<Dish>& Restaurant::getMenu(){
        return menu;
    }
