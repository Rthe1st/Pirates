package com.mehow.pirates.gameObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.mehow.pirates.AnimationLogic;
import com.mehow.pirates.Cords;
import com.mehow.pirates.Moves;

public class MovementGameObjectMap<T extends Moves> extends GameObjectMap<T> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	MovementGameObjectMap(){
		goMap = new TreeMap<Cords, T>();
		deadGos = new ArrayList<T>();
	}

	MovementGameObjectMap(TreeMap<Cords, T> tGoMap){
		goMap = tGoMap;
		deadGos = new ArrayList<T>();
	}
	
	public void newTurn(){
		for(T go : goMap.values()){
			go.newTurn();
		}
	}
	
	public void undoMove(Cords startCords, Cords endCords, boolean atTurnStart){
		if (startCords == null) {
			noLongerExists(endCords);
		} else if (endCords == null) {
			reviveLast(startCords);
		} else {
			updateMap(endCords, startCords);
			if (atTurnStart) {
				goMap.get(startCords).undoTurn(startCords);
			} else {
				goMap.get(startCords).undoStep(startCords);
			}
		}
	}
	
	public void place(Cords endCords, T go){
		goMap.put(endCords, go);
		goMap.get(endCords).makeStep(endCords);
	}
	
	public void makeMove(Cords startCords, Cords endCords){
		updateMap(startCords, endCords);
		goMap.get(endCords).makeStep(endCords);
	}
	//--------------------------
	//Animation
	//--------------------------
	public boolean checkMoreMoves(int interStepNumber) {
		for (T go : goMap.values()) {
			if (go.hasMoreSteps(interStepNumber)) {
				return true;
			}
		}
		return false;
	}
	public void drawSelves(Canvas canvas, int interStepNo, float offsetAmount, RectF drawArea){
		for(T go : goMap.values()){
			AnimationLogic.drawSelf(canvas, go.getCurrentInterStep(interStepNo), offsetAmount, drawArea, go.getSelf(), go.getSelfPaint());
		}
	}
}
