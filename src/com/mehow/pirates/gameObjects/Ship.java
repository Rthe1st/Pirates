package com.mehow.pirates.gameObjects;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.mehow.pirates.AnimationLogic;
import com.mehow.pirates.AnimationSteps;
import com.mehow.pirates.Cords;
import com.mehow.pirates.Moves;
import com.mehow.pirates.R;
import com.mehow.pirates.level.GameLogic;

//work out how to have cord copy hash map better

public class Ship implements GameObject, Serializable, Moves{

	public static final String ENCODE_VALUE = "2";
	
	protected static Paint stdPaint;
	protected static Paint disabledPaint;
	private static Paint errorPaint;
	
    protected Cords currentCords;
    
    AnimationSteps animationSteps;
	
	private final ShipPathAlgs shipPathAlgs;
    private final MinePathAlgs minePathAlgs;
    
    private int moveRange = 3;
    private static int mineRange = 1;
    
    private boolean hasMoved;
    
    private boolean hasMined;
    
    //animation&display
    private static Bitmap self;
    
    public Ship(Cords cords, PathAlgorithms.Callbacks pathCallbacks){
        currentCords = cords;
        animationSteps = new AnimationSteps(cords);
        shipPathAlgs = new ShipPathAlgs(pathCallbacks);
        minePathAlgs = new MinePathAlgs(pathCallbacks);
        hasMoved = false;
        hasMined = false;
	}
    
    public static void loadPaints(Resources r){
    	stdPaint = new Paint();
    	disabledPaint = new Paint();
    	errorPaint = new Paint();
    	disabledPaint.setARGB(255, 100, 100, 100);
		errorPaint.setARGB(100, 100, 0, 100);
    }
    
    public boolean allowedToMove(){
    	return !(hasMoved ||hasMined);
    }
    
    public boolean allowedToMine(){
    	return !(hasMined);
    }
    
	//syncing animation steps after mapdata manipulation
    @Override
    public void newTurn(){
    	animationSteps.clearSteps(currentCords);
        hasMoved = false;
        hasMined = false;
    }
    @Override
    public void makeStep(Cords newCords){
    	currentCords = newCords;
    	animationSteps.makeStep(currentCords);
        hasMoved = true;
    }
    
    @Override
    public InterStep getCurrentInterStep(int interStepNo){
    	return animationSteps.getCurrentTurnInterStep(interStepNo);
    }
    
    @Override
    public void undoStep(Cords newCords){
    	//relies on animationsteps being insync with the global undo, whihc it should be
    	//but werid and redundant storing same data in 2 places
    	animationSteps.undoStep();
    	hasMoved = false;
    }
    
    @Override
    public void undoTurn(Cords newCords){
    	currentCords = newCords;
    	animationSteps.clearSteps(currentCords);
    	hasMoved = false;
        hasMined = false;
    }

	@Override
	public void noLongerExists() {}

	@Override
	public void kill() {
		currentCords = null;
	}

	@Override
	public void revive(Cords newCords) {
    	//clear cords?
    	currentCords = newCords;
	}
    
    public void layMine(){
    	hasMined = true;
    }

    public void undoLayMine(){
    	hasMined = false;
    }
    
	@Override
    public boolean trySelect(GameLogic.GameStates gameState){
		boolean success = false;
    	Log.i("GameLogic", "allowed to move: "+allowedToMove()+"allowed to mine: "+allowedToMine());
		switch (gameState) {
		case MINE_MODE:
			if(allowedToMine()){
				findPossibleMineMoves();
				success = true;
			}
			break;
		case MOVE_MODE:
			if(allowedToMove()){
				findPossibleMoves();
				success = true;
			}
		
			break;
		default:
			throw (new RuntimeException(
					"try to select ship in unexpeected state: "
							+ gameState.toString()));
		}
		return success;
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
    	System.out.println("finding moves, move range: "+getMoveRange());
        shipPathAlgs.findPossibleMoves(currentCords, getMoveRange());
    }
    public void clearPossibleMoves(){
        shipPathAlgs.clear();
        minePathAlgs.clear();
    }
    public boolean canMove(Cords cords){
        return shipPathAlgs.contains(cords);
    }
    
