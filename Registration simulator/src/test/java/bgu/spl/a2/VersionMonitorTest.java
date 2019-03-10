package bgu.spl.a2;

import static org.junit.Assert.*;



import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class VersionMonitorTest {
	
	VersionMonitor vm1;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		vm1=new VersionMonitor();
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testGetVersion() {
		double expected=0;
		double actual=vm1.getVersion();
		assertEquals("testing getVersion", expected, actual, 0);
	}

	@Test
	public void testInc() {
		double expected=1;
		vm1.inc();
		double actual=vm1.getVersion();
		assertEquals("testing inc", expected, actual, 0);
	}

	@Test
	public void testAwait() {
		Thread t1=new Thread(new Runnable() { //anonymous class
			
			@Override
			public void run() {
				boolean throwEx=false; //check if the exception is InterruptedException as needed
				try{
					vm1.await(0);	
				}
				catch(Exception e){
					throwEx=true;
					if(!(e instanceof InterruptedException))
						fail();
				}
				if(!throwEx) //didnt throw exception although interrupted 
					fail();
			}
		});
		
		//check if t1 is waiting as needed
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		t1.start();
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		if(!t1.getState().name().equals("WAITING"))
			fail();
		
		//check if exception throwing when t1 interrupted
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		t1.interrupt();
		
		//test if t2 awakes and terminated after version=1
		Thread t2=new Thread(new Runnable() { //anonymous class
			
			@Override
			public void run() {
				try{
					vm1.await(0);	
				}
				catch(InterruptedException e ){}
				catch(Exception e){
					fail();
				}
			}
		});
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		t2.start();
		vm1.inc();
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		assertTrue("ends await testing", t2.getState().name().equals("TERMINATED")); //t2 should be terminated
	}
}

