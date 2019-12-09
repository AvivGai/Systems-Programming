package Java;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InventoryTest {
    private Inventory inventory;
    private BookInventoryInfo[] bookInventory= new BookInventoryInfo[1];
    private BookInventoryInfo b;

    @Before
    public void setUp(){
        inventory=  Inventory.getInstance();
        b=new BookInventoryInfo("book",10,5);
        bookInventory[0]=b;
    }

    @Test
    public void getInstance() throws Exception {
        assertTrue("the function doesn't return an Inventory instance",this.inventory!=null);
    }


    @Test
    public void load() throws Exception {
        this.inventory.load(bookInventory);
        assertTrue("the function doesn't load the book",this.inventory.take(b.getBookTitle()).equals(OrderResult.SUCCESSFULLY_TAKEN));
    }

    @Test
    public void take() throws Exception {
        int amount= b.getAmountInInventory();
        this.inventory.load(bookInventory);
        assertTrue("the function doesn't take the book",this.inventory.take(b.getBookTitle()).equals(OrderResult.SUCCESSFULLY_TAKEN));
        assertTrue("the function doesn't reduce the amount of the book",b.getAmountInInventory()==(amount-1));
    }

    @Test
    public void checkAvailabiltyAndGetPrice() throws Exception {
        this.inventory.load(bookInventory);
        assertTrue("the function doesn't return the book price",this.inventory.checkAvailabilityAndGetPrice(b.getBookTitle())==(b.getPrice()));
    }

    @After
    public void delete(){
        this.inventory.reset();
    }


}