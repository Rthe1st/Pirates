package com.mehow.pirates.gameObjects.enemys;

import java.io.Serializable;
import java.util.Vector;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.mehow.pirates.AnimationLogic;
import com.mehow.pirates.Cords;
import com.mehow.pirates.R;

public class PathEnemy extends Enemy implements Serializable{
	protected static int defNumOfMovesAllowed = 1;

	public static final String ENCODE_VALUE = "3";

	private Vector<Cords> moveCords;

	int moveIndex;

	// if a path is connected in a loop, walk round it, else, go back and forth
	// improvement: add the option for player to turn the loop on or off?
	private boolean loop;
	private int direction;

	// move horizontaly before vert
	public PathEnemy(Cords cords, int tempNumOfMovesAllowed,
			Callbacks tCallbacks, String parameters) {
		super(cords, tempNumOfMovesAllowed, tCallbacks);
		moveCords = new Vector<Cords>();
		parseParameters(parameters);
		moveIndex = 0;
	}

	public PathEnemy(Cords cords, Callbacks tCallbacks, String parameters) {
		super(cords, defNumOfMovesAllowed, tCallbacks);
		moveCords = new Vector<Cords>();
		parseParameters(parameters);
		moveIndex = 0;
	}

	public PathEnemy(Cords cords, Callbacks tCallbacks) {
		super(cords, defNumOfMovesAllowed, tCallbacks);
		moveCords = new Vector<Cords>();
		moveCords.add(cords);
		moveIndex = 0;
		Log.i("PathEnemy", "moveCords length:" + moveCords.size());
	}

	// parameters are of the form [(x?y)!(x?y),...]
	private void parseParameters(String parameters) {
		// cut of begining and ending [...]
		parameters = parameters.substring(0);
		parameters = parameters.substring(1, parameters.length() - 1);
		Log.i("MapData", "Parameters: " + parameters);
		String[] splitParameters = parameters.split("!");
		for (String parameter : splitParameters) {
			// process (x,y)
			Log.i("PathEnemy", "cord parameter " + parameter);
			int x = Integer.parseInt(parameter.substring(1, 2));
			int y = Integer.parseInt(parameter.substring(3, 4));
			Log.i("PathEnemy", "x: " + x + " y " + y);
			Cords cords = new Cords(x, y);
			moveCords.add(cords);
		}
		if (moveCords.get(0).isNextTo(moveCords.get(moveCords.size() - 1))) {
			loop = true;
			direction = 1;
		}else{
			direction = 1;
		}
	}

	@Override
	public String getEncodedParameters() {
		String encodedParameters = "[";
		for (Cords cords : moveCords) {
			encodedParameters += "(" + cords.x + "?" + cords.y + ")!";
		}
		// remove last !
		encodedParameters = encodedParameters.substring(0,
				encodedParameters.length() - 1);
		encodedParameters += "]";
		return encodedParameters;
	}

	// moves x and y, chooses axis based on displacement
	@Override
	public Cords computeMoveStep(Cords shipCords) {
		moveIndex += direction;
		if (loop && moveIndex >= moveCords.size()) {
			moveIndex = 0;
		} else {
			if (moveIndex >= moveCords.size() || moveIndex < 0) {
				//flip direction
				direction *= -1;
				// times 2 because 1 to go back to current index, 1 to actualy
				// move backwards
				moveIndex += direction * 2;
			}
		}
		return moveCords.get(moveIndex);
	}

	public void changeMoveCords(Cords cords) {
		if (cords.equals(this.currentCords)) {
			return;
		} else if (moveCords.contains(cords)) {
			tryDeleteMoveCords(cords);
		} else {
			tryAddMoveCords(cords);
		}
	}

	public boolean tryDeleteMoveCords(Cords cords) {
		if (moveCords.get(0).equals(cords)) {
			moveCords.remove(0);
			return true;
		} else if (moveCords.get(moveCords.size() - 1).equals(cords)) {
			moveCords.remove(moveCords.size() - 1);
			return true;
		} else {
			return false;
		}
	}

	public boolean tryAddMoveCords(Cords newCords) {
		Cords firstMoveCords = moveCords.get(0);
		Cords lastMoveCords = moveCords.get(moveCords.size() - 1);
		// order matter, it seemed more intuative for players to add the next
		// move to the
		// end rather then start if both option are valid
		if (lastMoveCords.isNextTo(newCords)
				&& !newCords.equals(firstMoveCords)) {
			moveCords.add(newCords);
			return true;
		} else if (firstMoveCords.isNextTo(newCords)
				&& !newCords.equals(lastMoveCords)) {
			moveCords.add(0, newCords);
			return true;
		} else {
			return false;
		}
	}

	// ------------
	// ANIMATION
	// ------------

	private static Bitmap self;

	public static void loadSpecialBitmaps(Resources r) {
		self = BitmapFactory.decodeResource(r, R.drawable.pathenemy_ship);
	}

	public Bitmap getSelf() {
		return self;
	}

	@Override
	public void selectedDraw(Canvas canvas, RectF drawArea) {
		drawMovementSquares(canvas, drawArea, true);
	}

	@Override
	public void drawSelfNoAnimate(Canvas canvas, RectF drawArea) {
		super.drawSelfNoAnimate(canvas, drawArea);
		drawMovementSquares(canvas, drawArea, false);
	}

	@Override
	public void drawSelf(Canvas canvas, int interStepNo, float animationOffset,
			RectF drawArea) {
		super.drawSelf(canvas, interStepNo, animationOffset, drawArea);
		drawMovementSquares(canvas, drawArea, false);
	}

	private void drawMovementSquares(Canvas canvas, RectF drawArea,
			boolean isSelected) {
		for (Cords cords : this.moveCords) {
			float xOffset = AnimationLogic.calculateCanvasOffset(cords.x,
					cords.x, 0, drawArea.width());
			float yOffset = AnimationLogic.calculateCanvasOffset(cords.y,
					cords.y, 0, drawArea.height());
			drawArea.offsetTo(xOffset, yOffset);
			Paint movePaint = new Paint();
			if (isSelected == false) {
				movePaint.setARGB(100, 100, 0, 0);
			} else {
				movePaint.setARGB(100, 0, 100, 0);
			}
			canvas.drawRect(drawArea, movePaint);
		}
	}
}
