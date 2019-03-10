/**
 * 
 */
package bgu.spl.a2.sim.actions;

import java.util.ArrayList;
import java.util.LinkedList;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

/**
 * @author Ittai
 * this action was submitted to student actor
 *
 */
public class CheckAndSighnStudent extends Action<String> {
	
	ArrayList<String> coursesList;
	Promise<Computer> computerPromise;
	
	public CheckAndSighnStudent(ArrayList<String> coursesList, Promise<Computer> computerPromise) {
		//init local variables
		this.coursesList = coursesList;
		this.computerPromise = computerPromise;
		
		//init abstract action variables
		promise = new Promise<String>();
		promisesList = new LinkedList<>();
		subActionsList = new LinkedList<>();
		setActionName("CheckAndSighnStudent");
	}

	@Override
	protected void start() {
		callback completeTheAction;
		completeTheAction = () -> {
			StudentPrivateState studentPrivateState = ((StudentPrivateState)submitedActorPrivateState);
			long signiture;
			signiture = computerPromise.get().checkAndSign(coursesList, studentPrivateState.getGrades());
			studentPrivateState.setSignature(signiture);
			complete("Student "+submitedActorId+" is singed");
		};
		
		computerPromise.subscribe(completeTheAction);
	}
}