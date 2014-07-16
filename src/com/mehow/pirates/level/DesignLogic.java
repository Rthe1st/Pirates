package com.mehow.pirates.level;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable.Callback;
import android.os.Bundle;
import android.util.Log;

import com.mehow.pirates.Consts;
import com.mehow.pirates.Cords;
import com.mehow.pirates.LevelInfo;
import com.mehow.pirates.gameObjects.CordData;
import com.mehow.pirates.gameObjects.GameObject;
import com.mehow.pirates.gameObjects.GameObjectMap;
import com.mehow.pirates.gameObjects.Goal;
import com.mehow.pirates.gameObjects.MapData;
import com.mehow.pirates.gameObjects.Rock;
import com.mehow.pirates.gameObjects.Sea;
import com.mehow.pirates.gameObjects.Ship;
import com.mehow.pirates.gameObjects.Tile;
import com.mehow.pirates.gameObjects.enemys.Aenemy;
import com.mehow.pirates.gameObjects.enemys.Enemy;
import com.mehow.pirates.gameObjects.enemys.Henemy;
import com.mehow.pirates.gameObjects.enemys.PathEnemy;
import com.mehow.pirates.gameObjects.enemys.Venemy;

public class DesignLogic implements TileView.LogicCallbacks {

	public static final String BUNDLE_ID = "DESIGN_LOGIC";

	// hard to use because not all gameObjects have the same construtor
	// parameters
	// although sub types tend to?
	// Constructor<? extends GameObject> placementConstructor;

	private LevelInfo levelInfo;

	public Callbacks mCallbacks;

	public interface Callbacks {
		public void updateScreen(boolean animate);

		public void showToast(String text);
	}

	// game var
	public MapData mapData;

	private Consts.DesignModeSuperTypes designModeSuperType = Consts.DesignModeSuperTypes.ENEMY;

	private Consts.DesignModeSubTypes designModeSubType = Consts.DesignModeSubTypes.VENEMY;

	// replace with an enum?
	private boolean deleteFlag;
	private boolean selectFlag;

	private GameObject selectedGameObject;

