package com.mehow.pirates;

import android.os.Handler;

public class AnimationLogic {
	
	private final int numberOfStages = 5;
	private int offsetNo = 0;
	private int interStepNo = 0;
	private static int animationSpeed = 100;
	
	public static interface AnimationView{
		public void invalidate ();
	}
	
	public static interface GameLogicCallbacks {
		public void animationFinished();
		
		public boolean checkMoreMoves(int stepNumber);
	};
	
	private AnimationView animationView;
	private GameLogicCallbacks gameLogicCallbacks;
	
	public AnimationLogic(AnimationView tAnimationView, GameLogicCallbacks tGameLogicCallbacks){
		animationView = tAnimationView;
		gameLogicCallbacks = tGameLogicCallbacks;
	}
	
	public int getCurrentAnimationInterStepNo(){
		return interStepNo;
	}
	
	public int getCurrentAnimationOffsetNo(){
		return offsetNo;
	}
	
	public int getNumberOfStages(){
		return numberOfStages;
	}
	
	private boolean moreMoves;
	
	private Handler animationHandle = new Handler();
	
	public void updateScreen(boolean animate){
		//animate parameter is just to optimise
		if(animate == false){
			animationView.invalidate();
		}else{
			interStepNo = 0;
			offsetNo = 0;
			animationLoop();
		}
	}
	
	private void animationLoop() {
		System.out.println("animation stage: " + offsetNo+ " numOfStages: " + numberOfStages);
		if (offsetNo != numberOfStages) {
			invalidateWrapper();
		} else {
			nextStep();
		}
	}

	private void invalidateWrapper(){
		animationView.invalidate();
		offsetNo += 1;
		animationHandle.postDelayed(new Runnable(){

			@Override
			public void run() {
				animationLoop();
			}
			
		}, animationSpeed);
	}
	
	private void nextStep() {
		interStepNo += 1;
		offsetNo = 0;
		moreMoves = gameLogicCallbacks.checkMoreMoves(interStepNo);
		if (moreMoves) {
			animationLoop();
		} else {
			gameLogicCallbacks.animationFinished();
		}
	}
}
