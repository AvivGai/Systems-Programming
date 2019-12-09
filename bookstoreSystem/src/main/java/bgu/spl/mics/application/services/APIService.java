package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{
	private int tick=1;
	private HashMap<Integer, List<String>> tickOrders;
	private Customer customer;
	private List<Future<OrderReceipt>> receipts= new LinkedList<>();
	private CountDownLatch d;

	public APIService(String name,Customer customer, CountDownLatch d) {
		super(name);
		this.d=d;
		this.customer=customer;
		tickOrders= new HashMap<>();
		for(OrderSchedule o:customer.getOrderSchedule()) {
			if(!this.tickOrders.containsKey(o.tick)){
				this.tickOrders.put(o.tick,new LinkedList<>());
				this.tickOrders.get(o.tick).add(o.bookTitle);
			}
			else{
				this.tickOrders.get(o.tick).add(o.bookTitle);
			}
		}
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, broadcast-> {
			this.tick=broadcast.get();
			List<String> books= this.tickOrders.get(this.tick);
			if(books!=null) {
				for (String s : books) {
					BookOrderEvent ev = new BookOrderEvent(s, customer, this.tick);
					Future<OrderReceipt> futureObject = sendEvent(ev);
					receipts.add(futureObject);

				}
				for(Future<OrderReceipt> f : receipts) {
					if(f!=null){
						OrderReceipt orderReceipt = f.get();
						if (orderReceipt != null) {
							customer.addReceipt(orderReceipt);}
				}}
			}
		});
		subscribeBroadcast(TerminateBroadcast.class, broadcast->terminate());
		d.countDown();
	}
}
