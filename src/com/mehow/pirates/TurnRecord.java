package com.mehow.pirates;

import java.io.Serializable;
import java.util.Stack;

//is it a good idea to make a step class? subclass Pair?
public class TurnRecord implements Serializable{
	// stuff for undoable classes to implement

	// instead of this, is an class better?
	// this needs thinking about, enemy uses them publicly but ship uses them
	// privatly
	public interface Undoable {
		// sticks a dummy turn in so that later new turns have an end to build
		// off.
		void newTurn();

		// public boolean canMakeMove();
		Cords getLatestCords();

		void makeStep(Cords cords);

		void undoTurn();
		
		void undoStep();

		int getStepCount();
	}

	// add isAlive variable so enemys can die + spawn
	Stack<Cords> stepCords;// maybe this should be a stack?

	public TurnRecord(Cords tStartCords) {
		stepCords = new Stack<Cords>();
		stepCords.add(tStartCords);
	}

	// always check with atFirstStep before call
	public void undoStep() {
		stepCords.pop();
	}

	public void addStepCords(Cords latestCords) {
		stepCords.add(latestCords);
	}

	public Cords getStepCords(int stepNo){
		return stepCords.get(stepNo);
	}
	/*
	// for animation steps
	public Cords getStepStartCords(int stepNo) {
		// -1 because the last Cords in Array only ever acts as an end cord
		if (stepNo < stepCords.size() - 1) {
			return stepCords.get(stepNo);
		} else {
			throw new IllegalArgumentException(
					"last step of a turn cannot act as start cords");
		}
	}

	public Cords getStepEndCords(int stepNo) {
		return stepCords.get(stepNo);
	}*/

	// --------------------
	public void clean() {
		Cords start = stepCords.get(0);
		stepCords.empty();
		stepCords.add(start);
	}

	public Cords getStartCords() {
		return stepCords.get(0);
	}

	public int getStepCount() {
		return stepCords.size();
	}

	public Cords getLatestCords() {
		return stepCords.get(stepCords.size() - 1);
	}
}
