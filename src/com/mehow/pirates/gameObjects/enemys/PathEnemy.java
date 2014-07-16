package com.mehow.pirates.gameObjects.enemys;

import java.io.Serializable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.mehow.pirates.AnimationLogic;
import com.mehow.pirates.Consts;
import com.mehow.pirates.Cords;
import com.mehow.pirates.R;

public class PathEnemy extends Enemy implements Serializable {
	protected static int defNumOfMovesAllowed = 1;

	public static final String ENCODE_VALUE = "3";

	// this need to be kept insync, worth making a class?
	//private Vector<Cords> moveCords;
	//private Vector<Bitmap> moveBitmaps;
	private PathData pathData;

	int moveIndex;
	
	int startIndex;

	// if a path is connected in a loop, walk round it, else, go back and forth
	// improvement: add the option for player to turn the loop on or off?
	private boolean loop;
	private int direction;

	// move horizontaly before vert
	public PathEnemy(Cords cords, int tempNumOfMovesAllowed,
			Callbacks tCallbacks, String parameters) {
		super(cords, tempNumOfMovesAllowed, tCallbacks);
		pathData = new PathData();
		parseParameters(parameters);
		moveIndex = startIndex;
	}

	public PathEnemy(Cords cords, Callbacks tCallbacks, String parameters) {
		super(cords, defNumOfMovesAllowed, tCallbacks);
		pathData = new PathData();		
		parseParameters(parameters);
		moveIndex = startIndex;
	}

	public PathEnemy(Cords cords, Callbacks tCallbacks) {
		super(cords, defNumOfMovesAllowed, tCallbacks);
		pathData = new PathData();
		pathData.addHeadCords(cords);
		startIndex = 0;
		moveIndex = startIndex;
		Log.i("PathEnemy", "pathdata length:" + pathData.getSize());
	}

	// parameters are of the form [(x?y)!(x?y)!...#startIndex]
	private void parseParameters(String parameters) {
		// cut of begining and ending [...]
		parameters = parameters.substring(0, parameters.length()-1);
		int endOfPointsIndex = parameters.indexOf("#");
		startIndex = Integer.parseInt(parameters.substring(endOfPointsIndex+1));
		parameters = parameters.substring(1, endOfPointsIndex);
		Log.i("MapData", "Parameters: " + parameters);
		String[] splitParameters = parameters.split("!");
		for (String parameter : splitParameters) {
			// process (x,y)
			Log.i("PathEnemy", "cord parameter " + parameter);
			int x = Integer.parseInt(parameter.substring(1, 2));
			int y = Integer.parseInt(parameter.substring(3, 4));
			Log.i("PathEnemy", "x: " + x + " y " + y);
			Cords cords = new Cords(x, y);
			pathData.addHeadCords(cords);
		}
		if (pathData.getCords(0).isNextTo(pathData.getCords(pathData.getSize() - 1))) {
			loop = true;
			direction = 1;
		} else {
			direction = 1;
			loop = false;
		}
	}

	@Override
	public String getEncodedParameters() {
		String encodedParameters = "[";
		for(int i=0;i<pathData.getSize();i++){
			Cords cords = pathData.getCords(i);
			encodedParameters += "(" + cords.x + "?" + cords.y + ")!";
		}
		// remove last !
		encodedParameters = encodedParameters.substring(0,
				encodedParameters.length() - 1);
		//store start index
		encodedParameters += "#"+String.valueOf(startIndex);
		encodedParameters += "]";
		return encodedParameters;
	}

	// moves x and y, chooses axis based on displacement
	@Override
	public Cords computeMoveStep(Cords shipCords) {
		Log.i("PathEnemy","compute move step pathenemy loop:"+loop+" moveIndex:"+moveIndex+" dirction:"+direction);
		int newMoveIndex;
		//if theres a loop, the ship will never move backwards, no at 0 check need
		if(loop && moveIndex == pathData.getSize()-1){
			newMoveIndex = 0;
		}else{
			newMoveIndex = moveIndex + direction;
		}
		Cords newCords = pathData.getCords(newMoveIndex);
		if(isValidMove(callbacks.getInfoOnCords(newCords))){
			//flip direction so the next time move is calucated, it goes the right way
			if (!loop && (newMoveIndex == pathData.getSize()-1 || newMoveIndex == 0)) {
				direction *= -1;
			}
			moveIndex = newMoveIndex;
			return newCords;
		}else{
			return currentCords;
		}
	}

	public void changeMoveCords(Cords cords) {
		if (cords.equals(this.currentCords)) {
			return;
		} else if (pathData.contains(cords)) {
			tryDeleteMoveCords(cords);
		} else {
			tryAddMoveCords(cords);
		}
	}

