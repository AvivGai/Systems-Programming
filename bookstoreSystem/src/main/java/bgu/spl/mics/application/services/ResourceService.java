package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.findVehicleEvent;
import bgu.spl.mics.application.messages.releaseVehicleEvent;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourcesHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService {
	
	private ResourcesHolder r;
	private CountDownLatch d;
	private LinkedList<Event> unsolvedEvent;

	public ResourceService(String name, CountDownLatch d) {
		super(name);
		this.r= ResourcesHolder.getInstance();
		this.d=d;
		unsolvedEvent=new LinkedList<>();
	}
	
	@Override
	protected void initialize() {
		subscribeEvent(findVehicleEvent.class, event->{
			Future<DeliveryVehicle> f = r.acquireVehicle();
			DeliveryVehicle v=f.get();
			if(v!=null){
				complete(event, v);
			}
			else {
				unsolvedEvent.add(event);
			}
		});
		subscribeEvent(releaseVehicleEvent.class, event->{
			DeliveryVehicle v = event.getVehicleToRelease();
			r.releaseVehicle(v);
			complete(event, true);
			if(!unsolvedEvent.isEmpty()){
				complete(unsolvedEvent.pollFirst(),v);
			}
		} );
		subscribeBroadcast(TerminateBroadcast.class, broadcast->{
				for(Event e: unsolvedEvent){
					complete(e,null);
				}
				terminate();});
		d.countDown();
	}
}
