package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * TerminateBroadcast represents the final tick, when all services needs to terminate and unregister.
 * it is sent by time Service.
 */
public class TerminateBroadcast implements Broadcast {
    public TerminateBroadcast(){}
}
