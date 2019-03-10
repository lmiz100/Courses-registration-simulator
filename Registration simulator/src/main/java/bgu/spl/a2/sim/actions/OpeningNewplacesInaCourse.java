/**
 * 
 */
package bgu.spl.a2.sim.actions;

import java.util.LinkedList;
import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

/**
 * @author Ittai
 * this action was submitted to course Actor
 */
public class OpeningNewplacesInaCourse extends Action<String> {
	
	protected int additionSpaces;
	
	public OpeningNewplacesInaCourse(int additionSpaces) {
		//init local variables
		this.additionSpaces = additionSpaces;

		//init abstract action variables
		subActionsList = new LinkedList<>();
		promisesList = new LinkedList<>();
		promise = new Promise<String>();
		setActionName("OpeningNewplacesInaCourse");
	}

	@Override
	protected void start() {
		
		synchronized (((CoursePrivateState)(submitedActorPrivateState)).getAvailableSpots()) {
			int availableSpots = ((CoursePrivateState)(submitedActorPrivateState)).getAvailableSpots().intValue();
			if (availableSpots == -1) {
				complete("Course is closed, cannot add places");
			}
			else {
				((CoursePrivateState)(submitedActorPrivateState)).setAvailableSpots(availableSpots + additionSpaces);
				complete(additionSpaces + " places add to: " + submitedActorId);
			}
		}
	}
}