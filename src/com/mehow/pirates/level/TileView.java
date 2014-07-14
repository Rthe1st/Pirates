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
import com.mehow.pirates.Consts;
import com.mehow.pirates.Cords;
import com.mehow.pirates.gameObjects.Goal;
import com.mehow.pirates.gameObjects.Mine;
import com.mehow.pirates.gameObjects.Rock;
import com.mehow.pirates.gameObjects.Sea;
import com.mehow.pirates.gameObjects.Ship;
import com.mehow.pirates.gameObjects.Tile;
import com.mehow.pirates.gameObjects.enemys.Aenemy;
import com.mehow.pirates.gameObjects.enemys.Enemy;
import com.mehow.pirates.gameObjects.enemys.Henemy;
import com.mehow.pirates.gameObjects.enemys.PathEnemy;
import com.mehow.pirates.gameObjects.enemys.Venemy;

//this call should be changed to deal with only the interaction with view objects (ie not game logic)

public class TileView extends View implements
		AnimationLogic.AnimationViewCallbacks {

	private static int tileWidth;
	private static int tileHeight;
	private static RectF tileDrawArea;

	private LogicCallbacks logicCallbacks;

	public AnimationLogic animationLogic;
	
	public interface ActivityCallbacks{
		public LogicCallbacks getLogicInstance();
	}
	
	public interface LogicCallbacks extends com.mehow.pirates.AnimationLogic.LogicCallbacks{
		public int getMapWidth();

		public int getMapHeight();

		public void onActionUp(Cords cord);

		public void draw(Canvas canvas, int interStepNo, float offsetAmount, RectF drawArea);
	};

	public TileView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Activity activity = (Activity) this.getContext();
		if (!(activity instanceof ActivityCallbacks)) {
			throw new IllegalStateException(
					"Activity must return a logic.");
		}
		logicCallbacks = ((ActivityCallbacks)activity).getLogicInstance();
		animationLogic = new AnimationLogic(this, logicCallbacks);
	}

	// this is called as soon as size is set, oldw and old h will be 0 is size
	// set for first time
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		defineMapProperties(w, h);
		// load for each game object class
		Resources r = this.getResources();
		Consts.loadPaints();
		Ship.loadPaints();
		Ship.loadSpecialBitmaps(r);
		Mine.loadSpecialBitmaps(r);
		Tile.loadPaints(r);
		Goal.loadSpecialBitmaps(r);
		Rock.loadSpecialBitmaps(r);
		Sea.loadSpecialBitmaps(r);
		Enemy.loadPaints(r);
		Aenemy.loadSpecialBitmaps(r);
		Venemy.loadSpecialBitmaps(r);
		Henemy.loadSpecialBitmaps(r);
		PathEnemy.loadSpecialBitmaps(r);
	}

	// get drawable objects of image in res
	public Drawable getImageDrawable(int intDrawable) {
		Resources r = this.getContext().getResources();
		return r.getDrawable(intDrawable);
	}

	// derive size tiles should be
	private void defineMapProperties(int width, int height) {
		System.out.println("width: " + width + " height: " + height);
		tileWidth = (int) Math.floor(width / logicCallbacks.getMapWidth());
		// System.out.println("pre round tilewidth"+Math.floor(width/mapData.getMapWidth()));
		tileHeight = (int) Math.floor(height/ logicCallbacks.getMapHeight());
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
		//assumes square tiles
		double animationFrameDistance = (double) tileWidth / (double) animationLogic.getNumberOfStages();
		int offsetAmount = (int) Math.round((animationLogic.getCurrentAnimationOffsetNo())*animationFrameDistance);
		logicCallbacks.draw(canvas, animationLogic.getCurrentAnimationInterStepNo(), offsetAmount, tileDrawArea);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		float y, x;
		int xCord, yCord;
		if (action == MotionEvent.ACTION_UP) {
			System.out.println("ACTION_UP");
			y = event.getY();
			x = event.getX();
			System.out.println("float cords: x: " + x + " y: " + y);
			// System.out.println("tileWidth: "+tileWidth+" tileHeight: "+tileHeight);
			xCord = (int) Math.floor(x / tileWidth);
			yCord = (int) Math.floor(y / tileHeight);
			Cords touchedCord = new Cords(xCord, yCord);
			logicCallbacks.onActionUp(touchedCord);
		}
		return true;
	}
}