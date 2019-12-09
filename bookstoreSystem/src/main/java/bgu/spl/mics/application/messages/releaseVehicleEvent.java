package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

/**
 * releaseVehicleEvent represents releasing a vehicle after a delivery of a book is finished.
 * it is sent by logistic Service and resource Service handles it
 */
public class releaseVehicleEvent implements Event {

    private DeliveryVehicle vehicleToRelease;

    public releaseVehicleEvent(DeliveryVehicle v){
        vehicleToRelease = v;
    }

    /**
     * @return the DeliveryVehicle that finished the delivery and needs to be released
     */
    public DeliveryVehicle getVehicleToRelease(){
        return vehicleToRelease;
    }
}
