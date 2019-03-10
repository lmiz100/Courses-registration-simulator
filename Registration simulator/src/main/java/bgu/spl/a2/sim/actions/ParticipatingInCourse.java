/**
 * 
 */
package bgu.spl.a2.sim.actions;

import java.util.ArrayList;
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
public class ParticipatingInCourse extends Action<String> {
	
	String studentId;
	Integer grade;
	
	public ParticipatingInCourse(String studentId, String grade) {
		//init local variables
		this.studentId = studentId;
		if (grade.equals("-")) 
			this.grade = -1;
		else 
			this.grade = Integer.parseInt(grade);
		
		//init abstract action variables
		subActionsList = new LinkedList<>();
		promisesList = new LinkedList<>();
		promise = new Promise<String>();
		setActionName("ParticipatingInCourse");
	}

	@Override
	protected void start() {
		
		//start function independent code
		boolean participateInAllPrerequisites = true, haveOpenSpots = false;
		callback thenCallback;
		CoursePrivateState submittedCousePrivateState = (CoursePrivateState)submitedActorPrivateState;
		ArrayList<String> prerequisites = (ArrayList<String>) submittedCousePrivateState.getPrequisites();
		
		//check if the student is already signed to the course
		if (submittedCousePrivateState.getRegStudents().contains(studentId)) {
			complete(studentId + " already participating in " + submitedActorId + " course");
			return;
		}
		
		for (String currentCourseName : prerequisites) {
			CoursePrivateState currentCourse = (CoursePrivateState)pool.getPrivaetState(currentCourseName);
			if(currentCourse != null && !currentCourse.getRegStudents().contains(studentId))
				participateInAllPrerequisites = false;
		}
		
		if(!participateInAllPrerequisites) {
			then(subActionsList,  thenCallback = () -> {
				complete("Student " + studentId + " doesn't meet the prerequisites");
			});
		}
		else {
			synchronized (submittedCousePrivateState.getAvailableSpots()) {
				int availablePlaces = submittedCousePrivateState.getAvailableSpots().intValue();
				if(availablePlaces > 0) {
					haveOpenSpots = true;
					//update course private state
					submittedCousePrivateState.setAvailableSpots(availablePlaces-1);
					submittedCousePrivateState.addRegStudent(studentId);
				}
			}
				if(haveOpenSpots) {
					//update student private state
					SetCourseGradeToStudent setGrade;
					if (this.grade.equals("-")) 
						setGrade = new SetCourseGradeToStudent(submitedActorId, -1);
					else 
						setGrade = new SetCourseGradeToStudent(submitedActorId, grade);
					
					subActionsList.add(setGrade);
					sendMessage(setGrade, studentId, new StudentPrivateState());
					
					then(subActionsList, thenCallback = () -> {
						boolean gradeExist, studentExistInCoursesStudentsList;
						synchronized (((StudentPrivateState)(pool.getPrivaetState(studentId))).getGrades()) {
							studentExistInCoursesStudentsList = submittedCousePrivateState.getRegStudents().contains(studentId);
							gradeExist = ((StudentPrivateState)(pool.getPrivaetState(studentId))).getGrades().get(submitedActorId) == grade;
							if (studentExistInCoursesStudentsList & gradeExist) {
								complete("Student " + studentId + " is now registered to " + submitedActorId);
							}
							else {
								pool.submit(new ParticipatingInCourse(studentId, ""+grade), submitedActorId, new CoursePrivateState());
							}
						}
					});
				}
				else {
					complete("No available places in course " + submitedActorId + " for student " + studentId);
				}
		}
	}
}