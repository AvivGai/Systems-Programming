package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.findVehicleEvent;
import bgu.spl.mics.application.messages.releaseVehicleEvent;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import java.util.concurrent.CountDownLatch;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {
	private CountDownLatch d;

	public LogisticsService(String name,CountDownLatch d) {
		super(name);
		this.d=d;
	}

	@Override
	protected void initialize() {
		subscribeEvent(DeliveryEvent.class, event->{
			Future<DeliveryVehicle> f1=sendEvent(new findVehicleEvent());
			if(f1!=null){
				DeliveryVehicle v = f1.get();
				if(v!=null){
					v.deliver(event.getAddress(), event.getDistance());
					complete(event, true);
					sendEvent(new releaseVehicleEvent(v));
				}
				else{
					complete(event,null);}}
			else{
				complete(event,null);
			}
		});
		subscribeBroadcast(TerminateBroadcast.class, broadcast->terminate());
		d.countDown();
	}
}
