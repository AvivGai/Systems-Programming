package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * TickBroadcast represents the current tick.
 * it is sent by time Service.
 */
public class TickBroadcast implements Broadcast {

    private int tick;

    public TickBroadcast(int tick){
        this.tick=tick;
    }

    /**
     * @return the tick of the broadcast
     */
    public int get(){
        return this.tick;
    }
}
