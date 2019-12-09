package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
	public static void main(String[] args) {
		Path p= Paths.get(args[0]);
		String s= p.toAbsolutePath().toString();
		Gson gson= new Gson();
		jason j=null;
		try{
			JsonReader read= new JsonReader(new FileReader(s));
			j= gson.fromJson(read,jason.class);
		}
		catch(FileNotFoundException e){}
		Inventory inventory = Inventory.getInstance();
		for(BookInventoryInfo b: j.initialInventory){
			b.setSemaphore(b.getAmountInInventory());
		}
		inventory.load(j.initialInventory);
		ResourcesHolder resourcesHolder = ResourcesHolder.getInstance();
		resourcesHolder.load(j.initialResources[0].vehicles);
		List<Thread> allThreads = new LinkedList<>();
		int numOfSelling = j.services.selling;
		int numOfInventory = j.services.inventoryService;
		int numOfLogistic= j.services.logistics;
		int numOfResources= j.services.resourcesService;
		int numOfAPI= j.services.customers.length;
		int numOfServices = numOfSelling+numOfInventory+numOfLogistic+numOfResources+numOfAPI;
		CountDownLatch d= new CountDownLatch(numOfServices);
		//create Time Service
		Integer speed = j.services.time.get("speed").getAsInt();
		Integer duration = j.services.time.get("duration").getAsInt();
		TimeService timeService = new TimeService("timeS", speed,duration);
		Thread timeThread = new Thread(timeService);
		allThreads.add(timeThread);
		//create Selling Service
		SellingService[] sellingArr = new SellingService[numOfSelling];
		for(int i = 0 ; i<numOfSelling; i++) {
			sellingArr[i] = new SellingService("SellingService " + i, d);
			Thread t = new Thread(sellingArr[i]);
			allThreads.add(t);
			t.start();
		}
		//create Inventory Service
		InventoryService[] inventoryArr = new InventoryService[numOfInventory];
		for(int i = 0; i<numOfInventory; i++){
			inventoryArr[i] = new InventoryService("InventoryService "+i,d);
			Thread t = new Thread(inventoryArr[i]);
			allThreads.add(t);
			t.start();
		}
		//create Logistic Service
		LogisticsService[] logisticArr = new LogisticsService[numOfLogistic];
		for(int i = 0 ; i<numOfLogistic; i++) {
			logisticArr[i] = new LogisticsService("LogisticService " + i,d);
			Thread t = new Thread(logisticArr[i]);
			allThreads.add(t);
			t.start();
		}
		//create Resources Service
		ResourceService[] resourcesArr = new ResourceService[numOfResources];
		for(int i = 0 ; i<numOfResources; i++) {
			resourcesArr[i] = new ResourceService("ResourceService "+i,d);
			Thread t = new Thread(resourcesArr[i]);
			allThreads.add(t);
			t.start();
		}
		//create API Services
		APIService[] APIArr = new APIService[numOfAPI];
		for(int i=0; i<APIArr.length; i++){
			APIArr[i] = new APIService("APIService "+i,j.services.customers[i],d);
			Thread t = new Thread(APIArr[i]);
			allThreads.add(t);
			t.start();
		}
		try{
			d.await();
		}
		catch (InterruptedException e){}
		timeThread.start();
		for(Thread t: allThreads){
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	//print to output files
		inventory.printInventoryToFile(args[2]);
		MoneyRegister money = MoneyRegister.getInstance();
		money.printOrderReceipts(args[3]);
		try{
		FileOutputStream file = new FileOutputStream(args[4]);
		ObjectOutputStream obj = new ObjectOutputStream(file);
		obj.writeObject(money);
		obj.close();
		file.close();
		}catch(IOException e){}
		HashMap<Integer, Customer> customersMap = new HashMap<>();
		for (Customer c: j.services.customers){
			customersMap.put(c.getId(), c);
		}
		try{
			FileOutputStream file = new FileOutputStream(args[1]);
			ObjectOutputStream obj = new ObjectOutputStream(file);
			obj.writeObject(customersMap);
			obj.close();
			file.close();
		}catch(IOException e){}
	}
}