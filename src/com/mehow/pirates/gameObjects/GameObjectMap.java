package com.mehow.pirates.gameObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.mehow.pirates.Cords;

public class GameObjectMap<T extends GameObject>  implements Serializable{
	
	/**
	 * this isnt needed because serializable is only used for lifecycle storage
	 */
	private static final long serialVersionUID = -913317125473723635L;

	private TreeMap<Cords, T> goMap;
	
	// lists for dead things, this needs improving as well
	public ArrayList<T> deadGos;
	
	public GameObjectMap(){
		goMap = new TreeMap<Cords, T>();
		deadGos = new ArrayList<T>();
	}
	public GameObjectMap(TreeMap<Cords, T> tGoMap){
		goMap = tGoMap;
		deadGos = new ArrayList<T>();
	}
	
	public T get(Cords cords){
		return goMap.get(cords);
	}
	
	public int getLivingCount(){
		return goMap.size();
	}
	public boolean containsAt(Cords cords){
		return goMap.containsKey(cords);
	}
	public void newTurn(){
		for(T go : goMap.values()){
			go.newTurn();
		}
	}
	public void put(Cords cords, T go){
		goMap.put(cords, go);
	}
	public void undoTurn(){
		for (T mapObject : new ArrayList<T>(goMap.values())) {
			Cords currentCords = mapObject.getLatestCords();
			mapObject.undoTurn();
			if (mapObject.exists() == false) {
				goMap.remove(currentCords);
			}else{
				Cords newCords = mapObject.getLatestCords();
				updateMap(currentCords, newCords);
			}
		}
	}
	
	public void undoStep(){
		for (T mapObject : new ArrayList<T>(goMap.values())) {
			Cords currentCords = mapObject.getLatestCords();
			mapObject.undoStep();
			if (mapObject.exists() == false) {
				goMap.remove(currentCords);
			}else{
				Cords newCords = mapObject.getLatestCords();
				updateMap(currentCords, newCords);
			}
		}
	}

	public void kill(Cords cords){
		deadGos.add(goMap.remove(cords));
	}
	
	public void remove(Cords cords){
		goMap.remove(cords);
	}
	
	public void makeStep(Cords oldCords, Cords newCords){
		goMap.get(oldCords).makeStep(newCords);
		updateMap(oldCords, newCords);
	}
	
	private void updateMap(Cords oldCords, Cords newCords){
		T gameObject = goMap.remove(oldCords);
		goMap.put(newCords, gameObject);
	}
	//avoid using this, direct access to go's is dangerous
	//needed by, for example, enemy movement algorithm
	public Collection<T> getAll(){
		return goMap.values();
	}
	
	//--------------------------
	//Animation
	//--------------------------
	public boolean checkMoreMoves(int interStepNumber) {
		for (GameObject go : goMap.values()) {
			if (go.hasMoreSteps(interStepNumber)) {
				return true;
			}
		}
		return false;
	}
	public void drawSelves(Canvas canvas, int interStepNo, float offsetAmount, RectF drawArea){
		for(T go : goMap.values()){
			go.drawSelf(canvas, interStepNo, offsetAmount, drawArea);
		}
	}
	public void drawSelvesNoAnimate(Canvas canvas, RectF drawArea){
		for(T go : goMap.values()){
			go.drawSelfNoAnimate(canvas, drawArea);
		}
	}
}