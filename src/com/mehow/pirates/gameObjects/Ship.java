package com.mehow.pirates.gameObjects;

import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;

import com.mehow.pirates.Cords;
import com.mehow.pirates.R;

//work out how to have cord copy hash map better

public class Ship extends GameObject{

    private final ShipPathAlgs shipPathAlgs;
    private final MinePathAlgs minePathAlgs;
    
    private int moveRange = 3;
    private static int mineRange = 1;

    //animation&display
    private static Bitmap self;
    
    public Ship(Cords cords, PathAlgorithms.Callbacks pathCallbacks){
        super(cords);
        shipPathAlgs = new ShipPathAlgs(pathCallbacks);
        minePathAlgs = new MinePathAlgs(pathCallbacks);
	}
    /*
        MOVEMENT
     */
    public int getMoveRange(){
        return moveRange;
    }
    public ArrayList<Cords> getPossibleShipMoves(){
        System.out.println("possiblemoves: "+shipPathAlgs.getAllCords());
        return shipPathAlgs.getAllCords();
    }
    public void findPossibleMoves(){
        shipPathAlgs.findPossibleMoves(turnRecords.getLatestCords(), getMoveRange());
    }
    public void clearPossibleMoves(){
        shipPathAlgs.clear();
        minePathAlgs.clear();
    }
    public boolean canMove(Cords cords){
        return shipPathAlgs.contains(cords);
    }
    /*
        MINE MOVEMENT
     */
    public int getMineRange(){
        return mineRange;
    }
    public ArrayList<Cords> getPossibleMineMoves(){
        return minePathAlgs.getAllCords();
    }
    //if movecord parameter was passed in from mapset location, would it be better?
    public void findPossibleMineMoves(){
        minePathAlgs.findPossibleMoves(turnRecords.getLatestCords(), getMineRange());
    }
    public boolean isInMineCords(Cords touchedCord){
        return minePathAlgs.contains(touchedCord);
    }
    public ArrayList<Cords> getAllMineCords(){
        return minePathAlgs.getAllCords();
    }
    
    @Override
    protected Bitmap getSelf() {
 	   return self;
    }
    @Override
    protected Paint getSelfPaint(){
    	return stdPaint;
    }
    
    //load image to bitmap from drawable objects
   public static void loadSpecialBitmaps(Resources r){
	   	self = BitmapFactory.decodeResource(r, R.drawable.ship);
    }
    
    public void drawMineMoves(Canvas canvas, RectF drawArea){
    	Paint movePaint = new Paint();
    	movePaint.setARGB(100, 100, 0, 0);
    	float xOffset;
    	float yOffset;
    	for (Cords highlightCord : this.getPossibleMineMoves()) {
    		xOffset = calculateCanvasOffset(highlightCord.x, highlightCord.x, 0, drawArea.width());
    		yOffset = calculateCanvasOffset(highlightCord.y, highlightCord.y, 0, drawArea.height());    		
    		drawArea.offsetTo(xOffset, yOffset);
    		canvas.drawRect(drawArea, movePaint);
    	}
    }
    
    public void drawShipMoves(Canvas canvas, RectF drawArea){
    	Paint movePaint = new Paint();
    	movePaint.setARGB(100, 100, 0, 0);
    	float xOffset;
    	float yOffset;
    	for (Cords highlightCord : this.getPossibleShipMoves()) {
    		xOffset = calculateCanvasOffset(highlightCord.x, highlightCord.x, 0, drawArea.width());
    		yOffset = calculateCanvasOffset(highlightCord.y, highlightCord.y, 0, drawArea.height());    		
    		drawArea.offsetTo(xOffset, yOffset);
    		canvas.drawRect(drawArea, movePaint);
    	}
    }
    /*
        Activity lifecycle Bundling
    */
	private Bundle flattenPrevMoves(){
		Bundle bundle = new Bundle();
		Cords cords;
	/*	bundle.putInt("PREV_MOVES_SIZE", previousMoves.size());
		for(int i=0; i<previousMoves.size();i++){
			cords = previousMoves.get(i);
			bundle.putInt("TURN_"+i+"_X", cords.x);
			bundle.putInt("TURN_"+i+"_Y", cords.y);
		}*/
		return bundle;
	}
	private Bundle inflatePrevMoves(Bundle bundle){
		/*int prevMovesSize = bundle.getInt("PREV_MOVES_SIZE");
		for(int i=0; i<prevMovesSize;i++){
			previousMoves.add(new Cords(bundle.getInt("TURN_"+i+"_X"),bundle.getInt("TURN_"+i+"_Y")));
		}*/
		return bundle;
	}

	/*public Bundle shipSaveState(){
		Bundle bundle = new Bundle();
        bundle.putSerializable();
		bundle.putInt("SHIP_X", shipCord.x);
		bundle.putInt("SHIP_Y", shipCord.y);
		bundle.putBundle("PREV_MOVES", flattenPrevMoves());
		bundle.putBundle("shipPathAlgs", shipPathAlgs.pathAlgSaveState());
		return bundle;
	}*/

    public void loadState(Bundle bundle){
        shipPathAlgs.loadState(bundle.getBundle("shipPathAlgs"));
        int x = bundle.getInt("SHIP_X");
        int y = bundle.getInt("SHIP_Y");
        inflatePrevMoves(bundle.getBundle("PREV_MOVES"));
    }
}
