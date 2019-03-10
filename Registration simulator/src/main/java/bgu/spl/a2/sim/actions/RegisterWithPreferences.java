/**
 * 
 */
package bgu.spl.a2.sim.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

/**
 * @author Ittai
 * this action was submitted to course actor
 */
public class RegisterWithPreferences extends Action<String> {
	String studentId;
	ArrayList<String> preferencesList, gradesList;
	
	public RegisterWithPreferences(String studentId, Collection<String> preferencesList, Collection<String> gradesList) {
		//init local variables
		this.studentId = studentId;
		this.preferencesList = new ArrayList<String>();
		this.gradesList = new ArrayList<String>();
		this.preferencesList.addAll(preferencesList);
		this.gradesList.addAll(gradesList);
		
		//init abstract action variables
		subActionsList = new LinkedList<>();
		promisesList = new LinkedList<>();
		promise = new Promise<String>();
		setActionName("RegisterWithPreferences");
	}

	@Override
	protected void start() {
		if (preferencesList.size()==0) {
			complete("unable to register to any of the courses");
			return;
		}
				
		subActionsList.add(new ParticipatingInCourse(studentId, gradesList.get(0)));
		promisesList.add(sendMessage(subActionsList.get(0), preferencesList.get(0), new CoursePrivateState()));
		gradesList.remove(0);
		preferencesList.remove(0);
		
		callback thenCallback;
		thenCallback = () -> {
		if (((String)promisesList.get(0).get()).contains(" is now registered to ")) {
			complete("registered!");
		}
		else {
			//new RegisterWithPreferences(studentId, preferencesList, gradesList);
			sendMessage(new RegisterWithPreferences(studentId, preferencesList, gradesList), preferencesList.get(0), new CoursePrivateState());
		}
		};
		
		then(subActionsList, thenCallback);
	}

}
