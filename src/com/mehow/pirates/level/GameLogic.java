package com.mehow.pirates.level;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.mehow.pirates.AnimationLogic;
import com.mehow.pirates.Cords;
import com.mehow.pirates.LevelInfo;
import com.mehow.pirates.animation.Effect;
import com.mehow.pirates.animation.ExplosionSequence;
import com.mehow.pirates.gameObjects.GameObject;
import com.mehow.pirates.gameObjects.GameObjectMap;
import com.mehow.pirates.gameObjects.Goal;
import com.mehow.pirates.gameObjects.MapData;
import com.mehow.pirates.gameObjects.Mine;
import com.mehow.pirates.gameObjects.ScoreCalc;
import com.mehow.pirates.gameObjects.Ship;
import com.mehow.pirates.gameObjects.Tile;
import com.mehow.pirates.gameObjects.enemys.Enemy;

/**
 * Created by User on 11/01/14.
 */
public class GameLogic implements TileView.LogicCallbacks {

	public Callbacks mCallbacks;

	public interface Callbacks {
		public void showLevelCompleteDialog(boolean setNewScore, int score);

		public void updateCounts(int mineChange, int turnChange, int scoreChange);

		public void changeMineBtnState(boolean state);

		public void changeMineButtonImage(GameStates state);

		public void showGameOverDialog();
	}

	public LevelInfo levelInfo;

	// game states
	// see flow chart for details
	public enum GameStates {
		MOVE_COMPLETE // attivate move complete when player presses "end go"
		, MOVE_MODE, MINE_MODE// the 2 main modes the user interacts with
		, GAME_OVER // when user flops
		, LEVEL_COMPLETE // when user anti-flops
	};

	private GameStates gameState;
	private GameStates lastGameState;
	// game var
	private int turnCount = 0;
	private int mineCount;
	private int score = 0;

	public MapData mapData;

	// used to store a previously touched cord for use in next touch.
	// I.E. stores where the ship is when selected, for use updating ship next
	// state when the cord to move ship to is chosen

	// access with set and release fucntions
	// private Cords selectedCords;
	private GameObject selectedGameObject;

	// enemy movement
	private final int numberOfStages = 5;
	private int offsetNo = 0;
	private int interStepNo = 0;
	private static int animationSpeed = 100;
	private int lastMoveAnimationTime = 0;

	//graphic effects
	//serilize mutha fukcer
	private Vector<Effect> effectSequences;
	
