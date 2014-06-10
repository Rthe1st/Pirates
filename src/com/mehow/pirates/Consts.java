package com.mehow.pirates;

import com.mehow.pirates.gameObjects.GameObject;
import com.mehow.pirates.gameObjects.Mine;
import com.mehow.pirates.gameObjects.Ship;
import com.mehow.pirates.gameObjects.Tile;
import com.mehow.pirates.gameObjects.enemys.Enemy;

public class Consts {
	public final static int  mapInfoTutIndex = 6;
	public final static int mapInfoTutSlidesNumIndex = 7;
	
	//note, when time, make a "meta Game object class", which can be used to generate all this crap
	//from a gameobject file, reducing touch points when adding new objects
	public static enum GameObjectSuperTypes{
		ENEMY, SHIP, TILE, MINE
	}
	public static enum GameObjectSubTypes{
		VENEMY, HENEMY, AENEMY,ROCK, SEA, GOAL,SHIP,MINE
	}
	/*public static final Class<?>[] gameObjectSuper = new Class<?>[]{
		Enemy.class, Ship.class, Mine.class, Tile.class
	};*/

}
