package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

/**
 * DeliveryEvent represents a delivery of a book.
 * it is sent by Selling Service and logistic Service handles it
 */
public class DeliveryEvent implements Event {

    private String address;
    private int distance;

    public DeliveryEvent(String address, int distance){
        this.address = address;
        this.distance = distance;
    }

    /**
     * @return the address of the customer that suppose to get the delivery
     */
    public String getAddress(){
        return address;
    }

    /**
     * @return the distance to the delivery address
     */
    public int getDistance(){
        return distance;
    }

}
