package com.mehow.pirates.gameObjects.enemys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.mehow.pirates.AnimationLogic;
import com.mehow.pirates.AnimationSteps;
import com.mehow.pirates.Consts;
import com.mehow.pirates.Cords;
import com.mehow.pirates.Moves;
import com.mehow.pirates.animation.AnimationSequence;
import com.mehow.pirates.gameObjects.CordData;
import com.mehow.pirates.gameObjects.GameObject;
import com.mehow.pirates.gameObjects.Goal;
import com.mehow.pirates.gameObjects.InterStep;
import com.mehow.pirates.gameObjects.Sea;
import com.mehow.pirates.level.GameLogic;

abstract public class Enemy implements GameObject, Moves {
	protected static int defNumOfMovesAllowed = 2;
	protected boolean moveLastTurn;// used to stop animation when not moving to
									// dif cords
	protected int numOfMovesAllowed;
	protected int numOfMovesLeft;
	protected ArrayList<Cords> previousCords = new ArrayList<Cords>();

	private Stack<Boolean> frozenRecord;
	
	//0 when not frozen
	protected int frozenTurnCount;
	
	// this is hackish, really should make use of a member var EnemyPathAlgs
	protected final Callbacks callbacks;

	public interface Callbacks {
		public CordData getInfoOnCords(Cords cord);
	}
	
	private Paint selfPaint;
	
	Cords currentCords;

	AnimationSteps animationSteps;

    //animation&display

    public static enum AnimationType{
    	STATIONARY, FROZEN
    };

    protected HashMap<AnimationType, AnimationSequence> animations;    
	protected AnimationSequence currentAnimation;
    
	public Enemy(Cords cords, int tempNumOfMovesAllowed, Callbacks tCallbacks) {
		currentCords = cords;
		animationSteps = new AnimationSteps(cords);
		previousCords = new ArrayList<Cords>(0);
		numOfMovesAllowed = tempNumOfMovesAllowed;
		numOfMovesLeft = numOfMovesAllowed;
		callbacks = tCallbacks;
		selfPaint = Consts.stdPaint;
		frozenTurnCount = 0;
		frozenRecord = new Stack<Boolean>();
	}

	public Enemy(Cords cords, Callbacks tCallbacks) {
		this(cords, defNumOfMovesAllowed, tCallbacks);
	}

	abstract public Cords computeMoveStep(Cords shipCords);

	// --------------------
	// used in child compute step functions
	protected Cords attemptXmove(Cords oldCords, int shipX) {
		if (shipX < oldCords.x
				&& isValidMove(callbacks.getInfoOnCords(new Cords(
						oldCords.x - 1, oldCords.y)))) {
			return new Cords(oldCords.x - 1, oldCords.y);
		} else if (shipX > oldCords.x
				&& isValidMove(callbacks.getInfoOnCords(new Cords(
						oldCords.x + 1, oldCords.y)))) {
			return new Cords(oldCords.x + 1, oldCords.y);
		} else {
			return oldCords;
		}
	}

	protected Cords attemptYmove(Cords oldCords, int shipY) {
		if (shipY < oldCords.y
				&& isValidMove(callbacks.getInfoOnCords(new Cords(oldCords.x,
						oldCords.y - 1)))) {
			return new Cords(oldCords.x, oldCords.y - 1);
		} else if (shipY > oldCords.y
				&& isValidMove(callbacks.getInfoOnCords(new Cords(oldCords.x,
						oldCords.y + 1)))) {
			return new Cords(oldCords.x, oldCords.y + 1);
		} else {
			return oldCords;
		}
	}

	public static boolean isValidMove(CordData cordData) {
		boolean validTile = cordData.tile instanceof Sea
				|| cordData.tile instanceof Goal;
		boolean notOccupied = cordData.enemy == null;
		if (validTile && notOccupied) {
			return true;
		} else {
			return false;
		}
	}

	public int getNumOfMovesAllowed() {
		return numOfMovesAllowed;
	}

	public int getNumOfMovesLeft() {
		// System.out.println("get numofmovesLeft: "+numOfMovesLeft);
		return numOfMovesLeft;
	}

	@Override
	public void newTurn() {
		animationSteps.clearSteps(currentCords);
		resetMovesLeft();
		frozenRecord.push(frozenTurnCount == 0);
		if(frozenTurnCount > 0){
			frozenTurnCount -= 1;
		}
	}

	public boolean canMakeMove() {
		return numOfMovesLeft > 0 && frozenTurnCount == 0;
	}

	@Override
	public void undoStep(Cords newCords) {
		// relies on animationsteps being insync with the global undo, whihc it
		// should be
		// but werid and redundant storing same data in 2 places
		animationSteps.undoStep();
		numOfMovesLeft += 1;
	}

