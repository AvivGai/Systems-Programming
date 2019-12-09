package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.Semaphore;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo {
	private String bookTitle;
	private int amount;
	private int price;
	private Semaphore available;

	public BookInventoryInfo(String name, int amount, int price){
		this.bookTitle=name;
		this.price=price;
		this.amount=amount;
	}

	/**
	 * Retrieves the title of this book.
	 * <p>
	 * @return The title of this book.
	 */
	public String getBookTitle() {
		return this.bookTitle;
	}

	/**
	 * Retrieves the amount of books of this type in the inventory.
	 * <p>
	 * @return amount of available books.
	 */
	public int getAmountInInventory() {
		return amount;
	}


	public void setAmountInInventory(int i){
		this.amount=i;
	}

	/**
	 * Retrieves the price for  book.
	 * <p>
	 * @return the price of the book.
	 */
	public int getPrice() {
		return price;
	}

	/**
	 * Retrieves if book is available.
	 * <p>
	 * @return the Semaphore that specified how many copy of this book are available.
	 */
	public Semaphore getAvailable(){
		return this.available;
	}


	public void setSemaphore(int i){this.available= new Semaphore(i);}
}