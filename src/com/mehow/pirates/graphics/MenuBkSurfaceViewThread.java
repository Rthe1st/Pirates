package com.mehow.pirates.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.SurfaceHolder;

import com.mehow.pirates.R;

public class MenuBkSurfaceViewThread extends Thread{
	private volatile boolean done;
	private SurfaceHolder holder;
	private Context context;
	int height = 0;
	int width = 0;
	int badguyHeight = 50;
	int badguyWidth = 50;
	
	MenuBkSurfaceViewThread(SurfaceHolder tHolder, Context tContext, int tWidth, int tHeight){
		super();
		done = false;
		holder = tHolder;
		context = tContext;
		width = tWidth;
		height = tHeight;
	}
	
	@Override
	public void run(){
		System.out.println("running thread");
		SurfaceHolder surfaceHolder = holder;
		//System.out.print("holder: "+holder+" surfaceholder: "+surfaceHolder);
		//keep drawing till thread stopped
		int maxFrameSlow = 10;
		int frameSlow = 0;
		int xPos = width/2;
		int yPos = height-badguyHeight;
		int moveDisX = xPos;
		int moveDir = 0;
		int curFrame = 0;
		Drawable badguy;
		while(!done){
			//System.out.println("drawing inthread");
			//lock surface whilst drawing
			try{
				if(moveDir == -1){
					switch(curFrame){
						case 0: badguy = context.getResources().getDrawable(R.drawable.menu_badguy_left_1);break;
						default: badguy = context.getResources().getDrawable(R.drawable.menu_badguy_left_2);
					}
				}else if(moveDir == 1){
					switch(curFrame){
						case 0: badguy = context.getResources().getDrawable(R.drawable.menu_badguy_right_1);break;
						default: badguy = context.getResources().getDrawable(R.drawable.menu_badguy_right_2);
					}				
				}else{
					switch(curFrame){
						case 0: badguy = context.getResources().getDrawable(R.drawable.menu_badguy_stationary_1);break;
						default: badguy = context.getResources().getDrawable(R.drawable.menu_badguy_stationary_2);
					}
				}
				if(frameSlow == maxFrameSlow){
					if(curFrame > 2){
						curFrame = 0;
					}else{
						curFrame += 1;
					}
					frameSlow = 0;
				}else{
					frameSlow += 1;
				}
				Canvas canvas = surfaceHolder.lockCanvas();
				if(canvas != null){
					//draw
					Bitmap imageBitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888 );
					Canvas imageCanvas = new Canvas(imageBitmap);
					badguy.setBounds(0, 0, 50, 50);
					badguy.draw(imageCanvas);
					canvas.drawRGB(255, 255, 255);
					//	System.out.println("width: "+width);
					canvas.drawBitmap(imageBitmap, xPos, yPos, new Paint());
					//unlock surface view and render canvas
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
				if(moveDir == 0){
					moveDisX = calcMoveDis(xPos);
				//	System.out.println("curX: "+xPos+" nextX: "+(xPos+moveDisX)+" (moveDis: "+moveDisX+")");
					if(moveDisX == 0){
						moveDir = 0;
						Thread.sleep(1000);
					}else if(moveDisX < 0){
						moveDir = -1;
					}else if(moveDisX > 0){
						moveDir = 1;
					}else{
						moveDir = 0;
					}
				//	System.out.println("movedir: "+moveDir);
				}
				if(moveDisX < 0 && moveDir == -1){
					xPos -= 1;
					moveDisX += 1;
				}else if(moveDisX > 0 && moveDir == 1){
					xPos += 1;
					moveDisX -= 1;
				}else{
					moveDir = 0;
					moveDisX = 0;
				}
			}catch(InterruptedException e){
			}
		}
	}
	private int calcMoveDis(int curX){
		//outputs the distance from curX to move
		//movement is weighted in favor of moving towards furthest edge
		//chance of standing still as well
		
		double closestEdgeWeighting = 0.25;
		double chanceOfStanding = 0.5;
		
		int moveDis;
		double leftChance;
		double rand = Math.random();
		//50% chance of not moving
		if(rand<chanceOfStanding){
			//System.out.println("-------------------------------calc dis: "+0);
			return 0;
		}else{
			//chance of moving towards more distante screen side is greater
			if(curX < width/2){
				leftChance = closestEdgeWeighting;
			}else{
				leftChance = 1-closestEdgeWeighting;
			}
			rand = Math.random();
		//	System.out.println("rand: "+rand+" leftchance: "+leftChance);
			if(rand <= leftChance){
				//move left
				//moves a random percentage of current x cord (distance from left screen edge)
				moveDis = (int) -((curX)*Math.random());//(int) ((Math.random()*((width-curX)/4))-badguyWidth);
		//		System.out.println("moving left, moveDis: "+moveDis);
			}else{
				//move right
				//move random percentage of distance from right screen edge
				moveDis = (int) ((width-badguyWidth-curX)*Math.random());	
		//		System.out.println("moving to right, moveDis: "+moveDis);
			}
			//System.out.println("--------------------------------calc dis: "+newX);
			return moveDis;
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
}
