package com.mehow.pirates.animation;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class AnimationSequence {
	//static, because for example all enemies will have the same move frames
	//only time and current frame need be object based
	protected AnimationDrawable animation;
	int currentFrameIndex;
	protected long leftOverTime;
	boolean loop;
	
	public AnimationSequence(AnimationDrawable animationDrawable){
		this.animation = animationDrawable;
		 currentFrameIndex = 0;
		 loop = true;
		 leftOverTime = 0;
	}

	public AnimationSequence(AnimationDrawable animationDrawable, boolean loop){
		this.animation = animationDrawable;
		 currentFrameIndex = 0;
		this.loop = loop;
		 leftOverTime = 0;
	}
	
	public void update(long timeChange){
		leftOverTime += timeChange;
		while(leftOverTime > animation.getDuration(currentFrameIndex) && (loop || currentFrameIndex+1 < animation.getNumberOfFrames())){
			Log.i("AnimationSequence", "changing frame from"+currentFrameIndex+" to "+currentFrameIndex +1);
			leftOverTime -= animation.getDuration(currentFrameIndex);
			currentFrameIndex += 1;
			if(currentFrameIndex == animation.getNumberOfFrames()){
				currentFrameIndex = 0;
			}
		}
	}
	
	public Drawable getCurrentFrame(){
		return animation.getFrame(currentFrameIndex);
	}
	//only for debug
	public int getIndex(){return currentFrameIndex;};
	public void reset(){
		leftOverTime = 0;
		currentFrameIndex = 0;
	}
}
