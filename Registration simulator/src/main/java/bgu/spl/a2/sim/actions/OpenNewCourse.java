package bgu.spl.a2.sim.actions;

import java.util.Collection;
import java.util.LinkedList;
import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

/**
 * @author Ittai
 * this action was submitted to department Actor
 */
public class OpenNewCourse extends Action<String> {

	protected String newCourseName;
	protected CoursePrivateState newCoursePrivateState;
	protected int availableSpaces;
	protected Collection<String> prerequisites;
	
	public OpenNewCourse(int availableSpaces, String newCourseName, Collection<String> prerequisites) {
		//init local variables
		this.newCoursePrivateState = new CoursePrivateState();
		this.newCourseName = newCourseName;
		this.prerequisites = prerequisites;
		this.availableSpaces = availableSpaces;
		
		//init abstract action variables
		subActionsList = new LinkedList<>();
		promisesList = new LinkedList<>();
		promise = new Promise<String>();
		setActionName("OpenNewCourse");
	}

	@Override
	protected void start() {
		newCoursePrivateState.getPrequisites().addAll(prerequisites);
		newCoursePrivateState.setAvailableSpots(availableSpaces);
		pool.submit(null, newCourseName, newCoursePrivateState);
		
		//add to the department's courses list
		((DepartmentPrivateState)submitedActorPrivateState).addCourse(newCourseName);
		
		//define completeTheActionCallback
		callback thenCallback;
		thenCallback = () ->{
			complete("New course: " + newCourseName + " was opened");
			};
						
		// call "then function" with completeTheActionCallback
		then(subActionsList, thenCallback);
	}
}