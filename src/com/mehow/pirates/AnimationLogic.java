package com.mehow.pirates;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;

import com.mehow.pirates.gameObjects.InterStep;

public class AnimationLogic {

	private final int numberOfStages = 5;
	private int offsetNo = 0;
	private int interStepNo = 0;
	private static int animationSpeed = 100;

	public static interface AnimationViewCallbacks {
		public void invalidate();
	}

	public static interface LogicCallbacks {
		// this is a delayed callback
		// as no game mechanic work should be done whilst drawing, action this
		// performs
		// should be those which could of been done before animation happended,
		// but didnt because we wanted user to view aniamtion
		public void animationFinished();

		public boolean checkMoreMoves(int stepNumber);
	};

	private AnimationViewCallbacks animationViewCallbacks;
	private LogicCallbacks logicCallbacks;

	public AnimationLogic(AnimationViewCallbacks tAnimationView,
			LogicCallbacks tLogicCallbacks) {
		animationViewCallbacks = tAnimationView;
		logicCallbacks = tLogicCallbacks;
	}

	public int getCurrentAnimationInterStepNo() {
		return interStepNo;
	}

	public int getCurrentAnimationOffsetNo() {
		return offsetNo;
	}

	public int getNumberOfStages() {
		return numberOfStages;
	}

	private boolean moreMoves;

	private Handler animationHandle = new Handler();

	public void updateScreen(boolean animate) {
		// animate parameter is just to optimise
		if (animate == false) {
			animationViewCallbacks.invalidate();
		} else {
			interStepNo = 0;
			offsetNo = 0;
			animationLoop();
		}
	}

	private void animationLoop() {
		System.out.println("animation stage: " + offsetNo + " numOfStages: "
				+ numberOfStages);
		if (offsetNo != numberOfStages) {
			invalidateWrapper();
		} else {
			nextStep();
		}
	}

	private void invalidateWrapper() {
		animationViewCallbacks.invalidate();
		offsetNo += 1;
		animationHandle.postDelayed(new Runnable() {

			@Override
			public void run() {
				animationLoop();
			}

		}, animationSpeed);
	}

	private void nextStep() {
		interStepNo += 1;
		offsetNo = 0;
		moreMoves = logicCallbacks.checkMoreMoves(interStepNo);
		if (moreMoves) {
			animationLoop();
		} else {
			logicCallbacks.animationFinished();
		}
	}

	public static float calculateCanvasOffset(int startPoint, int endPoint,
			float animationOffset, float tileDimension) {
		if (startPoint > endPoint) {
			// did not know you could assign params
			// WHAT A GREAT IDEA
			animationOffset = animationOffset * -1;
		} else if (startPoint == endPoint) {
			animationOffset = 0;
		}
		// System.out.println("startPoint: "+startPoint+" tileDim:"+tileDimension+" animationOffset: "+animationOffset);
		return (startPoint * tileDimension) + animationOffset;
	}

	public static float calculateCanvasOffset(int startPoint, int endPoint,
			float tileDimension) {
		// System.out.println("startPoint: "+startPoint+" tileDim:"+tileDimension+" animationOffset: "+animationOffset);
		return (startPoint * tileDimension);
	}
}
