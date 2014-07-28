package com.mehow.pirates.animation;

import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;

import com.mehow.pirates.Cords;
import com.mehow.pirates.R;

public class ExplosionSequence extends Effect{
	
	public static AnimationDrawable drawable;
	
	final Cords location;
	
	public static void loadDrawable(Resources r){
		drawable = (AnimationDrawable) r.getDrawable(R.drawable.explosion);
	}
	
	public ExplosionSequence(Cords location){
		super(drawable, false);
		this.location = location;
	}
	
	public boolean isFinished(){
		return currentFrameIndex == animation.getNumberOfFrames()-1
				&& this.leftOverTime > animation.getDuration(currentFrameIndex);
	}
	
	public int getX(){
		return location.x;
	}
	
	public int getY(){
		return location.y;
	}
}
