package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

/**
 * CheckAvailabilityEvent represents checking availability of a book.
 * it is sent by Selling Service and inventory Service handles it
 */
public class CheckAvailabilityEvent implements Event {
    private String bookName;
    private Customer c;

    public CheckAvailabilityEvent(String bookName, Customer c){
        this.bookName=bookName;
        this.c=c;
    }

    /**
     * @return the name of the book
     */
    public String getBookName(){
        return this.bookName;
    }

    /**
     * @return the customer who ordered the book
     */
    public Customer getCustomer() {
        return c;
    }
}

