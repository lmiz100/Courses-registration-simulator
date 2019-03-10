/**
 * 
 */
package bgu.spl.a2.sim.actions;

import java.util.LinkedList;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

/**
 * @author Ittai
 * this action is submitted to student actor
 */
public class SetCourseGradeToStudent extends Action<Boolean> {
	String courseName;
	Integer grade;
	
	public SetCourseGradeToStudent(String courseName, Integer grade) {
		//init local variables
		this.courseName = courseName;
		this.grade = grade;
		
		//init abstract action variables
		subActionsList = new LinkedList<>();
		promisesList = new LinkedList<>();
		promise = new Promise<Boolean>();
		setActionName("SetCourseGradeToStudent");
	}

	@Override
	protected void start() {
		if(grade != Integer.MIN_VALUE)
			((StudentPrivateState)submitedActorPrivateState).setGrades(courseName, grade);
		else
			((StudentPrivateState)submitedActorPrivateState).getGrades().remove(courseName);
		complete(true);
	}
}