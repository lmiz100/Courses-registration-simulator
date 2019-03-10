package bgu.spl.a2;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class PoolThread extends Thread {
	private HashMap<String, ConcurrentLinkedQueue<Action<?>>> actorsQueueMap;
	private HashMap<String, AtomicBoolean> actorsBusyMap;
	private HashMap<String, PrivateState> actorsStateMap;
	private VersionMonitor monitor;
	private ActorThreadPool tp;
	private CountDownLatch latch;
	
	public PoolThread(HashMap<String, ConcurrentLinkedQueue<Action<?>>> queueMap,HashMap<String, AtomicBoolean> actorsBusyMap, HashMap<String, PrivateState> actorsStateMap, VersionMonitor monitor, ActorThreadPool tp) {
		this.actorsQueueMap=queueMap;
		this.actorsBusyMap=actorsBusyMap;
		this.actorsStateMap=actorsStateMap;		
		this.monitor=monitor;
		this.tp=tp;
		this.latch=tp.getLatch();
	}
	
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			int version=monitor.getVersion();
			String freeActorKey=searchAction();
			while(freeActorKey.equals("")&&!Thread.currentThread().isInterrupted()) {
				try {
					monitor.await(version); //no action to execute so the thread waits
				} catch (InterruptedException e) {
					if(!tp.isWorking()) //if the ThreadPool shutdown and the thread was interrupted while waiting
						Thread.currentThread().interrupt();
				}
				version=monitor.getVersion(); //if the version was change while searching the thread will keep search for actions rather than wait
				freeActorKey=searchAction();
			}
			if(tp.isWorking()&&!freeActorKey.equals("")) { //if threadPoll off we don't want to execute action 
				Action<?> toExecute=actorsQueueMap.get(freeActorKey).poll();
				toExecute.handle(tp, freeActorKey, actorsStateMap.get(freeActorKey)); //start execute the action
				actorsStateMap.get(freeActorKey).addRecord(toExecute.getActionName()); //add the action to actor's history
				actorsBusyMap.get(freeActorKey).set(false); //release the actor after execute the action
				monitor.inc(); //update monitor version after execution
			}
		}
		this.latch.countDown();
	}
	
	private String searchAction() {
		String output="";
		//boolean execute=false;
		if(tp.isWorking()) { 
			HashMap<String, AtomicBoolean> copy=(HashMap<String, AtomicBoolean>) actorsBusyMap.clone(); //create a copy of actorBusyMap
			for(Map.Entry<String, AtomicBoolean> entry: copy.entrySet()) {
				if(entry.getValue().get()==false)   //the actor is free
					if(!actorsQueueMap.get(entry.getKey()).isEmpty()&&entry.getValue().compareAndSet(false, true)) { //there is action in actor's queue
						//execute=entry.getValue().compareAndSet(false, true); //check that no other thread catch the actor before this thread
						//if(execute) {
							output=entry.getKey();
							break; //end loop when found action
						//}
					}
			}
		}
		return output;
	}
	
	

}
