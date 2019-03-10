package bgu.spl.a2;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;



/**
 * an abstract class that represents an action that may be executed using the
 * {@link ActorThreadPool}
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 * @param <R> the action result type
 */
public abstract class Action<R> {
	
	/**
	 * @fields <callingActorId, callingActorPrivateState,  thenCallback, pool, subActionsList>
	 * are defined for every action @handle method
	 * @param thenCallback is used by @handle method to check if the action is ready to be complete
	 * 
	 * @fields <completeTheAction, promise, actionName> are defined by each action on the start method
	 * @param completeTheAction is the code to perform to complete the action
	 */
	
	protected ActorThreadPool pool;
	protected String submitedActorId;
	protected PrivateState submitedActorPrivateState;
	protected callback completeTheAction;
	
	protected LinkedList<Action<?>> subActionsList;
	protected LinkedList<Promise<?>> promisesList;
	protected Promise<R> promise;
	protected String actionName;
	
	

	/**
     * start handling the action - note that this method is protected, a thread
     * cannot call it directly.
     * 
     */
    protected abstract void start();

    /**
    *
    * start/continue handling the action
    *
    * this method should be called in order to start this action
    * or continue its execution in the case where it has been already started.
    *
    * IMPORTANT: this method is package protected, i.e., only classes inside
    * the same package can access it - you should *not* change it to
    * public/private/protected
    *
    */
   /*package*/ final void handle(ActorThreadPool pool, String actorId, PrivateState actorState) {
	  // if(MainTest.verbose)
		   //System.out.println(Thread.currentThread().getName() + " executing" + getActionName());			//changed
	   if(completeTheAction!=null)
		   completeTheAction.call();
	   else {
		   this.pool = pool;
		   this.submitedActorId = actorId;
		   this.submitedActorPrivateState = actorState;
		   start();
	   }
   }
    
    
    /**
     * add a callback to be executed once *all* the given actions results are
     * resolved
     * 
     * Implementors note: make sure that the callback is running only once when
     * all the given actions completed.
     *
     * @param actions
     * @param callback the callback to execute once all the results are resolved
     */
    protected final void then(Collection<? extends Action<?>> actions, callback callback) {
    	AtomicInteger counter = new AtomicInteger(actions.size());
		callback call = () -> {
			synchronized (counter) {
				counter.decrementAndGet();
				if(counter.get() == 0) {
			    	completeTheAction = callback;
			    	pool.submit(this, this.submitedActorId, this.submitedActorPrivateState);
				}
			}
			};
			
    	//an action with no subactions, no need to push again to the actor's queue
    	if(actions.size()==0) {
    		callback.call();
    		return;
    	}
			
		for (Action<?> currentAction : actions) {
			currentAction.getResult().subscribe(call);
		}
    }

    /**
     * resolve the internal result - should be called by the action derivative
     * once it is done.
     *
     * @param result - the action calculated result
     */
    protected final void complete(R result) {
 	  // if(MainTest.verbose)
 		//   System.out.println(result);							//changed
    	promise.resolve(result);
    }
    
    /**
     * @return action's promise (result)
     */
    public final Promise<R> getResult() {
    	return promise;
    }
    
    /**
     * send an action to an other actor
     * 
     * @param action
     * 				the action
     * @param actorId
     * 				actor's id
     * @param actorState
	 * 				actor's private state (actor's information)
	 *    
     * @return promise that will hold the result of the sent action
     */
	public Promise<?> sendMessage(Action<?> action, String actorId, PrivateState actorState){
		pool.submit(action, actorId, actorState);
        return action.getResult();
	}
	
	/**
	 * set action's name
	 * @param actionName
	 */
	public void setActionName(String actionName){
		this.actionName = actionName;
	}
	
	/**
	 * @return action's name
	 */
	public String getActionName(){
		return actionName;
	}
	
	public PrivateState getSubmitedActorPrivateState() {
		return submitedActorPrivateState;
	}
	
	public String getSubmitedActorId() {
		return submitedActorId;
	}
	
	public String toString() {							//changed
		if(promise != null && promise.isResolved())
			return ('\n' + Thread.currentThread().getName() +  " ##ACTION## " + getActionName() + " @ " + submitedActorId  + " # " + getResult().get() + '\n');
		return ('\n' + "##ACTION## " + getActionName() + " @ " + submitedActorId  + " # " + "not resolved yet" + '\n');
	}
}