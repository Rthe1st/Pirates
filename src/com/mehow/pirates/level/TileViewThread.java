package com.mehow.pirates.level;

import java.util.Stack;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class TileViewThread extends Thread{
	public static final int FRAME_RATE = 20;
	public static final int MS_PER_FRAME = 1000/FRAME_RATE;
	private volatile boolean done;
	private SurfaceHolder holder;
	private Context context;
	private TileView.LogicCallbacks logicCallbacks;
	private TileView tileView;
	private Stack<Runnable> userInput;
	private long startTime;
	private long lastUpdateTime;
	
	TileViewThread(SurfaceHolder tHolder, Context tContext, TileView.LogicCallbacks tLogicCallbacks, TileView tTileView){
		super();
		done = false;
		holder = tHolder;
		context = tContext;
		logicCallbacks = tLogicCallbacks;
		tileView = tTileView;
		userInput = new Stack<Runnable>();
		lastUpdateTime = System.currentTimeMillis();
		startTime = System.currentTimeMillis();
	}
	
	@Override
	public void run(){
		System.out.println("running thread");
		SurfaceHolder surfaceHolder = holder;
		while(!done){
			if(userInput.size()>0){
				getUserInput().run();
			}
			startTime = System.currentTimeMillis();
			long timeChange = startTime-lastUpdateTime;
			lastUpdateTime = startTime;
			logicCallbacks.update(timeChange);
			//System.out.println("drawing inthread");
			//lock surface whilst drawing
			Canvas canvas = surfaceHolder.lockCanvas();
			if(canvas != null){
				//draw 
				canvas.drawRGB(255, 255, 255);
				logicCallbacks.draw(canvas, tileView.getTileDrawArea());
				//unlock surface view and render canvas
				surfaceHolder.unlockCanvasAndPost(canvas);
			}
			//limit the frame rate, if processing is done early, sleeps
			try {
				long sleepTime = startTime+MS_PER_FRAME-System.currentTimeMillis();
				//think about this
				if(sleepTime < 0){
					sleepTime = 0;
				}
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				//when user input added, interrupt will happen
				//end sleep
			}
		}
	}

	public void requestExitAndWait(){
		//mark thread as cmplete and combine with main thread
		done = true;
		//should probs actualy do something with catch
		try{
			join();
		}catch(InterruptedException ex){
			
		}
	}
	public void onWindowResize(int w, int h){
		//deal wit da change;
	}
	
	//check this is syncronised used proberly
	public synchronized void addUserInput(Runnable newUserInput){
		//cheeky, bad code?
		//because game is basicly event based, only 1 user input allowed befrore the game updates
		if(userInput.size()==0){
			userInput.add(newUserInput);
		}
		this.interrupt();
	}
	
	private synchronized Runnable getUserInput(){
		return userInput.pop();
	}
}
