package com.mehow.pirates;

import com.mehow.pirates.animation.ExplosionSequence;
import com.mehow.pirates.gameObjects.Goal;
import com.mehow.pirates.gameObjects.Mine;
import com.mehow.pirates.gameObjects.Rock;
import com.mehow.pirates.gameObjects.Sea;
import com.mehow.pirates.gameObjects.Ship;
import com.mehow.pirates.gameObjects.Tile;
import com.mehow.pirates.gameObjects.enemys.Aenemy;
import com.mehow.pirates.gameObjects.enemys.Henemy;
import com.mehow.pirates.gameObjects.enemys.PathEnemy;
import com.mehow.pirates.gameObjects.enemys.Venemy;

import android.content.res.Resources;
import android.graphics.Paint;


public class Consts {
	public final static int  mapInfoTutIndex = 6;
	public final static int mapInfoTutSlidesNumIndex = 7;
	
	public static final int MINE_FREEZE_TIME = 4;
	
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

	public static void loadAnimations(Resources resources) {
		Consts.loadPaints();
		Ship.loadPaints();
		Ship.loadAnimationDrawables(resources);
		Rock.loadAnimationDrawables(resources);
		Sea.loadAnimationDrawables(resources);
		Mine.loadAnimationDrawables(resources);
		Tile.loadPaints(resources);
		Goal.loadAnimationDrawables(resources);
		Aenemy.loadAnimationDrawables(resources);
		Venemy.loadAnimationDrawables(resources);
		Henemy.loadAnimationDrawables(resources);
		PathEnemy.loadAnimationDrawables(resources);
		PathEnemy.loadSpecialBitmaps(resources);
		ExplosionSequence.loadDrawable(resources);
	}
	
}
