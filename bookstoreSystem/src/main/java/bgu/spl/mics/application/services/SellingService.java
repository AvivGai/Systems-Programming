package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;
import java.util.concurrent.CountDownLatch;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{
	private MoneyRegister moneyRegister;
	private int recipetNumber=0;
	private int tick=1;
	private CountDownLatch d;

	public SellingService(String name, CountDownLatch d) {
		super(name);
		moneyRegister= MoneyRegister.getInstance();
		this.d=d;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, broadcast->{
			this.tick=broadcast.get();
		});
		subscribeEvent(BookOrderEvent.class,event-> {
			int currTick= this.tick;
			Customer c= event.getCustomer();
			try {
				c.getAvailable().acquire();
			}
			catch (InterruptedException e){}
			CheckAvailabilityEvent ev= new CheckAvailabilityEvent(event.getBookName(), c);
			Future<Integer> f= (Future<Integer>)sendEvent(ev);
			if(f!=null){
			Integer price = f.get();
				if (price!=null && price != -1) {
					moneyRegister.chargeCreditCard(c, price);
					c.getAvailable().release();
					recipetNumber = recipetNumber++;
					OrderReceipt o = new OrderReceipt(this.recipetNumber, this.getName(), c.getId(), event.getBookName(), price, event.getOrderTick(), this.tick, currTick);
					moneyRegister.file(o);
					this.recipetNumber++;
					complete(event, o);
					DeliveryEvent d = new DeliveryEvent(c.getAddress(), c.getDistance());
					sendEvent(d);
				}
				else{
					complete(event,null);
					c.getAvailable().release();

				}}
			else{
				complete(event,null);
				c.getAvailable().release();
			}
	});
		subscribeBroadcast(TerminateBroadcast.class, broadcast->terminate());
		d.countDown();
	}
}

