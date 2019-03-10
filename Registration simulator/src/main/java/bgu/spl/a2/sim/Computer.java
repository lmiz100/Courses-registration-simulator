package bgu.spl.a2.sim;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class Computer {

	@SerializedName("Type")
	String computerType;
	@SerializedName("Sig Fail")
	long failSig;
	@SerializedName("Sig Success")
	long successSig;
	
	
	public Computer(String computerType) {
		this.computerType = computerType;
	}
	
	/**
	 * this method checks if the courses' grades fulfill the conditions
	 * @param courses
	 * 							courses that should be pass
	 * @param coursesGrades
	 * 							courses' grade
	 * @return a signature if couersesGrades grades meet the conditions
	 */
	public long checkAndSign(List<String> courses, Map<String, Integer> coursesGrades){
		for(String courseName: courses){
			Integer studentGrade=coursesGrades.get(courseName);
			if(studentGrade==null||studentGrade.intValue()<56) //check if student didn't took part in a course or fail at it
				return failSig; 
		}
		return successSig;
	}
	
	protected String getComputerType() {
		return this.computerType;
	}
	
	protected void setFailSig(long failSig) {
		this.failSig=failSig;
	}
	
	protected void setSuccessSig(long successSig) {
		this.successSig=successSig;
	}
}
