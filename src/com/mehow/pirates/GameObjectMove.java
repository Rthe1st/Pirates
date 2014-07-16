package com.mehow.pirates;

import java.io.Serializable;

import com.mehow.pirates.gameObjects.GameObject;
import com.mehow.pirates.gameObjects.GameObjectMap;

public class GameObjectMove <T extends GameObject> implements Serializable{

	public final GameObjectMap<T> gameObjectMap;
	private Cords startCords;
	public final Cords endCords;
	public final T go;
	public GameObjectMove(GameObjectMap<T> tGameObjectMap, T tGo, Cords tStartCords, Cords tEndCords){
		gameObjectMap = tGameObjectMap;
		startCords = tStartCords;
		endCords = tEndCords;
		go = tGo;
	}
	
	public void setStartCords(Cords tStartCords){
		startCords = tStartCords;
	}
	
	public Cords getStartCords(){
		return startCords;
	}
	
}
