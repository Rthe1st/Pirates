package com.mehow.pirates.gameObjects;

import java.io.Serializable;
import java.util.HashMap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;

import com.mehow.pirates.AnimationLogic;
import com.mehow.pirates.Cords;
import com.mehow.pirates.R;
import com.mehow.pirates.animation.AnimationSequence;
import com.mehow.pirates.gameObjects.Sea.AnimationType;
import com.mehow.pirates.level.GameLogic.GameStates;

public class Mine implements GameObject, Serializable{

	private static Paint minePaint;
	
	private Paint selfPaint;
	
	private Ship creator;
	
	Cords currentCords;
	
	public Mine(Cords startCords){
		selfPaint = Mine.minePaint;
		loadAnimations();
		currentAnimation = animations.get(AnimationType.STATIONARY);
		currentCords = startCords;
	}
	
	public Mine(Cords startCords, Ship ship){
		this(startCords);
		creator = ship;
	}
		
	@Override
    public void noLongerExists(){
    	//turnRecords.undoStep();
    	if(creator != null){
    		creator.undoLayMine();
    	}
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
	
	@Override
	public String getEncodedParameters(){
		return "";
	}
	
    public void update(long timeChange){
		currentAnimation.update(timeChange);
    }
    
    //------------
    //ANIMATION
    //------------
 	
    private static HashMap<AnimationType, AnimationDrawable> animationDrawables;
    protected AnimationSequence currentAnimation;
    protected HashMap<AnimationType, AnimationSequence> animations;
 	
    public static enum AnimationType{
    	STATIONARY
    };
    
    public static void loadAnimationDrawables(Resources resources){
    	animationDrawables = new HashMap<AnimationType, AnimationDrawable>();
    	animationDrawables.put(AnimationType.STATIONARY, (AnimationDrawable)resources.getDrawable(R.drawable.mine_stationary));
    }
    
    private void loadAnimations(){
    	animations = new HashMap<AnimationType, AnimationSequence>();
    	animations.put(AnimationType.STATIONARY, new AnimationSequence(animationDrawables.get(AnimationType.STATIONARY)));
    }
    
    public void setAnimationType(AnimationType newType){
    	currentAnimation.reset();
    	currentAnimation = animations.get(newType);
    }
    
    /*drawing*/
    public void drawSelfNoAnimate(Canvas canvas, RectF drawArea) {
    	InterStep currentStep = new InterStep(currentCords,currentCords);
    	float xOffset = AnimationLogic.calculateCanvasOffset(currentStep.startCords.x, currentStep.endCords.x, 0, drawArea.width());
    	float yOffset = AnimationLogic.calculateCanvasOffset(currentStep.startCords.y, currentStep.endCords.y, 0, drawArea.height());
    	drawArea.offsetTo(xOffset, yOffset);
    	Drawable drawable = currentAnimation.getCurrentFrame();
    	drawable.setBounds(new Rect((int)drawArea.left, (int)drawArea.top, (int)drawArea.right, (int)drawArea.bottom));
    	drawable.draw(canvas);
    	//canvas.drawRect(drawArea, getSelfPaint());
    }

	@Override
	public void setSelfPaint(Paint newPaint) {
		selfPaint = newPaint;
	}
}
