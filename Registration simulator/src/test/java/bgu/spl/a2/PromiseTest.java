package bgu.spl.a2;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PromiseTest {
	
	Promise<Integer> p;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		p=new Promise<>();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGet() {
		try{
			p.get();
			fail(); //didnt thrown exception as needed when call get but didnt resolved yet
		}
		catch(IllegalStateException ex){
			try{
				p.resolve(10);
				double expected=10;
				double actual=p.get().intValue();
				assertEquals("testing get", expected, actual, 0);
			}
			catch(Exception e){
				fail();
			}
		}
	}

	@Test
	public void testIsResolved() {
		try{
			if(p.isResolved())
				fail();
			p.resolve(-2);
			assertTrue("testing isResolved", p.isResolved()); //expected for true	
		}
		catch(Exception ex) {
			fail();
		}
	}

	@Test
	public void testResolve() {
		p.resolve(100);
		try{
			p.resolve(2);
			fail();
		}
		catch(IllegalStateException ex){
			try{
				double expected=100;
				double actual=p.get().intValue();
				assertEquals("testing resolve", expected, actual, 0);
			}
			catch(Exception e){
				fail();
			}
		}
		catch(Exception ex){
			fail();
		}
	}

	@Test
	public void testSubscribe() {
		try{
			p.subscribe(new callback() {
				
				@Override
				public void call() {
					throw new RuntimeException();
					
				}
			});
		}
		catch(RuntimeException ex) { //p didn't solved yet
			if(!p.isResolved())
				fail();
		}
		catch(Exception ex) {
			fail();
		}
		try{
			p.resolve(-5);
		}
		catch(Exception ex) { 
			if(!(ex instanceof RuntimeException))
				fail();
			else{
				try{
					Thread.sleep(1000);
				}
				catch(Exception e){}
				System.out.println(p.callsNumber());
				assertTrue("testing subscribe", p.callsNumber()==0); //check if after executed callback it removed from promise	
			}
		}
	}
	

}
