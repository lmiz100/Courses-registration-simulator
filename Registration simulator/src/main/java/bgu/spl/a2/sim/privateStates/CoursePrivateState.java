package bgu.spl.a2.sim.privateStates;

import java.util.ArrayList;
import java.util.List;

import bgu.spl.a2.PrivateState;

/**
 * this class describe course's private state
 */
public class CoursePrivateState extends PrivateState{

	private Integer availableSpots;
	private Integer registered;
	private List<String> regStudents;
	private List<String> prequisites;
	
	/**
 	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public CoursePrivateState() {
		super();
		this.availableSpots=new Integer(0); //default initialization value
		this.registered=new Integer(0);
		this.regStudents=new ArrayList<>();
		this.prequisites=new ArrayList<>();		
	}

	public Integer getAvailableSpots() {
		return availableSpots;
	}

	public Integer getRegistered() {
		return registered;
	}

	public List<String> getRegStudents() {
		return regStudents;
	}

	public List<String> getPrequisites() {
		return prequisites;
	}
	
	public void setAvailableSpots(int num) {
		availableSpots=num;
	}
	
	public void setRegStudents(int num) {
		registered=num;
	}
	
	public void addRegStudent(String studentId) {
		regStudents.add(studentId);		
	}
	
	public void addPrequisites(String toAdd) {
		prequisites.add(toAdd);		
	}
}
