package bgu.spl.a2.sim;

import java.util.ArrayList;

import bgu.spl.a2.Promise;

/**
 * represents a warehouse that holds a finite amount of computers
 *  and their suspended mutexes.
 * 
 */
public class Warehouse {
	private ArrayList<SuspendingMutex> computerList=new ArrayList<>();
	
	protected void addComputer(Computer comp) {
		computerList.add(new SuspendingMutex(comp));
	}
	
	public Promise<Computer> acquireCompByName(String computerType) {
		for(SuspendingMutex comp: computerList) 
			if(comp.getComputer().getComputerType().equals(computerType))
				return comp.down();
		return null; //there is no computer with this name
	}
	
	public Promise<Computer> acquireComp() {
		if(!computerList.isEmpty())
			return computerList.get(0).down();
		return null; //the warehouse is empty
	}
	
	public void releaseCompByName(String computerType) {
		for(SuspendingMutex comp: computerList) 
			if(comp.getComputer().getComputerType().equals(computerType)) {
				comp.up();
				return;
			}
	}
	
}