	public boolean tryDeleteMoveCords(Cords cords) {
		if (pathData.getCords(0).equals(cords)) {
			pathData.deleteTailCords();
			startIndex -= 1;
			return true;
		} else if (pathData.getLast().equals(cords)) {
			pathData.deleteHeadCords();
			return true;
		} else {
			return false;
		}
	}

	public boolean tryAddMoveCords(Cords newCords) {
		Cords firstMoveCords = pathData.getFirst();
		Cords lastMoveCords = pathData.getLast();
		// order matter, it seemed more intuitive for players to add the next
		// move to the
		// end rather then start if both option are valid
		if (lastMoveCords.isNextTo(newCords)
				&& !newCords.equals(firstMoveCords)) {
			pathData.addHeadCords(newCords);
			return true;
		} else if (firstMoveCords.isNextTo(newCords)
				&& !newCords.equals(lastMoveCords)) {
			pathData.addTailCords(newCords);
			startIndex += 1;
			return true;
		} else {
			return false;
		}
	}

	// ------------
	// ANIMATION
	// ------------

	private static Bitmap self;

	private static Bitmap frozen_self;

	private static Bitmap path_trace_straight;
	private static Bitmap path_trace_curve;
	private static Bitmap path_trace_endpoint;

	public static void loadSpecialBitmaps(Resources r) {
		self = BitmapFactory.decodeResource(r, R.drawable.pathenemy_ship);
		frozen_self = BitmapFactory.decodeResource(r,
				R.drawable.venemy_ship_frozen);
		path_trace_straight = BitmapFactory.decodeResource(r,
				R.drawable.path_trace_straight);
		path_trace_curve = BitmapFactory.decodeResource(r,
				R.drawable.path_trace_curve);
		path_trace_endpoint = BitmapFactory.decodeResource(r,
				R.drawable.path_trace_endpoint);
	}

	@Override
	public void selectedDraw(Canvas canvas, RectF drawArea) {
		for (int index=0; index<pathData.getSize(); index++) {
			Cords cords = pathData.getCords(index);
			float xOffset = AnimationLogic.calculateCanvasOffset(cords.x,
					cords.x, 0, drawArea.width());
			float yOffset = AnimationLogic.calculateCanvasOffset(cords.y,
					cords.y, 0, drawArea.height());
			drawArea.offsetTo(xOffset, yOffset);
			Paint movePaint = new Paint();
			movePaint.setARGB(100, 0, 255, 0);
			canvas.drawRect(drawArea, movePaint);
		}
	}

	@Override
	public void drawSelfNoAnimate(Canvas canvas, RectF drawArea) {
		drawMovementSquares(canvas, drawArea);
		super.drawSelfNoAnimate(canvas, drawArea);
	}

	@Override
	public void drawSelf(Canvas canvas, int interStepNo, float animationOffset,
			RectF drawArea) {
		drawMovementSquares(canvas, drawArea);
		super.drawSelf(canvas, interStepNo, animationOffset, drawArea);
	}

	private void drawMovementSquares(Canvas canvas, RectF drawArea) {
		for (int index=0; index<pathData.getSize(); index++) {
			Cords cords = pathData.getCords(index);
			float xOffset = AnimationLogic.calculateCanvasOffset(cords.x,
					cords.x, 0, drawArea.width());
			float yOffset = AnimationLogic.calculateCanvasOffset(cords.y,
					cords.y, 0, drawArea.height());
			drawArea.offsetTo(xOffset, yOffset);
			Bitmap pathTrace;
			switch(pathData.getPathType(index)){
			case CURVE:
				pathTrace = path_trace_curve;
				break;
			case END:
				pathTrace = path_trace_endpoint;
				break;
			case STRAIGHT:
				pathTrace = path_trace_straight;
				break;
			default:
				throw new RuntimeException("unknown pathType");
			}
			Matrix rotation = new Matrix();
			//change to postRotate(..) if trying to do more transformations with matrix
			rotation.setRotate(pathData.getRotation(index), pathTrace.getWidth()/2, pathTrace.getHeight()/2);
	        pathTrace = Bitmap.createBitmap(pathTrace, 0, 0, pathTrace.getWidth(), pathTrace.getHeight(), rotation, true);
			canvas.drawBitmap(pathTrace, null, drawArea, Consts.stdPaint);
		}
	}

	@Override
	public Bitmap getSelf() {
		if (this.frozenTurnCount == 0) {
			return self;
		} else {
			return frozen_self;
		}
	}
}
