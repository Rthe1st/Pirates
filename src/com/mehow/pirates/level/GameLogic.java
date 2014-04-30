package com.mehow.pirates.level;

import java.util.ArrayList;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Bundle;

import com.mehow.pirates.Cords;
import com.mehow.pirates.R;
import com.mehow.pirates.gameObjects.GoalTile;
import com.mehow.pirates.gameObjects.MapData;
import com.mehow.pirates.gameObjects.Mine;
import com.mehow.pirates.gameObjects.ScoreCalc;
import com.mehow.pirates.gameObjects.Ship;
import com.mehow.pirates.gameObjects.Tile;
import com.mehow.pirates.AnimationLogic;

/**
 * Created by User on 11/01/14.
 */
public class GameLogic implements TileView.GameLogicCallbacks
	, AnimationLogic.GameLogicCallbacks {

    public Callbacks mCallbacks;

    public interface Callbacks{
        public int getMineLimit();
        public void showLevelCompleteDialog(boolean setNewScore, int score);
        public int getLevelBestScore();
        public void updateCounts(int mineChange, int turnChange, int scoreChange);
        public Resources getResources();
        public int getMapId();
        public void changeMineBtnState(boolean state);
        public void showGameOverDialog();
        public void invalidateMap();
    }

//game states
    //see flow chart for details
    public enum GameStates {
        START_MOVE
        , MOVE_BEGUNG        //after this only mine can be placed
        , MOVE_COMPLETE        //attivate move complete when player presses "end go"
        , SHIP_MOVED
        , END_MOVE        //when user has done all possible actions, can only click end from here
        , MINE_MODE        //when user clicks mine button in a valid game state
        , GAME_OVER        //when user flops
        , LEVEL_COMPLETE        //when user anti-flops
    };

    private GameStates gameState;
    private GameStates lastGameState;
    //game var
    private int turnCount = 0;
    private int mineCount;
    private static int score = 0;

    public MapData mapData;

    //used to store a previously touched cord for use in next touch.
    // I.E. stores where the ship is when selected, for use updating ship next state when the cord to move ship to is chosen

    //access with set and release fucntions
    private Cords selectedCords;

    //constructor when creating level for first time (ie no android lifecycle funnyness)
    public GameLogic(Activity callBackActivity){
        if (!(callBackActivity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement GameLogic.Callbacks.");
        }
        mCallbacks = (Callbacks)callBackActivity;
        mineCount = mCallbacks.getMineLimit();
        mapData = new MapData();
        System.out.println("mapwidth pre: "+mapData.getMapWidth());
        //if(!mapData.isMapSet()){//if saved map not present
            System.out.println("isset");
            mapData.setupXMLMap(mCallbacks.getResources().getIntArray(R.array.mapSize),
                    mCallbacks.getResources().getStringArray(mCallbacks.getMapId()));
        //}
        System.out.println("mapwidth post: "+mapData.getMapWidth());
        gameState = GameStates.START_MOVE;
    }
    public GameLogic(Activity callBackActivity, Bundle bundle){
        if (!(callBackActivity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement GameLogic.Callbacks.");
        }
        mCallbacks = (Callbacks)callBackActivity;
        //bundebundeldundle
        mapData = new MapData(bundle.getBundle("MAP_DATA"));
        loadSelf(bundle);
    }
    //store gamestates as strings to save shit yo
    //whats a clever way to make sure saveState and loadState are kept in sync?
    public Bundle saveState(){
        Bundle bundle = new Bundle();
        bundle.putString("LAST_GAME_STATE", getLastGameState().toString());
        bundle.putString("GAME_STATE", gameState.toString());
        bundle.putInt("TURN_COUNT", turnCount);
        bundle.putInt("MINE_COUNT", mineCount);
        bundle.putInt("SCORE", score);
        bundle.putBundle("MAP_DATA", mapData.saveState());
        bundle.putSerializable("SELECTED_CORDS", selectedCords);
        return bundle;
    }
    private void loadSelf(Bundle bundle){
    	lastGameState = GameStates.valueOf(bundle.getString("LAST_GAME_STATE"));
    	gameState = GameStates.valueOf(bundle.getString("GAME_STATE"));
        turnCount = bundle.getInt("TURN_COUNT");
        mineCount = bundle.getInt("MINE_COUNT");
        score = bundle.getInt("SCORE");
        selectedCords = (Cords)bundle.getSerializable("SELECTED_CORDS");
    }

    //Check GameState diagram file for explanation
    public void undo(){
        System.out.println("undoing with gameState: "+getGameState());
        if(getGameState() == GameStates.START_MOVE && turnCount > 0){
            mapData.shipMap.undoTurn();
            mapData.mineMap.undoTurn();
            mapData.enemyMap.undoTurn();
            setGameState(GameStates.START_MOVE);
            changeTurnCount(-1);
            mCallbacks.invalidateMap();
        }else if(getGameState()==GameStates.MOVE_BEGUNG){
        	mapData.clearShipMoves();
        	setGameState(GameStates.START_MOVE);
        }else if(getGameState() == GameStates.SHIP_MOVED){
            mapData.shipMap.undoStep();
            setGameState(GameStates.START_MOVE);
            mCallbacks.invalidateMap();
        }else if(getGameState() == GameStates.MINE_MODE){
        	//go back to start of turn state
        	//FYI: mines for this turn wont of been placed yet, so don't undo
            if(getLastGameState()==GameStates.SHIP_MOVED){
        	    mapData.shipMap.undoStep();
            }
            setGameState(GameStates.START_MOVE);
            mCallbacks.invalidateMap();
       }else if(getGameState() == GameLogic.GameStates.END_MOVE){
           //undoStep can be called because mineMode -> endMove transition gives the ship a dummy move if it wasnt moved already
    	    mapData.shipMap.undoStep();
    	    mapData.mineMap.undoStep();
            setGameState(GameLogic.GameStates.START_MOVE);
            changeMineCount(1);
            mCallbacks.changeMineBtnState(true);
            mCallbacks.invalidateMap();
        }
    }

    public GameStates onActionUp(Cords touchedCords){
        System.out.println("orignl state: "+gameState);
        //Cords shipCords = mapData.ship.getShipCords();
        //hack used incase layout fails and tileview is bigger then is visible
        if(touchedCords.x < mapData.getMapWidth() && touchedCords.y < mapData.getMapHeight()){
            switch(gameState){
                case START_MOVE:
                    startMoveActionUp(touchedCords);break;
                case MOVE_BEGUNG:
                    moveBegunActionUp(touchedCords);break;
                case MINE_MODE:
                    mineModeActionUp(touchedCords);break;
                default:
                	throw new RuntimeException("onActionUp called whilst in invalid gameState: "+getGameState());
            }
        }
        return gameState;
    }
    public void startMoveActionUp(Cords touchedCords){
        if(mapData.shipMap.containsAt(touchedCords)){
            setGameState(GameStates.MOVE_BEGUNG);
            Ship selectedShip =  mapData.shipMap.get(touchedCords);
            selectedShip.findPossibleMoves();
            setSelectedCords(touchedCords);
        }
    }
    public void moveBegunActionUp(Cords touchedCords){
    	Ship ship = mapData.shipMap.get(getSelectedCords());
        if(ship.canMove(touchedCords)){
            setGameState(GameStates.SHIP_MOVED);
            mapData.shipMap.makeStep(getSelectedCords(), touchedCords);
            Tile postMoveTile = mapData.tileMap.get(touchedCords);
            if(postMoveTile instanceof GoalTile){
                changeTurnCount(1);
                setGameState(GameStates.LEVEL_COMPLETE);
                callLevelCompleteDialog();
            }
        }else{//move was invalid, reset
            setGameState(GameStates.START_MOVE);
        }
        ship.clearPossibleMoves();
        //reset selected cord (as its now been "used")
        setSelectedCords(null);
    }

    public void mineModeActionUp(Cords touchedCords){
    	//hack because UI doesn't account for multiple ships yet
    	//which is why getAllGo is OK
    	Ship ship = (new ArrayList<Ship>(mapData.shipMap.getAll())).get(0);
        if(ship.isInMineCords(touchedCords) && getMineCount() > 0){
            System.out.println("valid mine tile pressed");
            mapData.mineMap.newTurn();
            mapData.mineMap.put(touchedCords, new Mine(touchedCords));
            changeMineCount(-1);
            //if ship didnt move already then laying mines use up its move.
            if(getLastGameState() != GameStates.SHIP_MOVED){
                //this is jank hack.
            	//done because UI and flow dont identify which ship (because theres only 1 for now)
                //when "previous moves"/undo is redone, fix this for mutiple ships
                mapData.shipMap.makeStep(ship.getLatestCords(), ship.getLatestCords());
            }
            setGameState(GameStates.END_MOVE);
            mCallbacks.changeMineBtnState(false);
            ship.clearPossibleMoves();
        }else{
        	setGameState(getLastGameState());
        }
        
    }

    public boolean invalidate(){
        if(gameState != lastGameState){
            return true;
        }
        else{
            return false;
        }
    }

    public void setGameState(GameStates state){
        lastGameState = gameState;
        gameState = state;
        System.out.println("Game state transition: "+lastGameState+" to "+gameState);
    }
    public GameStates getGameState(){
        return gameState;
    }
    public GameStates getLastGameState(){
        return lastGameState;
    }
    public void setSelectedCords(Cords newCords){
        selectedCords = newCords;
    }
    public Cords getSelectedCords(){
        return selectedCords;
    }
    public void animationFinished(){
        //game over when ALL players/ships dead
        if(mapData.shipMap.getLivingCount() > 0){
            setGameState(GameStates.START_MOVE);
        }else{
            setGameState(GameStates.GAME_OVER);
            mCallbacks.showGameOverDialog();
        }
    }
    public void callLevelCompleteDialog(){
        int bestScore = mCallbacks.getLevelBestScore();
        boolean newHighScore = (bestScore<score);
        //current score is only stored in db if newHighScore is true
        mCallbacks.showLevelCompleteDialog(newHighScore, score);
    }
    public int getTurnCount(){
        return turnCount;
    }
    public void changeMineCount(int change){
        if(!(change<0) || mineCount >= 0){//this is an implies where (change>0) => (mineCount>=0)
            mineCount += change;
        }
        score = ScoreCalc.getScore(mineCount, turnCount);
        mCallbacks.updateCounts(mineCount, turnCount, score);
        System.out.println("mineCount: "+mineCount);
    }
    public void changeTurnCount(int change){
        turnCount += change;
        score = ScoreCalc.getScore(mineCount, turnCount);
        mCallbacks.updateCounts(mineCount, turnCount, score);
    }
    public int getMineCount(){
        return mineCount;
    }

    public void mineButtonPressed(){
    	//hack because UI doesn't account for multiple ships yet
    	//which is why getAllGo is OK
        Ship ship = (new ArrayList<Ship>(mapData.shipMap.getAll())).get(0);
        if(getGameState() == GameLogic.GameStates.MINE_MODE){
            setGameState(getLastGameState());
            ship.clearPossibleMoves();
        }else if(getGameState() == GameStates.SHIP_MOVED || getGameState() == GameStates.START_MOVE){
            setGameState(GameLogic.GameStates.MINE_MODE);
            ship.findPossibleMineMoves();
        }
    }
    public int getMapHeight(){
        return mapData.getMapHeight();
    }
    public int getMapWidth(){
        return mapData.getMapWidth();
    }
    public boolean endTurn(){
    	//for usability, this should be changed to work with other states
    	//will require "cleaning" of temp state stuff like move tiles
        if(getGameState() == GameStates.START_MOVE
        		||getGameState() == GameStates.SHIP_MOVED
        		||getGameState() == GameStates.END_MOVE){
            setGameState(GameStates.MOVE_COMPLETE);
            //algorithm will give each enemy list of cords to move to (for animating)
            mapData.enemyMoveAlgorithm();
            mapData.shipMap.newTurn();
            changeTurnCount(1);
            return true;
        }else{
            return false;
        }
    }
    
    //if gameObject other then enemies were to be animated
    //all the needs to be done:
    //	make sure they add steps for each square they move
    //	make sure this calls checkMoreMoves on them when in the correct state
    //	make sure draw(...) method below calldrawSeleves(..) on correct GameObjectMap
    public boolean checkMoreMoves(int stepNumber){
    	//VERY important this logic doesnt clash with that in the draw function
    	if(getGameState()==GameStates.MOVE_COMPLETE){
    		return mapData.enemyMap.checkMoreMoves(stepNumber);
    	}else{
    		return false; 
    	}
    }
    //The order of drawing matters!
    //first drawn will be covered up by later drawn
    
    //interStepNo being passed is currently used although it's only relevant to 1 type of GO at a time
    //so when moving enemies, only enemies make use of interStepNo, everything else uses drawSelvesNoAnimate
    //be careful when trying to animate 2 different go's simulatiouly
	public void draw(Canvas canvas, int interStepNo, float offsetAmount, RectF drawArea){
		mapData.tileMap.drawSelvesNoAnimate(canvas, drawArea);
		mapData.shipMap.drawSelvesNoAnimate(canvas, drawArea);
		mapData.mineMap.drawSelvesNoAnimate(canvas, drawArea);
		if(getGameState()==GameStates.MOVE_COMPLETE){
			mapData.enemyMap.drawSelves(canvas, interStepNo, offsetAmount, drawArea);
		}else{
			mapData.enemyMap.drawSelvesNoAnimate(canvas, drawArea);			
		}
		if(getGameState() == GameStates.MOVE_BEGUNG){
        	mapData.shipMap.get(selectedCords).drawShipMoves(canvas, drawArea);
        }else if(getGameState() == GameStates.MINE_MODE){
        	//cords hack, because UI only account for single ship at the moment
        	Cords shipCords = (new ArrayList<Ship>(mapData.shipMap.getAll())).get(0).getLatestCords();
        	mapData.shipMap.get(shipCords).drawMineMoves(canvas, drawArea);
        }
    }
}