package com.mehow.pirates.gameObjects;

import java.io.Serializable;
import java.util.HashMap;

import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;

import com.mehow.pirates.Cords;
import com.mehow.pirates.R;
import com.mehow.pirates.animation.AnimationSequence;

public class Sea extends Tile implements Serializable{

	public static final String ENCODE_VALUE = "0";
	
    public Sea(Cords cords) {
		super(cords);
		loadAnimations();
		currentAnimation = animations.get(AnimationType.STATIONARY);
		Log.i("Sea", "animations size: "+animations.size()+" stationary is null:"+(null==animations.get(AnimationType.STATIONARY)));
	}
  
    public static boolean isValidMove(CordData cordData){
 		if(cordData.tile == null || !cordData.tile.getClass().equals(Sea.class)){
 			return true;
 		}else{
 			return false;
 		}
 	}
    //------------
    //ANIMATION
    //------------
 	
    private static HashMap<AnimationType, AnimationDrawable> animationDrawables;
    //protected AnimationSequence currentAnimation;
    protected HashMap<AnimationType, AnimationSequence> animations;
 	
    public static enum AnimationType{
    	STATIONARY
    };
    
    public static void loadAnimationDrawables(Resources resources){
    	animationDrawables = new HashMap<AnimationType, AnimationDrawable>();
    	animationDrawables.put(AnimationType.STATIONARY, (AnimationDrawable)resources.getDrawable(R.drawable.sea_stationary));
    }
    
    private void loadAnimations(){
    	animations = new HashMap<AnimationType, AnimationSequence>();
    	animations.put(AnimationType.STATIONARY, new AnimationSequence(animationDrawables.get(AnimationType.STATIONARY)));
    }
    
    public void setAnimationType(AnimationType newType){
    	currentAnimation.reset();
    	currentAnimation = animations.get(newType);
    }
}