	/*
	 * reflection not used atm
	 * 
	 * @SuppressWarnings("rawtypes") // because I cant get generics to accept a
	 * non-raw type into generic // functions. Look this up private
	 * GameObjectMap selectedMap;
	 * 
	 * private GameObject selectedConstructor;
	 */
	// constructor when creating level for first time (i.e. no android lifecycle
	// funnyness)
	public DesignLogic(Activity callBackActivity, LevelInfo tLevelInfo) {
		if (!(callBackActivity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement DesignLogic.Callbacks.");
		}
		levelInfo = tLevelInfo;
		mCallbacks = (Callbacks) callBackActivity;
		mapData = new MapData(levelInfo.mapData);
		deleteFlag = false;
	}

	public DesignLogic(Activity callBackActivity, Bundle bundle) {
		if (!(callBackActivity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement DesignLogic.Callbacks.");
		}
		if (!(callBackActivity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement GameLogic.Callbacks.");
		}
		mCallbacks = (Callbacks) callBackActivity;
		mapData = new MapData(bundle.getBundle("MAP_DATA"));
		loadSelf(bundle);
	}

	public Bundle saveState() {
		Bundle bundle = new Bundle();
		bundle.putBundle("MAP_DATA", mapData.saveState());
		bundle.putString("SUPER_MODE", designModeSuperType.toString());
		if(designModeSubType != null){
			bundle.putString("SUB_MODE", designModeSubType.toString());
		}
		bundle.putSerializable("LEVEL_INFO", levelInfo);
		return bundle;
	}

	private void loadSelf(Bundle bundle) {
		designModeSuperType = Consts.DesignModeSuperTypes.valueOf(bundle
				.getString("SUPER_MODE"));
		if(bundle.getString("SUB_MODE") != null){
			designModeSubType = Consts.DesignModeSubTypes.valueOf(bundle
				.getString("SUB_MODE"));
		}
		levelInfo = (LevelInfo) bundle.getSerializable("LEVEL_INFO");
	}

	@Override
	public int getMapHeight() {
		return mapData.getMapHeight();
	}

	@Override
	public int getMapWidth() {
		return mapData.getMapWidth();
	}

	@Override
	public void onActionUp(Cords touchedCords) {
		if (this.designModeSuperType.equals(Consts.DesignModeSuperTypes.DELETE)) {
			deleteGameObject(touchedCords);
		} else if (this.designModeSuperType
				.equals(Consts.DesignModeSuperTypes.SELECT)) {
			Log.i("DesignLogic", "select mode stuff");
			if (selectedGameObject == null) {
				Log.i("DesignLogic", "no selected object, selecting");				
				selectgameObject(touchedCords);
			} else {
				selectedAction(touchedCords);
				Log.i("DesignLogic", "selected object already, selected action begung");
			}
		} else {
			tryAddGameObject(touchedCords);
		}
		mCallbacks.updateScreen(false);
	}

	private void deleteGameObject(Cords cords) {
		// aim was to delete thing in "view order"
		// probably be better if this order was defined explicitly
		// so it can be kept in sync with the animationlogic order
		GameObjectMap<?>[] goMaps = { mapData.enemyMap, mapData.shipMap,
				mapData.tileMap };
		for (GameObjectMap<?> goMap : goMaps) {
			if (goMap.get(cords) != null) {
				mapData.killStep(goMap, cords);
				mCallbacks.updateScreen(false);
				// break so only top object is deleted
				break;
			}
		}
	}

	private void selectgameObject(Cords touchedCords) {
		if (touchedCords.x < mapData.getMapWidth()
				&& touchedCords.y < mapData.getMapHeight()) {
			if (mapData.shipMap.containsAt(touchedCords)) {
				selectedGameObject = mapData.shipMap.get(touchedCords);
				mapData.shipMap.get(touchedCords).findPossibleMoves();
				mapData.shipMap.get(touchedCords).setSelfPaint(Ship.selectedPaint);
			}else if(mapData.enemyMap.containsAt(touchedCords)){
				selectedGameObject = mapData.enemyMap.get(touchedCords);
			}else{
				//tile or some shit
			}
		}
	}
	
	private void selectedAction(Cords touchedCords){
		if(selectedGameObject.getClass().equals(PathEnemy.class)){
			if(selectedGameObject == mapData.enemyMap.get(touchedCords)){
				deselectGameObject();			
			}else{
				PathEnemy pathEnemy = (PathEnemy)selectedGameObject;
				pathEnemy.changeMoveCords(touchedCords);
			}
		}else{
			deselectGameObject();
		}
	}
	
	private void deselectGameObject(){
		assert(selectedGameObject != null);
		selectedGameObject.setSelfPaint(Consts.stdPaint);
		selectedGameObject = null;
	}

	// reflectifying the enumns would get rid of a lot of switches
	// or making a facotory object and passing it in for each type
	private void tryAddGameObject(Cords cords) {
		//quick and dirty, caters for when user switches super mode, then doesnt select a sub type
		if(this.designModeSubType == null){
			return;
		}
		boolean legalPlacement;
		CordData cordData = mapData.getInfoOnCords(cords);
		switch (designModeSuperType) {
		case ENEMY:
			legalPlacement = tryAddEnemy(cordData, cords);
			break;
		case SHIP:
			legalPlacement = tryAddShip(cordData, cords);
			break;
		case TILE:
			legalPlacement = tryAddTile(cordData, cords);
			break;
		default:
			throw new RuntimeException("Super type not found");
		}
		if (legalPlacement) {
			mCallbacks.updateScreen(false);
		} else {
			mCallbacks.showToast("placement illegal");
		}
	}

	private boolean tryAddEnemy(CordData cordData, Cords cords) {
		Enemy enemy = null;
		//&& no player ship, because valid move allows players for game mode
		boolean legalPlacement = Enemy.isValidMove(cordData) && !mapData.shipMap.containsAt(cords);
		if (legalPlacement) {
			switch (designModeSubType) {
			case AENEMY:
				enemy = new Aenemy(cords, mapData);
				break;
			case HENEMY:
				enemy = new Henemy(cords, mapData);
				break;
			case VENEMY:
				enemy = new Venemy(cords, mapData);
				break;
			case PATHENEMY:
				enemy = new PathEnemy(cords, mapData);
				break;
			default:
				throw new RuntimeException("Enemy type no found");
			}
			placeGameObject(cords, mapData.enemyMap, enemy);
		}
		return legalPlacement;
	}

	private boolean tryAddShip(CordData cordData, Cords cords) {
		boolean legalPlacement = Ship.isValidMove(cordData);
		if (legalPlacement) {
			Ship ship = null;
			switch (designModeSubType) {
			case SHIP:
				ship = new Ship(cords, mapData);
				break;
			default:
				throw new RuntimeException("Ship type no found");
			}
			placeGameObject(cords, mapData.shipMap, ship);
		}
		return legalPlacement;
	}

	private boolean tryAddTile(CordData cordData, Cords cords) {
		Tile tile = null;
		boolean legalPlacement;
		switch (designModeSubType) {
		case SEA:
			legalPlacement = Sea.isValidMove(cordData);
			if (legalPlacement) {
				tile = new Sea(cords);
			}
			break;
		case ROCK:
			legalPlacement = Rock.isValidMove(cordData);
			if (legalPlacement) {
				tile = new Rock(cords);
			}
			break;
		case GOAL:
			legalPlacement = Goal.isValidMove(cordData);
			if (legalPlacement) {
				tile = new Goal(cords);
			}
			break;
		default:
			throw new RuntimeException("tile type "+designModeSubType.toString()+" not found");
		}
		if (legalPlacement) {
			placeGameObject(cords, mapData.tileMap, tile);
		}
		return legalPlacement;
	}

	private <T extends GameObject> void placeGameObject(Cords cords,
			GameObjectMap<T> gameObjectMap, T gameObject) {
		mapData.beginStep();
		if (gameObjectMap.containsAt(cords)) {
			mapData.killSubStep(gameObjectMap, cords);
		}
		mapData.placeGameObjectSubStep(gameObjectMap, gameObject, cords);
		mapData.endStep();
	}

	public void undo() {
		mapData.undo();
		mCallbacks.updateScreen(false);
	}

	@Override
	public void animationFinished() {
		// aniamtion never used
	}

	@Override
	public boolean checkMoreMoves(int stepNumber) {
		// animation never used
		return false;
	}

	public void setGameObjectSuperType(Consts.DesignModeSuperTypes superType) {
		designModeSuperType = superType;
		designModeSubType = null;
		if(designModeSuperType != Consts.DesignModeSuperTypes.SELECT && selectedGameObject != null){
			deselectGameObject();
			this.mCallbacks.updateScreen(false);
		}
		/*
		 * switch (superType) { case ENEMY: selectedMap = mapData.enemyMap;
		 * break; case MINE: selectedMap = mapData.mineMap; break; case SHIP:
		 * selectedMap = mapData.shipMap; break; case TILE: selectedMap =
		 * mapData.tileMap; break; case DELETE: break; case SELECT: break;
		 * default: throw new RuntimeException("undefined super type"); }
		 */
	}

	public void setGameObjectSubType(Consts.DesignModeSubTypes subType) {
		designModeSubType = subType;
	}

	@Override
	public void draw(Canvas canvas, int interStepNo, float offsetAmount,
			RectF drawArea) {
		mapData.tileMap.drawSelvesNoAnimate(canvas, drawArea);
		mapData.shipMap.drawSelvesNoAnimate(canvas, drawArea);
		mapData.mineMap.drawSelvesNoAnimate(canvas, drawArea);
		mapData.enemyMap.drawSelvesNoAnimate(canvas, drawArea);
		if(selectedGameObject != null){
			selectedGameObject.selectedDraw(canvas, drawArea);
		}
	}

	public void updateLevelInfo(int bronze, int silver, int gold, String name,
			int mines) {
		levelInfo.bronzeScore = bronze;
		levelInfo.silverScore = silver;
		levelInfo.goldScore = gold;
		levelInfo.name = name;
		levelInfo.mineLimit = mines;
	}

	public LevelInfo getLevelInfo() {
		// updateLevelInfo should of kept most info in sync
		// so just updates the actual map string
		levelInfo.mapData = mapData.encodeMapData();
		return levelInfo;
	}
}