package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {
	private int id;
	private String name;
	private String address;
	private int distance;
	private CreditCard creditCard;
	private OrderSchedule[] orderSchedule;
	private List<OrderReceipt> receipts;
	private Semaphore available= new Semaphore(1);


	public Customer(){
		receipts = new ArrayList<>();
	}

	/**
	 * Retrieves the name of the customer.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves the ID of the customer  .
	 */
	public int getId() {
		return id;
	}

	/**
	 * Retrieves the address of the customer.
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Retrieves the distance of the customer from the store.
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * Retrieves a list of receipts for the purchases this customer has made.
	 * <p>
	 * @return A list of receipts.
	 */
	public List<OrderReceipt> getCustomerReceiptList() {
		return receipts;
	}

	/**
	 * Retrieves the amount of money left on this customers credit card.
	 * <p>
	 * @return Amount of money left.
	 */
	public int getAvailableCreditAmount() {
		return creditCard.amount;
	}

	/**
	 * Retrieves this customers credit card serial number.
	 */
	public int getCreditNumber() {
		return creditCard.number;
	}

	public void pay(int i) {
		if(i<=getAvailableCreditAmount()) {
			creditCard.amount = getAvailableCreditAmount()-i;
		}
	}

	public OrderSchedule[]  getOrderSchedule() {
		return orderSchedule;
	}

	public void addReceipt(OrderReceipt orderReceipt){
		this.receipts.add(orderReceipt);
	}

	public Semaphore getAvailable(){
		return this.available;
	}
}