package com.mehow.pirates.gameObjects;

import java.io.Serializable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.mehow.pirates.AnimationLogic;
import com.mehow.pirates.Cords;
import com.mehow.pirates.R;
import com.mehow.pirates.level.GameLogic.GameStates;

public class Mine implements GameObject, Serializable{

	private static Paint minePaint;
	
	private Paint selfPaint;
	
	private Ship creator;
	
	Cords currentCords;
	
	public Mine(Cords startCords){
		currentCords = startCords;
		selfPaint = Mine.minePaint;
	}
	
	public Mine(Cords startCords, Ship ship){
		currentCords = startCords;
		creator = ship;
		selfPaint = Mine.minePaint;
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
//		minePaint.setARGB(0, 0, 100, 0);
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
		return selfPaint;
	}

	@Override
	public boolean exists() {
		return currentCords == null;
	}
	
    public void drawSelfNoAnimate(Canvas canvas, RectF drawArea) {
    	InterStep currentStep = new InterStep(currentCords,currentCords);
    	float xOffset = AnimationLogic.calculateCanvasOffset(currentStep.startCords.x, currentStep.endCords.x, 0, drawArea.width());
    	float yOffset = AnimationLogic.calculateCanvasOffset(currentStep.startCords.y, currentStep.endCords.y, 0, drawArea.height());
    	drawArea.offsetTo(xOffset, yOffset);
        canvas.drawBitmap(getSelf(), null, drawArea, getSelfPaint());
    }

	@Override
	public void setSelfPaint(Paint newPaint) {
		selfPaint = newPaint;
	}
	
	@Override
	public String getEncodedParameters(){
		return "";
	}
}