    public static boolean isValidMove(CordData cordData){
    	return cordData.enemy == null
				&& cordData.ship == null
				&& (cordData.tile instanceof Sea || cordData.tile instanceof Goal)
				&& cordData.mine == null;
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
        //minePathAlgs.findPossibleMoves(turnRecords.getLatestCords(), getMineRange());
        minePathAlgs.findPossibleMoves(currentCords, getMineRange());
    }
    public boolean isInMineCords(Cords touchedCord){
        return minePathAlgs.contains(touchedCord);
    }
    public ArrayList<Cords> getAllMineCords(){
        return minePathAlgs.getAllCords();
    }
    
    @Override
    public Bitmap getSelf() {
 	   return self;
    }

    public Paint getSelfPaint(){
    	if(this.hasMined && this.hasMoved){
    		return disabledPaint;
    	}else{
        	return stdPaint;
    	}
    }
    
    //load image to bitmap from drawable objects
   public static void loadSpecialBitmaps(Resources r){
	   	self = BitmapFactory.decodeResource(r, R.drawable.ship);
    }
   
   @Override
   public boolean hasMoreSteps(int interStepNumber){
	   return animationSteps.hasMoreSteps(interStepNumber);
   }
    
   @Override
   public void selectedDraw(Canvas canvas, RectF drawArea){
		drawShipMoves(canvas, drawArea);
		drawMineMoves(canvas, drawArea);
   }
   
    private void drawMineMoves(Canvas canvas, RectF drawArea){
    	Paint movePaint = new Paint();
    	movePaint.setARGB(100, 100, 0, 0);
    	float xOffset;
    	float yOffset;
    	for (Cords highlightCord : this.getPossibleMineMoves()) {
    		xOffset = AnimationLogic.calculateCanvasOffset(highlightCord.x, highlightCord.x, 0, drawArea.width());
    		yOffset = AnimationLogic.calculateCanvasOffset(highlightCord.y, highlightCord.y, 0, drawArea.height());    		
    		drawArea.offsetTo(xOffset, yOffset);
    		canvas.drawRect(drawArea, movePaint);
    	}
    }
    
    private void drawShipMoves(Canvas canvas, RectF drawArea){
    	Paint movePaint = new Paint();
    	movePaint.setARGB(100, 100, 0, 0);
    	float xOffset;
    	float yOffset;
    	for (Cords highlightCord : this.getPossibleShipMoves()) {
    		xOffset = AnimationLogic.calculateCanvasOffset(highlightCord.x, highlightCord.x, 0, drawArea.width());
    		yOffset = AnimationLogic.calculateCanvasOffset(highlightCord.y, highlightCord.y, 0, drawArea.height());    		
    		drawArea.offsetTo(xOffset, yOffset);
    		canvas.drawRect(drawArea, movePaint);
    	}
    }

	@Override
	public Cords getCurrentCords() {
		return currentCords;
	}

    public void drawSelf(Canvas canvas, int interStepNo, float animationOffset, RectF drawArea) {
    	InterStep currentStep;
    	//-1 for 0 based step array
    	//-1 because number of inter-steps is 1 less then number of steps
    	if(animationSteps.hasMoreSteps(interStepNo)){
    		currentStep = animationSteps.getCurrentTurnInterStep(interStepNo);
    	}else{
    		currentStep = new InterStep(getCurrentCords(),getCurrentCords());
    	}
    	float xOffset = AnimationLogic.calculateCanvasOffset(currentStep.startCords.x, currentStep.endCords.x, animationOffset, drawArea.width());
    	float yOffset = AnimationLogic.calculateCanvasOffset(currentStep.startCords.y, currentStep.endCords.y, animationOffset, drawArea.height());
    	//check this offsets in the right direction
    	drawArea.offsetTo(xOffset, yOffset);
        canvas.drawBitmap(getSelf(), null, drawArea, getSelfPaint());
    }
    public void drawSelfNoAnimate(Canvas canvas, RectF drawArea) {
    	InterStep currentStep = new InterStep(currentCords,currentCords);
    	float xOffset = AnimationLogic.calculateCanvasOffset(currentStep.startCords.x, currentStep.endCords.x, 0, drawArea.width());
    	float yOffset = AnimationLogic.calculateCanvasOffset(currentStep.startCords.y, currentStep.endCords.y, 0, drawArea.height());
    	drawArea.offsetTo(xOffset, yOffset);
        canvas.drawBitmap(getSelf(), null, drawArea, getSelfPaint());
    }

	@Override
	public boolean exists() {
        return currentCords != null;
	}
}
