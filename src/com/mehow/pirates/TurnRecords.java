package com.mehow.pirates;

import com.mehow.pirates.gameObjects.InterStep;

import java.io.Serializable;
import java.util.Stack;

//if a step isn't to an adjacent tile, animating the move may break

//this should arugably be merged into mine/ship/enemy classes and become an interface

//"dead" functions should maybe be moved into new class, give instances to "killable" gameObjects
public class TurnRecords implements TurnRecord.Undoable, Serializable{

    //have first cord stored separately from the stack?
    //how to make this work with objects that can spawn/de-spawn
    Stack<TurnRecord> turnRecordStack;
    
    boolean exists;
    
    public TurnRecords(Cords startCords) {//be this shit be this good probably shit you bad human
        turnRecordStack = new Stack<TurnRecord>();
        turnRecordStack.add(new TurnRecord(startCords));
        exists = true;
        //dead = false;
    }

    public void newTurn() {
        turnRecordStack.push(new TurnRecord(turnRecordStack.peek().getLatestCords()));
    }

    public Cords getLatestCords() {
        return turnRecordStack.peek().getLatestCords();
    }

    public Cords getStartCords() {
        return turnRecordStack.peek().getStartCords();
    }

    public void makeStep(Cords cords) {
        turnRecordStack.peek().addStepCords(cords);
    }

    public void undoStep() {
    	if(turnRecordStack.size()==1 && turnRecordStack.peek().getStepCount()==1){
    		exists = false;
    	}else{
    		turnRecordStack.peek().undoStep();
    	}
    }
    
    //always check with atFirstTurn before call
    //i wonder if "dead" should be split into a different functino and isDead tested by gamelogic?
    public void undoTurn() {
    	if(turnRecordStack.size()==1){
    		exists = false;
    	}else{
    		turnRecordStack.pop();
        	turnRecordStack.peek().clean();
        }
    }
    public boolean exists(){
    	return exists;
    }

    public int getStepCount() {
        return turnRecordStack.peek().getStepCount();
    }

    //using a pair type would be safer
    public InterStep getCurrentTurnInterStep(int interStepNo) {
        TurnRecord turnRecord = turnRecordStack.peek();
        return new InterStep(turnRecord.getStepCords(interStepNo), turnRecord.getStepCords(interStepNo+1));
    }
    
    /*again, dead class crap
    //these 2 variables are mutualy exclusive?
    int turnsSinceDied;//use this to know when to "resurect" using undos
    int stepSinceDied;
    boolean dead;//this could be implicit in the above var, but seemed crap
    */
    
    /*Don;t think this is needed, implicit based on mapData object holding this
    public void kill() {
        dead = true;
        turnsSinceDied = 0;
        stepSinceDied = 0;
    }

    public boolean isDead() {
        return dead;
    }*/

        /*implementation for death
    for implementation should be moved to new "deadturn" class
    stick an instance in each "killable" object
    mapdata will call to dead or alive turn function based on
    if an object is in a "dead" array or an "alive" mapset

    public void newDeadTurn() {
        if (!dead) {
            throw new RuntimeException("undoDeadStep called whilst not dead");
        }
        turnsSinceDied += 1;
    }

    public void makeDeadStep() {
        if (!dead) {
            throw new RuntimeException("undoDeadStep called whilst not dead");
        }
        stepSinceDied += 1;
    }


    public void undoDeadStep() {
        if (!dead) {
            throw new RuntimeException("undoDeadStep called whilst not dead");
        }
        if (turnsSinceDied == 0) {
            stepSinceDied -= 1;
            if (stepSinceDied == 0) {
                dead = false;
            }
        }
    }

    public void undoDeadTurn() {
        if (!dead) {
            throw new RuntimeException("undoDeadTurn called whilst not dead");
        }
        turnsSinceDied -= 1;
        if (turnsSinceDied == 0) {
            dead = false;
        }
    }*/
}
