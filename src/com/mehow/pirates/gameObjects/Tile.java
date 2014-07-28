package com.mehow.pirates.gameObjects;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.mehow.pirates.AnimationLogic;
import com.mehow.pirates.Cords;
import com.mehow.pirates.animation.AnimationSequence;
import com.mehow.pirates.level.GameLogic;

/**
 * Created by User on 01/02/14.
 */

abstract public class Tile implements GameObject{
	
	private Cords currentCords;
	 
	public static Paint standardPaint;
	
	private Paint selfPaint;
	
	
    //animation&display
    protected AnimationSequence currentAnimation;
	
    public Tile(Cords cords){
    	currentCords = cords;
    	selfPaint = Tile.standardPaint;
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
    	return selfPaint;
    }
    
    public static void loadPaints(Resources r){
    	standardPaint = new Paint();
    }
    
    public void drawSelfNoAnimate(Canvas canvas, RectF drawArea) {
    	InterStep currentStep = new InterStep(currentCords,currentCords);
    	float xOffset = AnimationLogic.calculateCanvasOffset(currentStep.startCords.x, currentStep.endCords.x, 0, drawArea.width());
    	float yOffset = AnimationLogic.calculateCanvasOffset(currentStep.startCords.y, currentStep.endCords.y, 0, drawArea.height());
    	drawArea.offsetTo(xOffset, yOffset);
    	Log.i("Tile", "x: "+this.currentCords.x+" y: "+this.currentCords.x+" class"+this.getClass());
    	Drawable drawable = currentAnimation.getCurrentFrame();
    	drawable.setBounds(new Rect((int)drawArea.left, (int)drawArea.top, (int)drawArea.right, (int)drawArea.bottom));
    	drawable.draw(canvas);
    	//canvas.drawRect(drawArea, getSelfPaint());
    }
    
    @Override
    public void setSelfPaint(Paint newPaint){
    	selfPaint = newPaint;
    }
    
    @Override
	public String getEncodedParameters(){
		return "";
	}
    
    public void update(long timeChange){
		currentAnimation.update(timeChange);
    }
}
