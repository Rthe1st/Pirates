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
import com.mehow.pirates.gameObjects.Goal.AnimationType;

public class Rock extends Tile implements Serializable{

	public static final String ENCODE_VALUE = "1";
	
	public Rock(Cords cords) {
		super(cords);
		loadAnimations();
		currentAnimation = animations.get(AnimationType.STATIONARY);
	}

   public static boolean isValidMove(CordData cordData){
		if(cordData.enemy==null
				&& cordData.ship==null && (cordData.tile == null || !cordData.tile.getClass().equals(Rock.class))){
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
   	animationDrawables.put(AnimationType.STATIONARY, (AnimationDrawable)resources.getDrawable(R.drawable.rock_stationary));
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