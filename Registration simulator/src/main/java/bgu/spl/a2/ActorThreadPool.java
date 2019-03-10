package bgu.spl.a2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.*;

import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.Warehouse;



/**
 * represents an actor thread pool - to understand what this class does please
 * refer to your assignment.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class ActorThreadPool {
	private HashMap<String, ConcurrentLinkedQueue<Action<?>>> actorsQueueMap;
	private HashMap<String, AtomicBoolean> actorsBusyMap; //entry value is false when actor is free
	private HashMap<String, PrivateState> actorsStateMap;
	private VersionMonitor monitor;
	private List<PoolThread> threads; //list of workers threads
	private AtomicBoolean isOn; //indicate if actor thread pull is working
	private Warehouse warehouse;
	private CountDownLatch latch;

	/**
	 * creates a {@link ActorThreadPool} which has nthreads. Note, threads
	 * should not get started until calling to the {@link #start()} method.
	 *
	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 *
	 * @param nthreads
	 *            the number of threads that should be started by this thread
	 *            pool
	 */
	public ActorThreadPool(int nthreads) {
		this.actorsQueueMap=new HashMap<>();
		this.actorsBusyMap=new HashMap<>();
		this.actorsStateMap=new HashMap<>();
		this.monitor=new VersionMonitor();
		this.threads=new ArrayList<>();
		this.isOn=new AtomicBoolean(false);
		this.warehouse=new Warehouse();
		this.latch=new CountDownLatch(nthreads);
		for(int i=0; i<nthreads; i++) //create threads and add to the pool
			threads.add(new PoolThread(actorsQueueMap, actorsBusyMap, actorsStateMap, monitor, this));
	}
	
	protected CountDownLatch getLatch() {
		return this.latch;
	}

	/**
	 * getter for actors
	 * @return actors
	 */
	public Map<String, PrivateState> getActors(){
		return this.actorsStateMap;
	}
	
	/**
	 * getter for actor's private state
	 * @param actorId actor's id
	 * @return actor's private state
	 */
	public PrivateState getPrivaetState(String actorId){
		return actorsStateMap.get(actorId);
	}


	/**
	 * submits an action into an actor to be executed by a thread belongs to
	 * this thread pool
	 *
	 * @param action
	 *            the action to execute
	 * @param actorId
	 *            corresponding actor's id
	 * @param actorState
	 *            actor's private state (actor's information)
	 */
	public void submit(Action<?> action, String actorId, PrivateState actorState) {
		if(!actorsQueueMap.containsKey(actorId)) //actor doesn't exist
			addActor(actorId, actorState);
		actorsQueueMap.get(actorId).add(action);
		monitor.inc(); //update that version have changed
		//this.start();
	}

	/**
	 * closes the thread pool - this method interrupts all the threads and waits
	 * for them to stop - it is returns *only* when there are no live threads in
	 * the queue.
	 *
	 * after calling this method - one should not use the queue anymore.
	 *
	 * @throws InterruptedException
	 *             if the thread that shut down the threads is interrupted
	 */
	public void shutdown() throws InterruptedException {
		for(PoolThread thread: threads)
			thread.interrupt();
		isOn.set(false);
		latch.await();
	}

	/**
	 * start the threads belongs to this thread pool
	 */
	public void start() {
		if(!isOn.get()&&monitor.getVersion()==0&&isOn.compareAndSet(false, true)) //need to stars threads because there is action to execute and no thread have started yet
			for(PoolThread thread: threads)
				thread.start();
	}
	
	protected void addActor(String key, PrivateState state) { //add new actor to the Threadpool
		actorsQueueMap.put(key, new ConcurrentLinkedQueue<>());
		actorsStateMap.put(key, state);
		actorsBusyMap.put(key, new AtomicBoolean(false));
	}
	
	protected boolean isWorking() {
		return this.isOn.get();
	}
	
	public Promise<Computer> acquireComputerByName(String computerType) {
		return warehouse.acquireCompByName(computerType);
	}
	
	protected Promise<Computer> acquireComputer() {
		return warehouse.acquireComp();
	}
	
	public void releaseComputerByName(String computerType) {
		warehouse.releaseCompByName(computerType);
	}

}
