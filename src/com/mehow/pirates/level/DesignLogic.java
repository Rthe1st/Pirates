package com.mehow.pirates.level;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Bundle;

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
import com.mehow.pirates.gameObjects.enemys.Aenemy;
import com.mehow.pirates.gameObjects.enemys.Enemy;
import com.mehow.pirates.gameObjects.enemys.Henemy;
import com.mehow.pirates.gameObjects.enemys.Venemy;

public class DesignLogic implements TileView.LogicCallbacks {

	public static final String BUNDLE_ID = "DESIGN_LOGIC";
	
	//hard to use because not all gameObjects have the same construtor parameters
	//although sub types tend to?
	//Constructor<? extends GameObject> placementConstructor;
	
	private LevelInfo levelInfo;
	
    public Callbacks mCallbacks;

    public interface Callbacks{
        public void updateScreen(boolean animate);
        public void showToast(String text);
    }
	
    //game var
    public MapData mapData;
    
    private Consts.GameObjectSuperTypes gameObjectSuperType = Consts.GameObjectSuperTypes.ENEMY;
    
    private Consts.GameObjectSubTypes gameObjectSubType = Consts.GameObjectSubTypes.VENEMY;
    
    private boolean deleteFlag;

    @SuppressWarnings("rawtypes")//because I cant get generics to accept a non-raw type into generic functions. Look this up
	private GameObjectMap selectedMap;
    
    private GameObject selectedConstructor; 
    
