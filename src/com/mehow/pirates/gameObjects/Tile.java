package com.mehow.pirates.gameObjects;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.mehow.pirates.Cords;
import com.mehow.pirates.level.GameLogic;

/**
 * Created by User on 01/02/14.
 */

abstract public class Tile implements GameObject{
	
	Cords currentCords;
	
	public static Paint standardPaint;
	
    public Tile(Cords cords){
    	currentCords = cords;
    }
    
	public boolean trySelect(GameLogic.GameStates gameState) {
		return false;
	}

	public void noLongerExists(){
	}
    public void kill(){
    	currentCords = null;
    }
    public void revive(Cords newCords){
    	currentCords = newCords;
    }
    
    public Cords getCurrentCords(){
    	return currentCords;
    }
    
    //animation
    public void selectedDraw(Canvas canvas, RectF drawArea){
    	
    }
    
    public boolean exists(){
    	return currentCords == null;
    }
    
    public Paint getSelfPaint(){
    	return Tile.standardPaint;
    }
    
    public static void loadPaints(Resources r){
    	standardPaint = new Paint();
    }
    
}
