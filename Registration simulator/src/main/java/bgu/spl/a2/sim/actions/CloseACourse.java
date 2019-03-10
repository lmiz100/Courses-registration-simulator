/**
 * 
 */
package bgu.spl.a2.sim.actions;

import java.util.LinkedList;
import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

/**
 * @author Ittai
 * this action was submitted to department Actor
 *
 */
public class CloseACourse extends Action<String> {
	
	String courseName;
	
	public CloseACourse(String courseName) {
		//init local variables
		this.courseName = courseName;
		
		//init abstract action variables
		subActionsList = new LinkedList<>();
		promisesList = new LinkedList<>();
		promise = new Promise<String>();
		setActionName("Close course");
	}

	@Override
	protected void start() {

		subActionsList.add(new CloseThisCourse());
		
		for (Action<?> action : subActionsList) {
			sendMessage(action, courseName, new CoursePrivateState());
		}
		
		//define completeTheActionCallback
		callback thenCallback;
		thenCallback = () ->{
			((DepartmentPrivateState)submitedActorPrivateState).getCourseList().remove(courseName);	//remove from department
			complete("Course " + courseName + " has closed");
			};
						
		// call "then function" with completeTheActionCallback
		then(subActionsList, thenCallback);
	}
}