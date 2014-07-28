package com.mehow.pirates;

import java.io.Serializable;
import java.util.Vector;

import android.util.Log;

import com.mehow.pirates.gameObjects.InterStep;

public class AnimationSteps implements Serializable{

	int stepCount = 0;
    //get rid of stepcords
    //instead, have a tracker in mapdata
    //that tracks the upperlimit of animated steps
    //then you can add steps
    //then for animation, tell it to loop from the last aniamted step?
    Vector<Cords> stepCords;
    
	public AnimationSteps(Cords startCords){
		stepCount = 0;
        stepCords = new Vector<Cords>();
        stepCords.add(startCords);
	}

    public void makeStep(Cords nextCords){
    	stepCount += 1;
    	Log.i("GameObject","makestep stepcount: "+stepCount);
    	stepCords.add(nextCords);
    }
    public void undoStep(){
    	stepCount -= 1;
    	stepCords.remove(stepCords.size()-1);
    }
    
    public void clearSteps(Cords newStart){
    	stepCount = 0;
    	stepCords.clear();
    	stepCords.add(newStart);
    }
    
    private int getStepCount(){
        return stepCords.size();
    }
    public InterStep getCurrentTurnInterStep(int interStepNo){
    	//-1 for 0 based step array
    	//-1 because number of inter-steps is 1 less then number of steps
    	if(this.hasMoreSteps(interStepNo)){
        	return new InterStep(stepCords.get(interStepNo), stepCords.get(interStepNo+1));
    	}else{
    		Cords cords = stepCords.get(stepCords.size()-1);
        	return new InterStep(cords, cords);
    	}
    }
    
    //this is only for animating, use canMakeMove or similar for gamelogic
    public boolean hasMoreSteps(int interStepNumber){
    	//-1 for 0 based step array
    	//-1 because number of inter-steps is 1 less then number of steps
    	//Log.i("GameObject", "Step count-2: "+(getStepCount()-2)+" inter step no: "+interStepNumber);
    	return getStepCount()-2 >= interStepNumber;
    }
}
