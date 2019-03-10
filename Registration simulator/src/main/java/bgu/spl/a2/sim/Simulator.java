/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import bgu.spl.a2.Action;
import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.sim.actions.AddStudent;
import bgu.spl.a2.sim.actions.CheckAdministrativeObligations;
import bgu.spl.a2.sim.actions.CloseACourse;
import bgu.spl.a2.sim.actions.OpenNewCourse;
import bgu.spl.a2.sim.actions.OpeningNewplacesInaCourse;
import bgu.spl.a2.sim.actions.ParticipatingInCourse;
import bgu.spl.a2.sim.actions.RegisterWithPreferences;
import bgu.spl.a2.sim.actions.Unregister;

/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {
	private static Warehouse warehouse=new Warehouse();
	private static ArrayList<Action<?>> phase1=new ArrayList<>();
	private static ArrayList<Action<?>> phase2=new ArrayList<>();
	private static ArrayList<Action<?>> phase3=new ArrayList<>();
	public static ActorThreadPool actorThreadPool;
	
	/**
	* Begin the simulation Should not be called before attachActorThreadPool()
	*/
    public static void start() {
    	actorThreadPool.start();
		for(Action<?> action: phase1) //execute phase 1 action
			actorThreadPool.submit(action, action.getSubmitedActorId() , action.getSubmitedActorPrivateState()); 
		for(Action<?> action: phase2) //execute phase 2 actions
			actorThreadPool.submit(action, action.getSubmitedActorId() , action.getSubmitedActorPrivateState());
		for(Action<?> action: phase3) //execute phase 3 actions
			actorThreadPool.submit(action, action.getSubmitedActorId() , action.getSubmitedActorPrivateState());
    }
	
	/**
	* attach an ActorThreadPool to the Simulator, this ActorThreadPool will be used to run the simulation
	* 
	* @param myActorThreadPool - the ActorThreadPool which will be used by the simulator
	*/
	public static void attachActorThreadPool(ActorThreadPool myActorThreadPool){
		actorThreadPool=myActorThreadPool;
	}
	
	/**
	* shut down the simulation
	* returns list of private states
	*/
	public static HashMap<String,PrivateState> end(){
		try {
			actorThreadPool.shutdown();
		} catch (InterruptedException e) {
			return (HashMap<String,PrivateState>)actorThreadPool.getActors();
		}
		return (HashMap<String,PrivateState>)actorThreadPool.getActors();
	}
	
	
	public static int main(String [] args){
		Gson gson=new Gson();
		JsonReader reader=null;
		try {
			reader=new JsonReader(new FileReader(args[0]));
		} catch (FileNotFoundException e2) {}
		myObject jsonObject=gson.fromJson(reader, myObject.class);
		ActorThreadPool threadPoll=new ActorThreadPool(jsonObject.getThreads());
		attachActorThreadPool(threadPoll);
		setWarehouse(jsonObject.getComputers());
		phase1=convertToActions(jsonObject.getPhase1());
		phase2=convertToActions(jsonObject.getPhase2());
		phase3=convertToActions(jsonObject.getPhase3());
		start();
		HashMap<String, PrivateState> simulatorResult=end();
		FileOutputStream fout=null;
		try {
			fout = new FileOutputStream("result.ser");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ObjectOutputStream oos=null;
		try {
			oos = new ObjectOutputStream(fout);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			oos.writeObject(simulatorResult);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(fout!=null) {
				try {
					fout.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				
		}
		
		return 1;
	}
	
	private static class myObject {
		@SerializedName("threads")
		protected int threads;
		@SerializedName("Computers")
		protected ArrayList<Computer> computers;
		@SerializedName("Phase 1")
		protected ArrayList<actionType> Phase1;
		@SerializedName("Phase 2")
		protected ArrayList<actionType> Phase2;
		@SerializedName("Phase 3")
		protected ArrayList<actionType> Phase3;
		
		public myObject(int threads) {
			this.threads=threads;
			this.computers=new ArrayList<>();
			this.Phase1=new ArrayList<>();
			this.Phase1=new ArrayList<>();
			this.Phase1=new ArrayList<>();
		}
		
		protected int getThreads() {
			return this.threads;
		}
		
		protected ArrayList<Computer> getComputers() {
			return this.computers;
		}
		
		protected ArrayList<actionType> getPhase1() {
			return this.Phase1;
		}
		
		protected ArrayList<actionType> getPhase2() {
			return this.Phase2;
		}
		
		protected ArrayList<actionType> getPhase3() {
			return this.Phase3;
		}
	}
	
	private static class actionType {
		@SerializedName("Action")
		protected String actionName;
		@SerializedName("Department")
		protected String department;
		@SerializedName("Course")
		protected String course;
		@SerializedName("Space")
		protected String space;
		@SerializedName("Prerequisites")
		protected ArrayList<String> prerequisites;
		@SerializedName("Student")
		protected String studentId;
		@SerializedName("Grade")
		protected ArrayList<String> grade;
		@SerializedName("Number")
		protected String number;
		@SerializedName("Preferences")
		protected ArrayList<String> preferences;
		@SerializedName("Students")
		protected ArrayList<String> students;
		@SerializedName("Computer")
		protected String computer;
		@SerializedName("Conditions")
		protected ArrayList<String> conditions;
		
		protected String getActionName() {
			return this.actionName;
		}
		
		protected String getDepartment() {
			return this.department;
		}
		
		protected String getCourse() {
			return this.course;
		}
		
		protected String getSpace() {
			return this.space;
		}
		
		protected ArrayList<String> getPrerequisites() {
			return this.prerequisites;
		}
		
		protected String getStudentId() {
			return this.studentId;
		}
		
		protected ArrayList<String> getGrade() {
			return this.grade;
		}
		
		protected String getNumber() {
			return this.number;
		}
		
		protected ArrayList<String> getPreferences() {
			return this.preferences;
		}
		
		protected ArrayList<String> getStudents() {
			return this.students;
		}
		
		protected String getComputer() {
			return this.computer;
		}
		
		protected ArrayList<String> getConditions() {
			return this.conditions;
		}
	}
	
	protected static void setWarehouse(ArrayList<Computer> computerList) {
		for(Computer computer: computerList)
			warehouse.addComputer(computer);
	}
	
	protected static ArrayList<Action<?>> convertToActions(ArrayList<actionType> actions) {
		ArrayList<Action<?>> output=new ArrayList<>();
		for(actionType action: actions) {
			int index=actionIndex(action.getActionName());
			switch(index) {
				case 0: output.add(new OpenNewCourse(Integer.parseInt(action.getSpace()), action.getCourse(), action.getPrerequisites()));
						break;
				case 1: output.add(new AddStudent(action.getStudentId()));
						break;
				case 2: output.add(new ParticipatingInCourse(action.getStudentId(), action.getGrade().get(0)));
						break;
				case 3: output.add(new OpeningNewplacesInaCourse(Integer.parseInt(action.getNumber())));
						break;
				case 4: output.add(new RegisterWithPreferences(action.getStudentId(), action.getPreferences(), action.getGrade()));
						break;
				case 5: output.add(new Unregister(action.getStudentId())); //no course name to unregister from!!
						break;
				case 6: output.add(new CloseACourse(action.getCourse())); //no department name to close from!!
						break;
				case 7: output.add(new CheckAdministrativeObligations(action.getConditions(), action.getStudents(), action.getComputer()));
						break;
				default: System.out.println("invalid action"); 
						break;
			}
		}
		return output;
	}
	
	protected static int actionIndex(String actionName) { 
		String[] legalActions={"Open Course","Add Student","Participate In Course","Add Spaces","Register With Preferences","Unregister","Close Course","Administrative Check"};
		for(int i=0; i<legalActions.length; i++) {
			if(actionName.equals(legalActions[i])) 
				return i;
		}
		return -1; //illegal action
	}
}



