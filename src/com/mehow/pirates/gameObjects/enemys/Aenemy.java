package com.mehow.pirates.gameObjects.enemys;

import java.io.Serializable;
import java.util.HashMap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;

import com.mehow.pirates.Cords;
import com.mehow.pirates.R;
import com.mehow.pirates.animation.AnimationSequence;
import com.mehow.pirates.gameObjects.Sea.AnimationType;

public class Aenemy extends Enemy implements Serializable{
	protected static int defNumOfMovesAllowed = 1;

	public static final String ENCODE_VALUE = "7";
	
	//move horizontaly before vert
	public Aenemy(Cords cords, int tempNumOfMovesAllowed, Callbacks tCallbacks) {
		super(cords,tempNumOfMovesAllowed, tCallbacks);
		loadAnimations();
		currentAnimation = animations.get(AnimationType.STATIONARY);
	}
	public Aenemy(Cords cords, Callbacks tCallbacks) {
		this(cords, defNumOfMovesAllowed, tCallbacks);
	}

	//moves x and y, chooses axis based on displacement
	public Cords computeMoveStep(Cords shipCords){
        Cords oldCords = currentCords;//turnRecords.getLatestCords();
        Cords newCords;
		int yDisplacement = Math.abs(shipCords.y-oldCords.y);
		int xDisplacement = Math.abs(shipCords.x-oldCords.x);
		if(yDisplacement > xDisplacement){
			newCords = attemptYmove(oldCords, shipCords.y);
			if(oldCords.equals(newCords)){
				newCords = attemptXmove(oldCords, shipCords.x);
			}
		}else{
			newCords = attemptXmove(oldCords, shipCords.x);
			if(oldCords.equals(newCords)){
				newCords = attemptYmove(oldCords, shipCords.y);
			}		
		}
		return newCords;
	}

    //------------
    //ANIMATION
    //------------
 	
    private static HashMap<AnimationType, AnimationDrawable> animationDrawables;
    
    public static void loadAnimationDrawables(Resources resources){
    	animationDrawables = new HashMap<AnimationType, AnimationDrawable>();
    	animationDrawables.put(AnimationType.STATIONARY, (AnimationDrawable)resources.getDrawable(R.drawable.aenemy_stationary));
    	animationDrawables.put(AnimationType.FROZEN, (AnimationDrawable)resources.getDrawable(R.drawable.aenemy_frozen));
    }
    
    private void loadAnimations(){
    	animations = new HashMap<AnimationType, AnimationSequence>();
    	animations.put(AnimationType.STATIONARY, new AnimationSequence(animationDrawables.get(AnimationType.STATIONARY)));
    	animations.put(AnimationType.FROZEN, new AnimationSequence(animationDrawables.get(AnimationType.FROZEN)));
    }
}
