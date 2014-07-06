package com.mehow.pirates.gameObjects.enemys;

import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.mehow.pirates.AnimationSteps;
import com.mehow.pirates.Cords;
import com.mehow.pirates.Moves;
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

	protected static Paint stdPaint;
	
	Cords currentCords;

	AnimationSteps animationSteps;

	// this is hackish, really should make use of a member var EnemyPathAlgs
	protected final Callbacks callbacks;

	public interface Callbacks {
		public CordData getInfoOnCords(Cords cord);
	}

	public Enemy(Cords cords, int tempNumOfMovesAllowed, Callbacks tCallbacks) {
		currentCords = cords;
		animationSteps = new AnimationSteps(cords);
		previousCords = new ArrayList<Cords>(0);
		numOfMovesAllowed = tempNumOfMovesAllowed;
		numOfMovesLeft = numOfMovesAllowed;
		callbacks = tCallbacks;
	}

	public Enemy(Cords cords, Callbacks tCallbacks) {
		currentCords = cords;
		animationSteps = new AnimationSteps(cords);
		previousCords = new ArrayList<Cords>(0);
		numOfMovesAllowed = defNumOfMovesAllowed;
		numOfMovesLeft = numOfMovesAllowed;
		callbacks = tCallbacks;
	}
	
	public static void loadPaints(Resources r){
    	stdPaint = new Paint();
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

	// ------------------------------------

	public static boolean isValidMove(CordData cordData) {
		boolean validTile = cordData.tile instanceof Sea
				|| cordData.tile instanceof Goal;
		boolean notOccupied = cordData.enemy == null && cordData.mine == null;
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
	}

	public boolean canMakeMove() {
		return numOfMovesLeft >= 1;
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

	public abstract Bitmap getSelf();

	public Paint getSelfPaint() {
		return stdPaint;
	}

	@Override
	public boolean exists() {
		return currentCords != null;
	}
	
    @Override
    public InterStep getCurrentInterStep(int interStepNo){
    	return animationSteps.getCurrentTurnInterStep(interStepNo);
    }
}