	private GameLogic(Activity callBackActivity){
		if (!(callBackActivity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement GameLogic.Callbacks.");
		}
		mCallbacks = (Callbacks) callBackActivity;		
	}

	// constructor when creating level for first time (ie no android lifecycle
	// funnyness)
	public GameLogic(Activity callBackActivity, LevelInfo tLevelInfo) {
		this(callBackActivity);
		levelInfo = tLevelInfo;
		mineCount = levelInfo.mineLimit;
		mapData = new MapData(levelInfo.mapData);
		gameState = GameStates.MOVE_MODE;
		effectSequences = new Vector<Effect>();
	}

	public GameLogic(Activity callBackActivity, Bundle bundle) {
		this(callBackActivity);
		// bundebundeldundle
		mapData = new MapData(bundle.getBundle("MAP_DATA"));
		loadSelf(bundle);
		effectSequences = new Vector<Effect>();

	}

	// store gamestates as strings to save shit yo
	// whats a clever way to make sure saveState and loadState are kept in sync?
	public Bundle saveState() {
		Bundle bundle = new Bundle();
		if (getLastGameState() != null) {
			bundle.putString("LAST_GAME_STATE", getLastGameState().toString());
		}
		bundle.putString("GAME_STATE", gameState.toString());
		bundle.putInt("TURN_COUNT", turnCount);
		bundle.putInt("MINE_COUNT", mineCount);
		bundle.putInt("SCORE", score);
		bundle.putBundle("MAP_DATA", mapData.saveState());
		bundle.putSerializable("LEVEL_INFO", levelInfo);
		return bundle;
	}

	private void loadSelf(Bundle bundle) {
		if (bundle.getString("LAST_GAME_STATE") != null) {
			lastGameState = GameStates.valueOf(bundle
					.getString("LAST_GAME_STATE"));
		}
		gameState = GameStates.valueOf(bundle.getString("GAME_STATE"));
		turnCount = bundle.getInt("TURN_COUNT");
		mineCount = bundle.getInt("MINE_COUNT");
		score = bundle.getInt("SCORE");
		setSelectedGameObject((Ship) bundle.getSerializable("SELECTED_SHIP"));
		levelInfo = (LevelInfo) bundle.getSerializable("LEVEL_INFO");
	}

	// Check GameState diagram file for explanation
	public void undo() {
		System.out.println("undoing with gameState: " + getGameState());
		if (getGameState() == GameStates.MOVE_MODE
				|| getGameState() == GameStates.MINE_MODE) {
			if (mapData.currentTurnSteps.atTurnStart()) {
				if (turnCount > 0) {
					mapData.undo();
					setGameState(GameStates.MOVE_MODE);
					changeTurnCount(-1);
				}
			} else {
				mapData.undo();
				// this hack breaks if a undo shouldn tchange gamestate
				setGameState(getLastGameState());
			}
		}
		if (getSelectedGameObject() != null) {
			if (getSelectedGameObject().getClass().equals(Ship.class)) {
				((Ship) getSelectedGameObject()).clearPossibleMoves();
			}
			setSelectedGameObject(null);
		}
	}

	public boolean trySetSelected(GameObject gameObject) {
		Log.i("GameLogic", "try set selected");
		boolean success = gameObject.trySelect(gameState);
		if (success)
			selectedGameObject = gameObject;
		return success;
	}

	public void onActionUp(Cords touchedCords) {
		System.out.println("orignl state: " + gameState);
		boolean updateRequired = false;
		if (gameState.equals(GameStates.MOVE_COMPLETE)) {
			return;
		}
		if (touchedCords.x < mapData.getMapWidth()
				&& touchedCords.y < mapData.getMapHeight()) {
			if (getSelectedGameObject() == null) {// if nothing is selected,
				if (mapData.shipMap.containsAt(touchedCords)) {
					updateRequired = trySetSelected(mapData.shipMap
							.get(touchedCords));
				}
			} else {
				selectedAction(touchedCords);
				updateRequired = true;
			}
		}
		Log.i("GameLogic", "end gamestate: " + getGameState());
	}

	public void selectedAction(Cords touchedCords) {
		Log.i("GameLogic", "selected action");
		if (getSelectedGameObject().getClass().equals(Ship.class)) {
			shipActionUp(touchedCords, (Ship) getSelectedGameObject(),
					gameState);
		}
	}

	public void shipActionUp(Cords touchedCords, Ship ship, GameStates gameState) {
		Log.i("GameLogic", "ship action up");
		if (gameState.equals(GameStates.MOVE_MODE)
				&& ship.canMove(touchedCords)) {
			moveShip(ship, touchedCords);
		} else if (gameState.equals(GameStates.MINE_MODE)
				&& ship.isInMineCords(touchedCords)) {
			placeMine(ship, touchedCords);
		} else {
			ship.clearPossibleMoves();
			this.setSelectedGameObject(null);
		}
	}

	private void placeMine(Ship selectedShip, Cords cords) {
		Mine mine = new Mine(cords, selectedShip);
		mapData.placeGameObjectStep(mapData.mineMap, mine, cords);
		selectedShip.layMine();
		changeMineCount(-1);
		selectedShip.clearPossibleMoves();
		// reset selected cord (as its now been "used")
		setSelectedGameObject(null);

	}

	private void moveShip(Ship selectedShip, Cords cords) {
		Log.i("GameLogic", "move ship");
		Log.i("GameLogic",
				"selected ship cords: " + selectedShip.getCurrentCords());
		mapData.makeMoveStep(mapData.shipMap, selectedShip.getCurrentCords(),
				cords);
		Tile postMoveTile = mapData.tileMap.get(cords);
		if (postMoveTile instanceof Goal) {
			changeTurnCount(1);
			setGameState(GameStates.LEVEL_COMPLETE);
			callLevelCompleteDialog();
		} else {
			this.mineButtonPressed();
			for (Ship ship : mapData.shipMap.getAll()) {
				boolean trySuccess = trySetSelected(ship);
				if (trySuccess) {
					// so only run on first ship successfully selected ship
					break;
				}
			}
		}
	}

	public void setGameState(GameStates state) {
		lastGameState = gameState;
		gameState = state;
		System.out.println("Game state transition: " + lastGameState + " to "
				+ gameState);
		mCallbacks.changeMineButtonImage(state);
	}

	public GameStates getGameState() {
		return gameState;
	}

	public GameStates getLastGameState() {
		return lastGameState;
	}

	public void setSelectedGameObject(GameObject gameObject) {
		selectedGameObject = gameObject;
	}

	public GameObject getSelectedGameObject() {
		return selectedGameObject;
	}

	public void animationFinished() {
		Log.i("GameLogic", "Animation finsihed");
		mapData.newTurn();
		changeTurnCount(1);
		// game over when ALL players/ships dead
		if (mapData.shipMap.getLivingCount() > 0) {
			setGameState(GameStates.MOVE_MODE);
			// autimaticly select a random ship
			// then auto select a ship, if any valid ones exist
			for (Ship ship : mapData.shipMap.getAll()) {
				boolean trySuccess = trySetSelected(ship);
				if (trySuccess) {
					break;// so only run on first ship successfully selected
							// ship
				}
			}
		} else {
			setGameState(GameStates.GAME_OVER);
			mCallbacks.showGameOverDialog();
		}
	}

	public void callLevelCompleteDialog() {
		boolean newHighScore = (levelInfo.bestScore < score);
		// current score is only stored in db if newHighScore is true
		mCallbacks.showLevelCompleteDialog(newHighScore, score);
	}

	public int getTurnCount() {
		return turnCount;
	}

	public void changeMineCount(int change) {
		if (!(change < 0) || mineCount >= 0) {// this is an implies where
												// (change>0) => (mineCount>=0)
			mineCount += change;
		} else {
			throw new RuntimeException(
					"changing mine count lead to invalid mineCount");
		}
		score = ScoreCalc.getScore(mineCount, turnCount);
		mCallbacks.updateCounts(mineCount, turnCount, score);
		System.out.println("mineCount: " + mineCount);
	}

	public void changeTurnCount(int change) {
		turnCount += change;
		score = ScoreCalc.getScore(mineCount, turnCount);
		mCallbacks.updateCounts(mineCount, turnCount, score);
	}

	public int getMineCount() {
		return mineCount;
	}

	// change to alternatie between movemode and minemode?
	public void mineButtonPressed() {
		if (getGameState() == GameLogic.GameStates.MINE_MODE) {
			setGameState(GameStates.MOVE_MODE);
			if (getSelectedGameObject() != null
					&& getSelectedGameObject().getClass().equals(Ship.class)) {
				Ship ship = (Ship) getSelectedGameObject();
				ship.clearPossibleMoves();
				if (ship.allowedToMove()) {
					ship.findPossibleMoves();
				}
			}
		} else {// in ship_moved or start_move
			setGameState(GameStates.MINE_MODE);
			if (getSelectedGameObject() != null
					&& getSelectedGameObject().getClass().equals(Ship.class)) {
				Ship ship = (Ship) getSelectedGameObject();
				ship.clearPossibleMoves();
				if (ship.allowedToMine()) {
					ship.findPossibleMineMoves();
				}
			}
		}
	}

	public int getMapHeight() {
		return mapData.getMapHeight();
	}

	public int getMapWidth() {
		return mapData.getMapWidth();
	}

	public void endTurn() {
		// for usability, this should be changed to work with other states
		// will require "cleaning" of temp state stuff like move tiles
		if (getGameState() == GameStates.MOVE_MODE
				|| getGameState() == GameStates.MINE_MODE) {
			setGameState(GameStates.MOVE_COMPLETE);
			if (getSelectedGameObject() != null
					&& getSelectedGameObject().getClass().equals(Ship.class)) {
				Ship ship = (Ship) getSelectedGameObject();
				ship.clearPossibleMoves();
				this.setSelectedGameObject(null);
			}
			// algorithm will give each enemy list of cords to move to (for
			// animating)
			enemyMoveAlgorithm();
			mCallbacks.changeMineBtnState(true);
		} else {
			System.out.println("not ending turn");
		}
	}

	// make sure each enemy:
	// starts turn
	// records each step
	// ends turn

	private void enemyMoveAlgorithm() {
		boolean enemyMoved;
		boolean gameOver = false;
		ArrayList<Enemy> enemies = new ArrayList<Enemy>(
				mapData.enemyMap.getAll());
		// new turn must not take place between steps being added and steps
		// being animated
		// because animation code only read the latest turn
		do {
			enemyMoved = false;
			for (Enemy enemy : enemies) {
				Cords oldCords = enemy.getCurrentCords();
				if (enemy.canMakeMove()) {
					ArrayList<Ship> shipList = new ArrayList<Ship>(
							mapData.shipMap.getAll());
					Ship ship = shipList.get(0);// hack, theres only 1 player
												// ship for now
												// in future the enemy
												// computeMoveStep AI would take
												// a collection of ship cords as
												// args
					Cords newCords = enemy.computeMoveStep(ship
							.getCurrentCords());
					mapData.makeMoveStep(mapData.enemyMap, oldCords, newCords);
					if (mapData.mineMap.containsAt(newCords)) {
						mapData.mineMap.kill(newCords);
						enemy.hitMine();
						this.effectSequences.add(new ExplosionSequence(enemy.getCurrentCords()));
					}
					if (mapData.shipMap.containsAt(newCords)) {
						mapData.shipMap.kill(newCords);
						if (mapData.shipMap.getLivingCount() == 0) {// replace
																	// with
							// gameLoigic
							// isGameOver
							// function?
							gameOver = true;
							break;
						}
					}
					enemyMoved = true;
				}
				// if enemies ever need to undo individual steps, fake steps
				// will need to be added here in an else clause
			}
		} while (enemyMoved == true && gameOver == false);
	}

	// if gameObject other then enemies were to be animated
	// all the needs to be done:
	// make sure they add steps for each square they move
	// make sure this calls checkMoreMoves on them when in the correct state
	// make sure draw(...) method below calldrawSeleves(..) on correct
	// GameObjectMap
	public boolean checkMoreMoves(int stepNumber) {
		// VERY important this logic doesnt clash with that in the draw function
		if (getGameState() == GameStates.MOVE_COMPLETE) {
			return mapData.enemyMap.checkMoreMoves(stepNumber);
		} else {
			return false;
		}
	}

	long timeSinceEnemyAnimation = 0;

	public void update(long time) {
		//Log.i("GameLogic", "update called");
		//Log.i("GameLogic", "time since last update:"+time);
		if (getGameState() == GameStates.MOVE_COMPLETE) {
			//Log.i("GameLogic", "animation enemy movement");
			timeSinceEnemyAnimation += time;
			Log.i("GameLogic", "timeSinceEnemyAnimation:"+timeSinceEnemyAnimation+" looping "+Math.floor(timeSinceEnemyAnimation/animationSpeed)+" times to keep up");
			while(timeSinceEnemyAnimation > this.animationSpeed){
				timeSinceEnemyAnimation -= animationSpeed;
				Log.i("GameLogic", "enemyanimation loop, offsetno:"+offsetNo+" interstepno:"+interStepNo);
				if (this.offsetNo == this.numberOfStages) {
					lastMoveAnimationTime = 0;
					this.offsetNo = 0;
					this.interStepNo += 1;
					if (!checkMoreMoves(interStepNo)) {
						interStepNo = 0;
						this.animationFinished();
					}
				}else{
					offsetNo += 1;
				}
			}
		}
		updateGos(mapData.enemyMap, time);
		updateGos(mapData.shipMap, time);
		updateGos(mapData.mineMap, time);
		updateGos(mapData.tileMap, time);
		Iterator<Effect> effectItorator = effectSequences.iterator();
		while(effectItorator.hasNext()){
			Effect effect = effectItorator.next();
			effect.update(time);
			if(effect.isFinished()==true){
				effectItorator.remove();
			}
		}
	}

	private <T extends GameObject> void  updateGos(GameObjectMap<T> goMap, long time){
		for(T go : goMap.getAll()){
			go.update(time);
		}
	}
	
	// The order of drawing matters!
	// first drawn will be covered up by later drawn

	// interStepNo being passed is currently used although it's only relevant to
	// 1 type of GO at a time
	// so when moving enemies, only enemies make use of interStepNo, everything
	// else uses drawSelvesNoAnimate
	// be careful when trying to animate 2 different go's simulatiouly
	@Override
	public void draw(Canvas canvas, RectF drawArea) {
		mapData.tileMap.drawSelvesNoAnimate(canvas, drawArea);
		mapData.shipMap.drawSelvesNoAnimate(canvas, drawArea);
		mapData.mineMap.drawSelvesNoAnimate(canvas, drawArea);
		if (getGameState() == GameStates.MOVE_COMPLETE) {
			//assumes square tiles
			double animationFrameDistance = (double) drawArea.width() / (double) this.numberOfStages;
			int offsetAmount = (int) Math.round((offsetNo)*animationFrameDistance);
			mapData.enemyMap.drawSelves(canvas, interStepNo, offsetAmount,
					drawArea);
		} else {
			mapData.enemyMap.drawSelvesNoAnimate(canvas, drawArea);
		}
		if (getSelectedGameObject() != null) {
			getSelectedGameObject().selectedDraw(canvas, drawArea);
		}
		Iterator<Effect> effectItorator = effectSequences.iterator();
		while(effectItorator.hasNext()){
			Effect effect = effectItorator.next();
	    	float xOffset = AnimationLogic.calculateCanvasOffset(effect.getX(), effect.getX(), 0, drawArea.width());
	    	float yOffset = AnimationLogic.calculateCanvasOffset(effect.getY(), effect.getY(), 0, drawArea.height());
	    	drawArea.offsetTo(xOffset, yOffset);
	    	Drawable drawable = effect.getCurrentFrame();
	    	drawable.setBounds(new Rect((int)drawArea.left, (int)drawArea.top, (int)drawArea.right, (int)drawArea.bottom));
	    	drawable.draw(canvas);
	    	//canvas.drawRect(drawArea, getSelfPaint());
		}
	}
}