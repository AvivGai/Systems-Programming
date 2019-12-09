package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

/**
 * Passive data-object representing a customer's order schedule.
 */
public class OrderSchedule implements Serializable {
    public String bookTitle;
    public int tick;
}
