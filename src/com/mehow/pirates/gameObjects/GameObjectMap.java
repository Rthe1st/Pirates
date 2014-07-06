package com.mehow.pirates.gameObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;

import com.mehow.pirates.AnimationLogic;
import com.mehow.pirates.Cords;

public class GameObjectMap<T extends GameObject>  implements Serializable{
	
	/**
	 * this isnt needed because serializable is only used for lifecycle storage
	 */
	private static final long serialVersionUID = -913317125473723635L;

	protected TreeMap<Cords, T> goMap;
	
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
	public void put(Cords cords, T go){
		goMap.put(cords, go);
	}
	
	public void noLongerExists(Cords cords){
		goMap.get(cords).noLongerExists();
		goMap.remove(cords);
	}
	
	public void kill(Cords cords){
		T go = goMap.remove(cords);
		go.kill();
		deadGos.add(go);
	}
	
	public void reviveLast(Cords cords){
		T go = deadGos.remove(deadGos.size()-1);
		go.revive(cords);
		goMap.put(cords, go);
	}
	
	public void undoMove(Cords startCords, Cords endCords, boolean atTurnStart){
		if (startCords == null) {
			noLongerExists(endCords);
		} else if (endCords == null) {
			reviveLast(startCords);
		} else {
			updateMap(endCords, startCords);
		}
	}
	
	public void place(Cords endCords, T go){
		goMap.put(endCords, go);
	}
	
	public void makeMove(Cords startCords, Cords endCords){
		updateMap(startCords, endCords);
	}
	
	public void updateMap(Cords oldCords, Cords newCords){
		Log.i("MapUpdate","Removing from: "+oldCords+" to "+newCords);
		T gameObject = goMap.remove(oldCords);
		goMap.put(newCords, gameObject);
	}
	//avoid using this, direct access to go's is dangerous
	//needed by, for example, enemy movement algorithm
	public Collection<T> getAll(){
		return goMap.values();
	}
	
	public void drawSelvesNoAnimate(Canvas canvas, RectF drawArea){
		for(T go : goMap.values()){
			AnimationLogic.drawSelfNoAnimate(canvas, drawArea, go.getCurrentCords(),go.getSelf(), go.getSelfPaint());
		}
	}
}