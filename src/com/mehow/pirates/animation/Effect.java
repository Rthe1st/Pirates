package com.mehow.pirates.animation;

import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;

import com.mehow.pirates.R;

public abstract class Effect extends AnimationSequence{

	public Effect(AnimationDrawable animationDrawable, boolean loop) {
		super(animationDrawable, loop);
	}

	public abstract boolean isFinished();
	
	public abstract int getX();
	
	public abstract int getY();
}