    //constructor when creating level for first time (i.e. no android lifecycle funnyness)
    public DesignLogic(Activity callBackActivity, LevelInfo tLevelInfo){
        if (!(callBackActivity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement DesignLogic.Callbacks.");
        }
        levelInfo = tLevelInfo;
        mCallbacks = (Callbacks)callBackActivity;
        mapData = new MapData(levelInfo.mapData);
        deleteFlag = false;
    }
    public DesignLogic(Activity callBackActivity, Bundle bundle){
    	if (!(callBackActivity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement DesignLogic.Callbacks.");
        }
    	if (!(callBackActivity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement GameLogic.Callbacks.");
        }
        mCallbacks = (Callbacks)callBackActivity;
        mapData = new MapData(bundle.getBundle("MAP_DATA"));
        loadSelf(bundle);
    }
    public Bundle saveState(){
        Bundle bundle = new Bundle();
        bundle.putBundle("MAP_DATA", mapData.saveState());
        bundle.putString("SUPER_MODE", gameObjectSuperType.toString());
        bundle.putString("SUB_MODE", gameObjectSubType.toString());
        bundle.putSerializable("LEVEL_INFO", levelInfo);
        return bundle;
    }
    private void loadSelf(Bundle bundle){
    	gameObjectSuperType = Consts.GameObjectSuperTypes.valueOf(bundle.getString("SUPER_MODE"));
    	gameObjectSubType = Consts.GameObjectSubTypes.valueOf(bundle.getString("SUB_MODE"));
        bundle.getString("SUB_MODE");
        levelInfo = (LevelInfo)bundle.getSerializable("LEVEL_INFO");
    }
    
    @Override
    public int getMapHeight(){
        return mapData.getMapHeight();
    }
    @Override
    public int getMapWidth(){
        return mapData.getMapWidth();
    }

    @Override
	public void onActionUp(Cords cords) {
    	if(deleteFlag){
    		deleteGameObject(cords);
    	}else{
    		tryAddGameObject(cords);
    	}
	}

    public void toggleDeleteFlag(){
    	deleteFlag = !deleteFlag;
    }
    
    private void deleteGameObject(Cords cords){
    	//aim was to delete thing in "view order"
    	//probably be better if this order was defined explicitly
    	//so it can be kept in sync with the animationlogic order
    	GameObjectMap<?>[] goMaps = {mapData.enemyMap, mapData.shipMap, mapData.tileMap};
    	for(GameObjectMap<?> goMap : goMaps){
    		if(goMap.get(cords) != null){
    			mapData.killStep(goMap, cords);
    			mCallbacks.updateScreen(false);
    			//break so only top object is deleted
    			break;
    		}
    	}
    }
    
    //reflectifying the enumns would get rid of a lot of switches
    private void tryAddGameObject(Cords cords){
		boolean legalPlacement;
		CordData cordData = mapData.getInfoOnCords(cords);
		switch(gameObjectSuperType){
		case ENEMY:
			legalPlacement = Enemy.isValidMove(cordData);
			if(legalPlacement){
				switch(gameObjectSubType){
				case AENEMY:
					mapData.beginStep();
					if(mapData.enemyMap.containsAt(cords)){
						mapData.killSubStep(mapData.enemyMap, cords);
					}
					mapData.placeGameObjectSubStep(mapData.enemyMap, new Aenemy(cords, mapData), cords);
					mapData.endStep();
					break;
				case HENEMY:
					mapData.beginStep();
					if(mapData.enemyMap.containsAt(cords)){
						mapData.makeSubStepMove(mapData.enemyMap, cords, null);
					}
					mapData.placeGameObjectSubStep(mapData.enemyMap, new Henemy(cords, mapData), cords);
					mapData.endStep();
					break;
				case VENEMY:
					mapData.beginStep();
					if(mapData.enemyMap.containsAt(cords)){
						mapData.makeSubStepMove(mapData.enemyMap, cords, null);
					}
					mapData.placeGameObjectSubStep(mapData.enemyMap, new Venemy(cords, mapData), cords);
					mapData.endStep();
					break;
				default:
					break;
				}
			}
			break;
		case SHIP:
			legalPlacement = Ship.isValidMove(cordData);
			if(legalPlacement){
				switch(gameObjectSubType){
				case SHIP:
					mapData.beginStep();
					if(mapData.shipMap.containsAt(cords)){
						mapData.makeSubStepMove(mapData.shipMap, cords, null);
					}
					mapData.placeGameObjectSubStep(mapData.shipMap, new Ship(cords, mapData), cords);
					mapData.endStep();
					break;
				default:
					break;
				}
			}
			break;
		case TILE:
			switch(gameObjectSubType){
			case SEA:
				legalPlacement = Sea.isValidMove(cordData);
				if(legalPlacement){
					mapData.beginStep();
					if(mapData.tileMap.containsAt(cords)){
					mapData.makeSubStepMove(mapData.tileMap, cords, null);
					}
					mapData.placeGameObjectSubStep(mapData.tileMap, new Sea(cords), cords);
					mapData.endStep();
				}
				break;
			case ROCK:
				legalPlacement = Rock.isValidMove(cordData);
				if(legalPlacement){
					mapData.beginStep();
					if(mapData.tileMap.containsAt(cords)){
						mapData.makeSubStepMove(mapData.tileMap, cords, null);
					}
					mapData.placeGameObjectSubStep(mapData.tileMap, new Rock(cords), cords);
					mapData.endStep();
				}
				break;
			case GOAL:
				legalPlacement = Goal.isValidMove(cordData);
				if(legalPlacement){
					mapData.beginStep();
					if(mapData.tileMap.containsAt(cords)){
					mapData.makeSubStepMove(mapData.tileMap, cords, null);
					}
					mapData.placeGameObjectSubStep(mapData.tileMap, new Goal(cords), cords);
					mapData.endStep();
				}
				break;
			default:
				legalPlacement = false;
			}
			break;
		default:
			legalPlacement = false;
		}
		if(legalPlacement){
			mCallbacks.updateScreen(false);
		}else{
			mCallbacks.showToast("placement illegal");			
		}    	
    }
	
	public void undo(){
		mapData.undo();
		mCallbacks.updateScreen(false);
	}

	@Override
	public void animationFinished() {
		//aniamtion never used
	}
	@Override
	public boolean checkMoreMoves(int stepNumber) {
		// animation never used
		return false;
	}

	public void setGameObjectSuperType(Consts.GameObjectSuperTypes superType){
		if(this.deleteFlag == true){
			this.toggleDeleteFlag();
		}
		gameObjectSuperType = superType;
		switch(superType){
		case ENEMY:
			selectedMap = mapData.enemyMap;
			break;
		case MINE:
			selectedMap = mapData.mineMap;
			break;
		case SHIP:
			selectedMap = mapData.shipMap;
			break;
		case TILE:
			selectedMap = mapData.tileMap;
			break;
		default:
			throw new RuntimeException("undefined super type");
		}
	}
	public void setGameObjectSubType(Consts.GameObjectSubTypes subType){
		if(this.deleteFlag == true){
			this.toggleDeleteFlag();
			
		}
		gameObjectSubType = subType;
	}
	
	@Override
	public void draw(Canvas canvas, int interStepNo, float offsetAmount,
			RectF drawArea) {
		mapData.tileMap.drawSelvesNoAnimate(canvas, drawArea);
		mapData.shipMap.drawSelvesNoAnimate(canvas, drawArea);
		mapData.mineMap.drawSelvesNoAnimate(canvas, drawArea);
		mapData.enemyMap.drawSelvesNoAnimate(canvas, drawArea);
	}
	
	public void updateLevelInfo(int bronze, int silver, int gold, String name,
			int mines){
		levelInfo.bronzeScore = bronze;
		levelInfo.silverScore = silver;
		levelInfo.goldScore = gold;
		levelInfo.name = name;
		levelInfo.mineLimit = mines;
	}
	
	public LevelInfo getLevelInfo(){
		//updateLevelInfo should of kept most info in sync
		//so just updates the actual map string
		levelInfo.mapData = mapData.encodeMapData();
		return levelInfo;
	}
}