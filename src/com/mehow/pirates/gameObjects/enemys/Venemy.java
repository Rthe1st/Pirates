package com.mehow.pirates.gameObjects.enemys;


import java.io.Serializable;
import java.util.HashMap;

import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;

import com.mehow.pirates.Cords;
import com.mehow.pirates.R;
import com.mehow.pirates.animation.AnimationSequence;
import com.mehow.pirates.gameObjects.enemys.Enemy.AnimationType;

public class Venemy extends Enemy implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//move horizontaly before vert
	protected static int defNumOfMovesAllowed = 2;

	public static final String ENCODE_VALUE = "4";
	
	public Venemy(Cords cords, int tempNumOfMovesAllowed, Callbacks tCallbacks) {
		super(cords, tempNumOfMovesAllowed, tCallbacks);
		loadAnimations();
		currentAnimation = animations.get(AnimationType.STATIONARY);
	}
	public Venemy(Cords cords, Callbacks tCallbacks) {
		this(cords, defNumOfMovesAllowed, tCallbacks);
	}

	public Cords computeMoveStep(Cords shipCords){
		Cords oldCords = currentCords;//turnRecords.getLatestCords();
		Cords newCords;
		//really dum lol make me smart please im alive i have rights
		//add log for landing on ship
		newCords = attemptYmove(oldCords, shipCords.y);
		return newCords;
	}
    
    //------------
    //ANIMATION
    //------------
 	
    private static HashMap<AnimationType, AnimationDrawable> animationDrawables;
    
    public static void loadAnimationDrawables(Resources resources){
    	animationDrawables = new HashMap<AnimationType, AnimationDrawable>();
    	animationDrawables.put(AnimationType.STATIONARY, (AnimationDrawable)resources.getDrawable(R.drawable.venemy_stationary));
    	animationDrawables.put(AnimationType.FROZEN, (AnimationDrawable)resources.getDrawable(R.drawable.venemy_frozen));
    }
    
    private void loadAnimations(){
    	animations = new HashMap<AnimationType, AnimationSequence>();
    	animations.put(AnimationType.STATIONARY, new AnimationSequence(animationDrawables.get(AnimationType.STATIONARY)));
    	animations.put(AnimationType.FROZEN, new AnimationSequence(animationDrawables.get(AnimationType.FROZEN)));
    }
}
