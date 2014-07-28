package com.mehow.pirates.level;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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

public class TileView extends SurfaceView implements SurfaceHolder.Callback{
	//implements AnimationLogic.AnimationViewCallbacks {

	//surfaceview shiz
	SurfaceHolder holder;
	Context context;
	boolean hasSurface;
	
	private static int tileWidth;
	private static int tileHeight;
	private static RectF tileDrawArea;

	private LogicCallbacks logicCallbacks;

	//public AnimationLogic animationLogic;

	public TileViewThread viewThread;
	
	public interface ActivityCallbacks{
		public LogicCallbacks getLogicInstance();
	}
	
	public interface LogicCallbacks extends com.mehow.pirates.AnimationLogic.LogicCallbacks{
		public int getMapWidth();

		public int getMapHeight();

		public void onActionUp(Cords cord);

		public void draw(Canvas canvas, RectF drawArea);
		
		public void update(long timeChange);
	};

	public TileView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Activity activity = (Activity) this.getContext();
		if (!(activity instanceof ActivityCallbacks)) {
			throw new IllegalStateException(
					"Activity must return a logic.");
		}
		logicCallbacks = ((ActivityCallbacks)activity).getLogicInstance();
		//animationLogic = new AnimationLogic(this, logicCallbacks);
		initialise(context);
	}

	private void initialise(Context tContext){
		//create surfaceviewholder and assign this class as the callback
		holder = getHolder();
		holder.addCallback(this);
		hasSurface = true;
	//	System.out.println("initialised");
		context = tContext;
	}
	
	public RectF getTileDrawArea(){
		return tileDrawArea;
	}
	
	// this is called as soon as size is set, oldw and old h will be 0 is size
	// set for first time
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		defineMapProperties(w, h);
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
			final Cords touchedCord = new Cords(xCord, yCord);
			this.viewThread.addUserInput(new Runnable(){
				@Override
				public void run(){
					logicCallbacks.onActionUp(touchedCord);					
				}
			});
		}
		return true;
	}
	
	//surfaceview shiz
	
	//may have to check holder not null
	public void surfaceCreated(SurfaceHolder holder){
		//create and start the graphics update thread
		if(viewThread == null){
			//System.out.println("viewthread was null, re-constructing");
			viewThread = new TileViewThread(holder, context, logicCallbacks, this);
			viewThread.start();
		}
	}
	public void pause(){
		//kill graphics update thread
		//System.out.println("paused");
		if(viewThread != null){
	//		System.out.println("viewhread set to null");
			viewThread.requestExitAndWait();
			try {
				viewThread.join();
			} catch (InterruptedException e) {

			}
			viewThread = null;
		}
	}
	//not actuly used, is not a required function, but recommended by book
	/*public void resume(){
	//	System.out.println("resumed");
		//create and start the graphics update thread
		width = this.getWidth();
		height = this.getHeight();
		if(viewThread == null){
			viewThread = new MenuBkSurfaceViewThread(holder, context, width, height);
			if(hasSurface == true){
				viewThread.start();
			}
		}
	}*/
	public void surfaceDestroyed(SurfaceHolder holder){
	//	System.out.println("surface destroyed");
		hasSurface = false;
		pause();
	}
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h){
		if(viewThread != null){
			viewThread.onWindowResize(w, h);
		}
	}
	
	public void addUserInput(Runnable newUserInput){
		viewThread.addUserInput(newUserInput);
	}
}