	@Override
	public void undoTurn(Cords newCords) {
		currentCords = newCords;
		animationSteps.clearSteps(currentCords);
		numOfMovesLeft = this.numOfMovesAllowed;
		//note there weridness around freezing because
		//on the turn when an enemy freezes its not frozen at the start
		//but is frozen at the end of the turn
		boolean previousFrozenState = frozenRecord.pop();
		if(previousFrozenState == true){
			frozenTurnCount += 1;
		}
		if(frozenTurnCount == Consts.MINE_FREEZE_TIME){
			frozenTurnCount = 0;
		}
	}

	@Override
	public void makeStep(Cords newCords) {
		if (numOfMovesLeft >= 1) {
			numOfMovesLeft -= 1;
			currentCords = newCords;
			animationSteps.makeStep(currentCords);
		} else {
			throw new RuntimeException("Out of Moves");
		}
	}
	
	public void hitMine(){
		frozenTurnCount = Consts.MINE_FREEZE_TIME;
	}

	@Override
	public void noLongerExists() {
	}

	@Override
	public void kill() {
		currentCords = null;
	}

	@Override
	public void revive(Cords newCords) {
		// clear cords?
		currentCords = newCords;
	}

	public boolean getMoveLastTurn() {
		return moveLastTurn;
	}

	public void resetMovesLeft() {
		numOfMovesLeft = numOfMovesAllowed;
	}

	@Override
	public boolean trySelect(GameLogic.GameStates gameState) {
		return false;
	}

	@Override
	public boolean hasMoreSteps(int interStepNumber) {
		return animationSteps.hasMoreSteps(interStepNumber);
	}

	@Override
	public void selectedDraw(Canvas canvas, RectF drawArea) {

	}

	@Override
	public Cords getCurrentCords() {
		return currentCords;
	}

	public Paint getSelfPaint() {
		return selfPaint;
	}

	@Override
	public boolean exists() {
		return currentCords != null;
	}
	
    @Override
    public InterStep getCurrentInterStep(int interStepNo){
    	return animationSteps.getCurrentTurnInterStep(interStepNo);
    }
    
    @Override
	public String getEncodedParameters(){
		return "";
	}
    
    public void drawSelfNoAnimate(Canvas canvas, RectF drawArea) {
    	InterStep currentStep = new InterStep(currentCords,currentCords);
    	float xOffset = AnimationLogic.calculateCanvasOffset(currentStep.startCords.x, currentStep.endCords.x, 0, drawArea.width());
    	float yOffset = AnimationLogic.calculateCanvasOffset(currentStep.startCords.y, currentStep.endCords.y, 0, drawArea.height());
    	drawArea.offsetTo(xOffset, yOffset);
    	Drawable drawable = currentAnimation.getCurrentFrame();
    	drawable.setBounds(new Rect((int)drawArea.left, (int)drawArea.top, (int)drawArea.right, (int)drawArea.bottom));
    	drawable.draw(canvas);
    	//canvas.drawRect(drawArea, getSelfPaint());
    	drawFrozen(canvas, drawArea);
    }
    
    public void drawSelf(Canvas canvas, int interStepNo, float animationOffset, RectF drawArea) {
    	InterStep currentStep;
    	//-1 for 0 based step array
    	//-1 because number of inter-steps is 1 less then number of steps
    	if(animationSteps.hasMoreSteps(interStepNo)){
    		currentStep = animationSteps.getCurrentTurnInterStep(interStepNo);
    	}else{
    		currentStep = new InterStep(getCurrentCords(),getCurrentCords());
    	}
    	float xOffset = AnimationLogic.calculateCanvasOffset(currentStep.startCords.x, currentStep.endCords.x, animationOffset, drawArea.width());
    	float yOffset = AnimationLogic.calculateCanvasOffset(currentStep.startCords.y, currentStep.endCords.y, animationOffset, drawArea.height());
    	drawArea.offsetTo(xOffset, yOffset);
    	Drawable drawable = currentAnimation.getCurrentFrame();
    	drawable.setBounds(new Rect((int)drawArea.left, (int)drawArea.top, (int)drawArea.right, (int)drawArea.bottom));
    	drawable.draw(canvas);
    	//canvas.drawRect(drawArea, getSelfPaint());
        drawFrozen(canvas, drawArea);
    }
    
    private void drawFrozen(Canvas canvas, RectF drawArea){
        if(this.frozenTurnCount != 0){
        	Paint frozenPaint = new Paint();
        	frozenPaint.setARGB(100, 0, 0, 255);
        	canvas.drawArc(drawArea, 0, 90*frozenTurnCount, true, frozenPaint);
        }
    }
    
    public void update(long timeChange){
		currentAnimation.update(timeChange);
    }
    
    public void setAnimationType(AnimationType newType){
    	currentAnimation.reset();
    	currentAnimation = animations.get(newType);
    }
    
    public void setSelfPaint(Paint newPaint){
    	selfPaint  = newPaint;
    }
}
