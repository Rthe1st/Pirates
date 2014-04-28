package com.mehow.pirates.graphics;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MenuBkSurfaceView extends SurfaceView implements SurfaceHolder.Callback{
	
	private SurfaceHolder holder;
	private MenuBkSurfaceViewThread viewThread;
	private boolean hasSurface;
	private Context context;
	private int width;
	private int height;
	
	//hasSurface is only required to make sure pause+resume functions work
	//properly, assuming they're used to prevent needless re-creating of viewthread
	
	public MenuBkSurfaceView(Context context, AttributeSet attrs){
		super(context,attrs);
		initialise(context);
	}
	public MenuBkSurfaceView(Context context){
		super(context);
		initialise(context);
	}
	public MenuBkSurfaceView(Context context, AttributeSet attrs, int defStyle)
	{
	    super(context, attrs, defStyle);
	    //this.context = context;
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
	//may have to check holder not null
	public void surfaceCreated(SurfaceHolder holder){
		//create and start the graphics update thread
		//System.out.println("surface created");
		width = this.getWidth();
		height = this.getHeight();
		if(viewThread == null){
			//System.out.println("viewthread was null, re-constructing");
			viewThread = new MenuBkSurfaceViewThread(holder, context, width, height);
			viewThread.start();
		}
	}
	public void pause(){
		//kill graphics update thread
		//System.out.println("paused");
		if(viewThread != null){
	//		System.out.println("viewhread set to null");
			viewThread.requestExitAndWait();
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
}
