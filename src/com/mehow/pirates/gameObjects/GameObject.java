package com.mehow.pirates.gameObjects;

import java.io.Serializable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;

import com.mehow.pirates.Cords;
import com.mehow.pirates.TurnRecords;

abstract public class GameObject implements Serializable{
	
	//global paints
	protected static Paint stdPaint;
	private static Paint disabledPaint;
	private static Paint errorPaint;
	
    protected final TurnRecords turnRecords;
    
	public GameObject(Cords startCords){
        turnRecords = new TurnRecords(startCords);
	}

    public void newTurn(){
        turnRecords.newTurn();
    }
    public void makeStep(Cords newCords){
    	//to satisfy interface, outside of generics, use makeStep(), mines don't move
        turnRecords.makeStep(newCords);//mines dont move bitch
    }
    public void makeStep(){
        turnRecords.makeStep(turnRecords.getLatestCords());//mines dont move bitch
    }
    public void undoStep(){
    	turnRecords.undoStep();
    }
    public Cords getLatestCords(){
    	return turnRecords.getLatestCords();
    }

    public int getStepCount(){
        return turnRecords.getStepCount();
    }
    
    public InterStep getCurrentTurnInterStep(int interStepNo){
        return turnRecords.getCurrentTurnInterStep(interStepNo);
    }
    public void undoTurn(){
        turnRecords.undoTurn();
    }

    public boolean exists(){
        return turnRecords.exists();
    }
    
    public Cords getCurrentStepCords(){
        return turnRecords.getLatestCords();
    }
    
    //-----------------
    //ANIMATION
    //----------------

    public static void loadBitmapsAndPaints(Resources r){
    	stdPaint = new Paint();
    	disabledPaint = new Paint();
    	errorPaint = new Paint();
    	disabledPaint.setARGB(175, 100, 100, 100);
		errorPaint.setARGB(100, 100, 0, 100);
    }
    
    protected abstract Bitmap getSelf();
    //protected abstract Paint getSelfPaint();
    protected Paint getSelfPaint(){
    	return stdPaint;
    }
    
    //would do if statics were allowed, put in an interface yo
    //abstract static void loadSpecialBitmaps(Resources r);
    
    //this is only for animating, use canMakeMove or similar for gamelogic
    protected boolean hasMoreSteps(int interStepNumber){
    	//-1 for 0 based step array
    	//-1 because number of inter-steps is 1 less then number of steps
    	return getStepCount()-2 >= interStepNumber;
    }
    
    public void drawSelf(Canvas canvas, int interStepNo, float animationOffset, RectF drawArea) {
    	InterStep currentStep;
    	//-1 for 0 based step array
    	//-1 because number of inter-steps is 1 less then number of steps
    	if(!this.hasMoreSteps(interStepNo)){
    		currentStep = new InterStep(getLatestCords(),getLatestCords());
    	}else{
    		currentStep = getCurrentTurnInterStep(interStepNo);
    	}
    	float xOffset = calculateCanvasOffset(currentStep.startCords.x, currentStep.endCords.x, animationOffset, drawArea.width());
    	float yOffset = calculateCanvasOffset(currentStep.startCords.y, currentStep.endCords.y, animationOffset, drawArea.height());
    	//check this offsets in the right direction
    	drawArea.offsetTo(xOffset, yOffset);
        canvas.drawBitmap(getSelf(), null, drawArea, getSelfPaint());
    }
    public void drawSelfNoAnimate(Canvas canvas, RectF drawArea) {
    	InterStep currentStep = new InterStep(turnRecords.getLatestCords(),turnRecords.getLatestCords());
    	float xOffset = calculateCanvasOffset(currentStep.startCords.x, currentStep.endCords.x, 0, drawArea.width());
    	float yOffset = calculateCanvasOffset(currentStep.startCords.y, currentStep.endCords.y, 0, drawArea.height());
    	//check this offsets in the right direction
    	System.out.println("offset x: "+xOffset+" yoffset: "+yOffset);
    	drawArea.offsetTo(xOffset, yOffset);
        canvas.drawBitmap(getSelf(), null, drawArea, getSelfPaint());
    }
    //calculate offset
    protected static float calculateCanvasOffset(int startPoint, int endPoint, float animationOffset, float tileDimension){
		if (startPoint > endPoint) {
			//did not know you could assign params
			//WHAT A GREAT IDEA
			animationOffset = animationOffset*-1;
		}else if(startPoint == endPoint){
			animationOffset = 0;
		}
		System.out.println("startPoint: "+startPoint+" tileDim:"+tileDimension+" animationOffset: "+animationOffset);
		return (startPoint*tileDimension)+animationOffset;	
    }
    
    /*
    Activity lifecycle Bundling
    */

	public Bundle saveState(){
		Bundle bundle = new Bundle();
		//bundle.putBundle("PAST_TURN_DATA", pastTurnData.saveState());
	//	bundle.putBundle("CUR_MOVES_CORDS", flattenCurCords());
		return bundle;
	}
	public void loadState(Bundle bundle){
		//pastTurnData.loadState(bundle.getBundle("PAST_TURN_DATA"));
		//inflateCurCords(bundle.getBundle("CUR_MOVES_CORDS"));
	}
    
	/*private Bundle flattenCurCords(){
		Bundle bundle = new Bundle();
		Cords cords;
		bundle.putInt("PREV_MOVES_SIZE", currentMoveCords.size());
		for(int i=0; i<currentMoveCords.size();i++){
			cords = currentMoveCords.get(i);
			bundle.putInt("TURN_"+i+"_X", cords.x);
			bundle.putInt("TURN_"+i+"_Y", cords.y);
		}
		return bundle;
	}
	private Bundle inflateCurCords(Bundle bundle){
		int curCordsSize = bundle.getInt("PREV_MOVES_SIZE");
		for(int i=0; i<curCordsSize;i++){
			//currentMoveCords.add(new Cords(bundle.getInt("TURN_"+i+"_X"),bundle.getInt("TURN_"+i+"_Y")));
		}
		return bundle;
	}*/
}
