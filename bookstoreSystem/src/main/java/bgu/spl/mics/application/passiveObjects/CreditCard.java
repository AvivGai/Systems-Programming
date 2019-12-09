package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

/**
 * Passive data-object representing a credit card of a customer.
 */
public class CreditCard implements Serializable {
    public int number;
    public int amount;
}
