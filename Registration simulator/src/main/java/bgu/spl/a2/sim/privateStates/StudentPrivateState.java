package bgu.spl.a2.sim.privateStates;

import java.util.HashMap;

import javax.xml.crypto.dsig.SignedInfo;

import bgu.spl.a2.PrivateState;

/**
 * this class describe student private state
 */
public class StudentPrivateState extends PrivateState{

	private HashMap<String, Integer> grades;
	private long signature;
	
	/**
 	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public StudentPrivateState() {
		super();
		this.grades=new HashMap<>();
		this.signature=0; //default initialization value
	}

	public HashMap<String, Integer> getGrades() {
		return grades;
	}

	public long getSignature() {
		return signature;
	}
	
	public void setGrades(String key, Integer value) {
		grades.put(key, value);
	}
	
	public void setSignature(long sig) {
		signature=sig;
	}

}
