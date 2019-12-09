package bgu.spl.mics.application.passiveObjects;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {

	private static class InventoryHolder {
		private static Inventory instance = new Inventory();
	}
	private ConcurrentHashMap<String, BookInventoryInfo> inventory;
	
	private Inventory() {}

	/**
     * Retrieves the single instance of this class.
     */
	public static Inventory getInstance() {
		return InventoryHolder.instance;
    }
	
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (BookInventoryInfo[ ] inventory ) {
		this.inventory = new ConcurrentHashMap<>();
		for(BookInventoryInfo book: inventory ){
			String bookTitle = book.getBookTitle();
			this.inventory.put(bookTitle, book);
		}
	}
	
	/**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
     */
	public OrderResult take (String book) {
		BookInventoryInfo b=inventory.get(book);
		Semaphore available = inventory.get(book).getAvailable();
		if(available.tryAcquire()==false){
			return OrderResult.NOT_IN_STOCK;
		}
		else{
		b.setAmountInInventory(b.getAmountInInventory()-1);
		return OrderResult.SUCCESSFULLY_TAKEN;}
	}
	
	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
	public int checkAvailabilityAndGetPrice(String book){
		int b= inventory.get(book).getAmountInInventory();
		if(b==0){
			return -1;
		}
		else{
		return inventory.get(book).getPrice();}
	}

	/**
     * 
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
	public void printInventoryToFile(String filename) {
		HashMap<String, Integer> map = new HashMap<>();
		for (Map.Entry e : this.inventory.entrySet()) {
			String title = (String)e.getKey();
			int amount = ((BookInventoryInfo)e.getValue()).getAmountInInventory();
			map.put(title, amount);
		}
		try{
		FileOutputStream file = new FileOutputStream(filename);
		ObjectOutputStream obj = new ObjectOutputStream(file);
		obj.writeObject(map);
		obj.close();
		file.close();
		}
		catch(IOException e){}
	}

	//test only method!!
	public void reset(){
		this.inventory=null;
	}
}