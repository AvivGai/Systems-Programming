package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

/**
 * BookOrderEvent represents a book order.
 * it is sent by API Service and selling Service handles it
 */
public class BookOrderEvent implements Event {
    private String book;
    private Customer customer;
    private int orderTick;

        public BookOrderEvent(String b, Customer c, int orderTick) {
        this.book = b;
        this.customer= c;
        this.orderTick=orderTick;
    }

    /**
     * @return the name of the book
     */
    public String getBookName(){
        return this.book;
    }

    /**
     * @return the customer who ordered the book
     */
    public Customer getCustomer(){return this.customer;}

    /**
     * @return the the tick that of the order
     */
    public int getOrderTick(){return this.orderTick;}
}


