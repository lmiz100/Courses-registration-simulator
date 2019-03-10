/**
 * 
 */
package bgu.spl.a2.sim.actions;

import java.util.LinkedList;
import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

/**
 * @author Ittai
 * this action was submitted to course Actor
 *
 */
public class Unregister extends Action<String> {
	
	protected String studentId;
	
	public Unregister(String studentId) {
		//init local variables
		this.studentId = studentId;
		
		//init abstract action variables
		subActionsList = new LinkedList<>();
		promisesList = new LinkedList<>();
		promise = new Promise<String>();
		setActionName("Unregister");
	}

	@Override
	protected void start() {
		
		SetCourseGradeToStudent removeStudentFromCourse = new SetCourseGradeToStudent(submitedActorId, Integer.MIN_VALUE);
		subActionsList.add(removeStudentFromCourse);
		sendMessage(removeStudentFromCourse, studentId, new StudentPrivateState());
		
		callback thenCallback;
		thenCallback = () -> {
			((CoursePrivateState)submitedActorPrivateState).getRegStudents().remove(studentId);
			int availableSpots = ((CoursePrivateState)submitedActorPrivateState).getAvailableSpots().intValue();
			((CoursePrivateState)submitedActorPrivateState).setAvailableSpots(availableSpots+1);
			complete(studentId + " was unregistered from course " + submitedActorId);
		};

			then(subActionsList, thenCallback);
	}
}