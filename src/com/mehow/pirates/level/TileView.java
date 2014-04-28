package com.mehow.pirates.level;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.mehow.pirates.AnimationLogic;
import com.mehow.pirates.Cords;
import com.mehow.pirates.gameObjects.GameObject;
import com.mehow.pirates.gameObjects.GoalTile;
import com.mehow.pirates.gameObjects.Mine;
import com.mehow.pirates.gameObjects.RockTile;
import com.mehow.pirates.gameObjects.SeaTile;
import com.mehow.pirates.gameObjects.Ship;
import com.mehow.pirates.gameObjects.enemys.Aenemy;
import com.mehow.pirates.gameObjects.enemys.Henemy;
import com.mehow.pirates.gameObjects.enemys.Venemy;
import com.mehow.pirates.level.activites.LevelActivity;

//this call should be changed to deal with only the interaction with view objects (ie not game logic)

public class TileView extends View implements
		AnimationLogic.AnimationView {

	private static int tileWidth;
	private static int tileHeight;
	private static RectF tileDrawArea;

	private GameLogicCallbacks gameLogicCallbacks;

	public AnimationLogic animationLogic;
	
	public interface GameLogicCallbacks {
		public void animationFinished();

		public GameLogic.GameStates getGameState();

		public int getMapWidth();

		public int getMapHeight();

		public GameLogic.GameStates onActionUp(Cords cord);

		public boolean invalidate();

		public void draw(Canvas canvas, int interStepNo, float offsetAmount, RectF drawArea);
		
		public boolean checkMoreMoves(int stepNumber);
	};

	public TileView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// Activities (!?FRAGMENTS?!)containing this view must implement its
		// callbacks.
		Activity activity = (Activity) this.getContext();
		// this should NOT have to cast activity, removes the point of
		// callbacks.
		if (!(((LevelActivity) activity).gameLogic instanceof GameLogicCallbacks)) {
			throw new IllegalStateException(
					"Activity must implement Gamelogic callbacks.");
		}

		gameLogicCallbacks = ((LevelActivity) activity).gameLogic;// this
																	// should
																	// probably
																	// go via a
																	// fragment?
		animationLogic = new AnimationLogic(this, ((LevelActivity) activity).gameLogic);
		// setup highlight colour for movement squares
		// chosenMap = attrs.getAttributeIntValue(0, 0) ;
		// resetData();
	}

	// this is called as soon as size is set, oldw and old h will be 0 is size
	// set for first time
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		defineMapProperties(w, h);
		// load for each game object class
		Resources r = this.getResources();
		GameObject.loadBitmapsAndPaints(r);
		Ship.loadSpecialBitmaps(r);
		Mine.loadSpecialBitmaps(r);
		GoalTile.loadSpecialBitmaps(r);
		RockTile.loadSpecialBitmaps(r);
		SeaTile.loadSpecialBitmaps(r);
		Aenemy.loadSpecialBitmaps(r);
		Venemy.loadSpecialBitmaps(r);
		Henemy.loadSpecialBitmaps(r);
	}

	// get drawable objects of image in res
	public Drawable getImageDrawable(int intDrawable) {
		Resources r = this.getContext().getResources();
		return r.getDrawable(intDrawable);
	}

	// derive size tiles should be
	private void defineMapProperties(int width, int height) {
		System.out.println("width: " + width + " height: " + height);
		tileWidth = (int) Math.floor(width / gameLogicCallbacks.getMapWidth());
		// System.out.println("pre round tilewidth"+Math.floor(width/mapData.getMapWidth()));
		tileHeight = (int) Math.floor(height/ gameLogicCallbacks.getMapHeight());
		// System.out.println("pre round tileheight"+Math.floor(width/mapData.getMapHeight()));
		// make sure always square - not needed currently but may be for
		// different screens
		 tileWidth = Math.min(tileWidth, tileHeight);
		 tileHeight = tileWidth;
		tileDrawArea = new RectF(0,0, tileWidth, tileHeight);
		System.out.println("tileh: " + tileHeight + " tilew: " + tileWidth);
	}

	// --------------------------------
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		double animationFrameDistance = (double) tileWidth / (double) animationLogic.getNumberOfStages();
		int offsetAmount = (int) Math.round((animationLogic.getCurrentAnimationOffsetNo())*animationFrameDistance);
		gameLogicCallbacks.draw(canvas, animationLogic.getCurrentAnimationInterStepNo(), offsetAmount, tileDrawArea);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		float y, x;
		int xCord, yCord;
		if (action == MotionEvent.ACTION_UP
				&& gameLogicCallbacks.getGameState() != GameLogic.GameStates.MOVE_COMPLETE) {
			System.out.println("ACTION_UP and gameSTATE != MOVE_COMPLETE");
			y = event.getY();
			x = event.getX();
			System.out.println("float cords: x: " + x + " y: " + y);
			// System.out.println("tileWidth: "+tileWidth+" tileHeight: "+tileHeight);
			xCord = (int) Math.floor(x / tileWidth);
			yCord = (int) Math.floor(y / tileHeight);
			Cords touchedCord = new Cords(xCord, yCord);
			GameLogic.GameStates returnState = gameLogicCallbacks
					.onActionUp(touchedCord);
			System.out.println("end state: " + returnState.toString());
			if (gameLogicCallbacks.invalidate()) {
				System.out.println("invalidated");
				//this.invalidate();
				animationLogic.updateScreen(false);
			}
		}
		return true;
	}
}