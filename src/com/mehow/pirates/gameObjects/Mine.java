package com.mehow.pirates.gameObjects;

import java.io.Serializable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.mehow.pirates.Cords;
import com.mehow.pirates.R;
import com.mehow.pirates.level.GameLogic.GameStates;

public class Mine implements GameObject, Serializable{

	private static Paint minePaint;
	
	private Ship creator;
	
	Cords currentCords;
	
	public Mine(Cords startCords){
		currentCords = startCords;
	}
	
	public Mine(Cords startCords, Ship ship){
		currentCords = startCords;
		creator = ship;
	}
	
	@Override
    public void noLongerExists(){
    	//turnRecords.undoStep();
    	if(creator != null){
    		creator.undoLayMine();
    	}
    }
	
	private static Bitmap self;

    @Override
    public Bitmap getSelf() {
 	   return self;
    }
    
    public static void loadSpecialBitmaps(Resources r){
	   	self = BitmapFactory.decodeResource(r, R.drawable.mine);
	   	minePaint = new Paint();
		minePaint.setARGB(100, 0, 100, 0);
    }

	@Override
	public boolean trySelect(GameStates gameState) {
		return false;
	}

	@Override
	public void kill() {
		currentCords = null;
	}

	@Override
	public void revive(Cords newCords) {
		currentCords = newCords;
	}

	@Override
	public Cords getCurrentCords() {
		return currentCords;
	}

	@Override
	public void selectedDraw(Canvas canvas, RectF drawArea) {
		
	}

    public static void loadPaints(Resources r){
    	minePaint = new Paint();
    }
	
	@Override
	public Paint getSelfPaint() {
		return minePaint;
	}

	@Override
	public boolean exists() {
		return currentCords == null;
	}
}
