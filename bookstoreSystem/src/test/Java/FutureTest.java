package Java;

import bgu.spl.mics.Future;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {
    private Future<String> f;
    @Before
    public void setup(){
        f= new Future<>();
        f.resolve("string");
    }

    @Test
    public void get() throws Exception {

        assertEquals("get function didn't return the result object",f.get(),"string");
    }

    @Test
    public void resolve() throws Exception {
        assertTrue("accepted filed wasn't changed",f.isDone());
        assertEquals("get function didn't return the result object",f.get(),"string");
    }

    @Test
    public void isDone() throws Exception {
        assertTrue("accepted filed wasn't changed",f.isDone());
    }

    @Test
    public void get1() throws Exception {
        long time= 1000;
        long curnn= System.currentTimeMillis();
        assertEquals("get function didn't return the result object",f.get(time, TimeUnit.MILLISECONDS),"string");
        assertTrue(System.currentTimeMillis()-curnn<time);
    }

}