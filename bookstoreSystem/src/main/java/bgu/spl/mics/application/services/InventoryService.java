package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.*;
import java.util.concurrent.CountDownLatch;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{
	private Inventory inventory;
	private CountDownLatch d;

	public InventoryService(String name,CountDownLatch d) {
		super(name);
		this.d=d;
		this.inventory= Inventory.getInstance();
	}

	@Override
	protected void initialize() {
		subscribeEvent(CheckAvailabilityEvent.class, event-> {
			int price=inventory.checkAvailabilityAndGetPrice(event.getBookName());
			if(price!=-1 && event.getCustomer().getAvailableCreditAmount()>=price) {
				OrderResult o = inventory.take(event.getBookName());
				if (o == OrderResult.SUCCESSFULLY_TAKEN) {
					complete(event, price);}
				else{
					complete(event,-1);
				}
			}
			else{
				complete(event,-1);}
			});
		subscribeBroadcast(TerminateBroadcast.class, broadcast->terminate());
		d.countDown();
	}
}


