package bgu.spl.a2.sim;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import bgu.spl.a2.Promise;

/**
 * 
 * this class is related to {@link Computer}
 * it indicates if a computer is free or not
 * 
 * Note: this class can be implemented without any synchronization. 
 * However, using synchronization will be accepted as long as the implementation is blocking free.
 *
 */
public class SuspendingMutex {
	private AtomicBoolean isAvailable;
	private ConcurrentLinkedQueue<Promise<Computer>> toResole;
	private Computer computer;
	
	/**
	 * Constructor
	 * @param computer
	 */
	public SuspendingMutex(Computer computer){
		this.isAvailable=new AtomicBoolean(true);
		this.toResole=new ConcurrentLinkedQueue<>();
		this.computer=computer;
	}
	/**
	 * Computer acquisition procedure
	 * Note that this procedure is non-blocking and should return immediatly
	 * 
	 * @return a promise for the requested computer
	 */
	public Promise<Computer> down(){
		Promise<Computer> outputPromise=new Promise<>();
		if(isAvailable.get()&&isAvailable.compareAndSet(true, false)) {  //the computer available
			outputPromise.resolve(computer);
		}
		else
			toResole.add(outputPromise);
		return outputPromise;
	}
	/**
	 * Computer return procedure
	 * releases a computer which becomes available in the warehouse upon completion
	 */
	public void up(){
		if(toResole.isEmpty()) //nobody is waiting for computer
			isAvailable.set(true);
		else 
			toResole.poll().resolve(computer);
	}
	
	protected Computer getComputer() {
		return this.computer;
	}
}
