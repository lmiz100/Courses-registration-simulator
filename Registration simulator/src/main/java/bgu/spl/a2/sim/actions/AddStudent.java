/**
 * 
 */
package bgu.spl.a2.sim.actions;

import java.util.LinkedList;
import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

/**
 * @author Ittai
 * this action was submitted to department Actor 
 *
 */
public class AddStudent extends Action<String> {
	
	protected String studentID;
	
	public AddStudent(String studentID) {
		//init local variables
		this.studentID = studentID;
		
		//init abstract action variables
		subActionsList = new LinkedList<>();
		promisesList = new LinkedList<>();
		promise = new Promise<String>();
		setActionName("AddStudent");
	}

	@Override
	protected void start() {
		if (pool.getPrivaetState(studentID) != null) {
			complete("Student already exist thus not added");
		}
		else {
			pool.submit(null, studentID, new StudentPrivateState());
			((DepartmentPrivateState) submitedActorPrivateState).addStudent(studentID);
			complete("'" + this.studentID + "'" + " was added");
		}
	}
}