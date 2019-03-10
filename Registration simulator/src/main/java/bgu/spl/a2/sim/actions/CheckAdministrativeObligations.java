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
 * this action was submitted to department actor
 */
public class CheckAdministrativeObligations extends Action<Boolean> {
	
	protected ArrayList<String> coursesList;
	protected ArrayList<String> studentsIDs;
	protected String computerName;
	
	public CheckAdministrativeObligations(ArrayList<String> coursesList, ArrayList<String> studentsIDs, String computerName) {
		//init local variables
		this.coursesList = coursesList;
		this.studentsIDs = studentsIDs;
		this.computerName = computerName;
		
		//init abstract action variables
		promise = new Promise<Boolean>();
		promisesList = new LinkedList<>();
		subActionsList = new LinkedList<>();
		setActionName("CheckAdministrativeObligations");
	}

	@Override
	protected void start() {
		Promise<Computer> computerPromise = pool.acquireComputerByName(computerName);
		promisesList.add(computerPromise);
		
		//define and send subactions
		for (String studentId : studentsIDs) {
			subActionsList.add(new CheckAndSighnStudent(coursesList, computerPromise));
			sendMessage(subActionsList.getLast(), studentId, new StudentPrivateState());
		}
				
		//define completeTheActionCallback
		
		callback callback;
		callback = () -> {
			//release to acquired computer
			pool.releaseComputerByName(computerName);				//method not created yet!!!!!!!
			complete(true);
		};
		
		then(subActionsList, callback);
	}
}