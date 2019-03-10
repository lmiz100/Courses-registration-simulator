/**
 * 
 */
package bgu.spl.a2.sim.actions;

import java.util.LinkedList;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

/**
 * @author Ittai
 * this action was submitted to course Actor
 */
public class CloseThisCourse extends Action<String> {
	
	public CloseThisCourse() {
		//init abstract action variables
		subActionsList = new LinkedList<>();
		promisesList = new LinkedList<>();
		promise = new Promise<String>();
		setActionName("CloseThisCourse");
	}

	@Override
	protected void start() {
		((CoursePrivateState)submitedActorPrivateState).setAvailableSpots(-1);
		
		//remove all registered students
		for (String student : ((CoursePrivateState)submitedActorPrivateState).getRegStudents()) {
			subActionsList.add(new Unregister(student));
		}
		
		for (Action<?> action : subActionsList) {
			sendMessage(action, submitedActorId, new CoursePrivateState());
		}
		
		//complete the action callback
		callback thenCallback;
		thenCallback = () ->{
			((CoursePrivateState)submitedActorPrivateState).setAvailableSpots(-1);
			complete("All students has been removed from " + submitedActorId + " available spots set to -1");
			};
			
		then(subActionsList, thenCallback);
	}

}
