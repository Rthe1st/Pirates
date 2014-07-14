package com.mehow.pirates;

import android.graphics.Paint;


public class Consts {
	public final static int  mapInfoTutIndex = 6;
	public final static int mapInfoTutSlidesNumIndex = 7;
	
	//note, when time, make a "meta Game object class", which can be used to generate all this crap
	//from a gameobject file, reducing touch points when adding new objects
	public static enum DesignModeSuperTypes{
		ENEMY, SHIP, TILE, MINE, DELETE, SELECT
	}
	public static enum DesignModeSubTypes{
		VENEMY, HENEMY, AENEMY,PATHENEMY,ROCK, SEA, GOAL,SHIP,MINE
	}
	/*public static final Class<?>[] gameObjectSuper = new Class<?>[]{
		Enemy.class, Ship.class, Mine.class, Tile.class
	};*/
	
	//global paints
	public static Paint stdPaint;
	
	public static void loadPaints(){
		stdPaint = new Paint();
	}

}
