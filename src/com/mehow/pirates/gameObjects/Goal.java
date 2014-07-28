package com.mehow.pirates.gameObjects;

import java.io.Serializable;
import java.util.HashMap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;

import com.mehow.pirates.Cords;
import com.mehow.pirates.R;
import com.mehow.pirates.animation.AnimationSequence;
import com.mehow.pirates.gameObjects.Rock.AnimationType;

public class Goal extends Tile implements Serializable{
	
	public static final String ENCODE_VALUE = "5";
	
	public Goal(Cords cords){
		super(cords);
		loadAnimations();
		currentAnimation = animations.get(AnimationType.STATIONARY);
	}
	
	//this is deleberatly not limited to 1, partly because a check would require a search of treemap
	//but also cause multipel goals could be fun
	public static boolean isValidMove(CordData cordData){
		if(cordData.enemy == null && cordData.ship==null){
			return true;
		}else{
			return false;
		}

	}
	
	//------------
	   //ANIMATION
	   //------------
		
	   private static HashMap<AnimationType, AnimationDrawable> animationDrawables;
	   protected HashMap<AnimationType, AnimationSequence> animations;
		
	   public static enum AnimationType{
	   	STATIONARY
	   };
	   
	   public static void loadAnimationDrawables(Resources resources){
	   	animationDrawables = new HashMap<AnimationType, AnimationDrawable>();
	   	animationDrawables.put(AnimationType.STATIONARY, (AnimationDrawable)resources.getDrawable(R.drawable.goal_stationary));
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
