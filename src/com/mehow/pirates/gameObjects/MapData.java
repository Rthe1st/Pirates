package com.mehow.pirates.gameObjects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

import android.os.Bundle;
import android.util.Log;

import com.mehow.pirates.Cords;
import com.mehow.pirates.GameObjectMove;
import com.mehow.pirates.Steps;
import com.mehow.pirates.gameObjects.enemys.Aenemy;
import com.mehow.pirates.gameObjects.enemys.Enemy;
import com.mehow.pirates.gameObjects.enemys.Henemy;
import com.mehow.pirates.gameObjects.enemys.PathEnemy;
import com.mehow.pirates.gameObjects.enemys.Venemy;

//replaced by Map for each type of object (enemey, tile, ship, etc) --(group enemies in one list?)
//this acts as a kind of implicit grid, query every map for a given cord value and whatever returns is what on the space

//based on 1 type of object per map, code assumes no 2 same classed object may occupy the same co-ordinate

//the ship map provides vague support for multiple player ships.
//however, this would require a user interface change
//(along the lines of: Click ship to select, then click button to alternate between mine mode/move mode
//current "mine mode" button straight of screws this by requireing ship cords to be queried
//so gameLogic and others have references to shipMap.getFirstKey etc

public class MapData implements PathAlgorithms.Callbacks, Enemy.Callbacks, Serializable {
	// only needd local to loading xml now?
	private int mapWidth;
	private int mapHeight;

	// instead of being public, create a gameObject enum, use as key in hashmap
	// with these
	// public HashMap<GameObjectTypes, TreeMap<Cords, GameObject>> gameObjects;
	// only drawback is having to cast for class specific stuff

	public HashMap<Class<? extends GameObject>, GameObjectMap<?>> mapMap;

	public MovementGameObjectMap<Enemy> enemyMap;

	public GameObjectMap<Tile> tileMap;

	public MovementGameObjectMap<Ship> shipMap;

	public GameObjectMap<Mine> mineMap;

	public Steps currentTurnSteps;

	// the compresion of steps into turns could cause problems
	// if gameobjects start doing complex inter-object shit when they step
	// if so, dont compress, just add Steps to a big vecotr<Steps> and step
	// through every step to undo
	public Stack<Vector<GameObjectMove<?>>> pastTurns;

	public MapData(String mapData) {
		enemyMap = new MovementGameObjectMap<Enemy>();
		tileMap = new GameObjectMap<Tile>();
		shipMap = new MovementGameObjectMap<Ship>();
		mineMap = new GameObjectMap<Mine>();
		mapMap = new HashMap<Class<? extends GameObject>, GameObjectMap<?>>();
		mapMap.put(Enemy.class, enemyMap);
		mapMap.put(Tile.class, tileMap);
		mapMap.put(Ship.class, shipMap);
		mapMap.put(Mine.class, mineMap);
		currentTurnSteps = new Steps();
		pastTurns = new Stack<Vector<GameObjectMove<?>>>();
		interpretMapData(mapData);
	}

	// split into undo turn and step because this is how gameLogic calls them
	public void undo() {
		if (currentTurnSteps.atTurnStart() && pastTurns.size() != 0) {
			undoMove(pastTurns.pop());
		} else if (!currentTurnSteps.atTurnStart()) {
			undoMove(currentTurnSteps.undoStep());
		}
	}

	protected void undoMove(Vector<GameObjectMove<?>> goMoves) {
		// Log.i("MapData")
		for (GameObjectMove<?> goMove : goMoves) {
			GameObjectMap<? extends GameObject> goMap = goMove.gameObjectMap;
			goMap.undoMove(goMove.getStartCords(), goMove.endCords,
					currentTurnSteps.atTurnStart());
		}
	}

	public <T extends GameObject> void killStep(GameObjectMap<T> goMap,
			Cords startCords) {
		currentTurnSteps.makeStep(goMap, goMap.get(startCords), startCords,
				null);
		goMap.kill(startCords);
	}
	
	public <T extends GameObject> void killSubStep(GameObjectMap<T> goMap,
			Cords startCords) {
		currentTurnSteps.makeSubStep(goMap, goMap.get(startCords), startCords,
				null);
		goMap.kill(startCords);
	}

	public <T extends GameObject> void makeMoveStep(GameObjectMap<T> goMap,
			Cords startCords, Cords endCords) {
		currentTurnSteps.makeStep(goMap, goMap.get(startCords), startCords,
				endCords);
		goMap.makeMove(startCords, endCords);
	}

	public <T extends GameObject> void makeSubStepMove(GameObjectMap<T> goMap,
			Cords startCords, Cords endCords) {
		currentTurnSteps.makeSubStep(goMap, goMap.get(startCords), startCords,
				endCords);
		goMap.makeMove(startCords, endCords);
	}

	public <T extends GameObject> void placeGameObjectStep(
			GameObjectMap<T> goMap, T gameObject, Cords endCords) {
		goMap.place(endCords, gameObject);
		currentTurnSteps.makeStep(goMap, gameObject, null, endCords);
	}

	public <T extends GameObject> void placeGameObjectSubStep(
			GameObjectMap<T> goMap, T gameObject, Cords endCords) {
		goMap.place(endCords, gameObject);
		currentTurnSteps.makeSubStep(goMap, gameObject, null, endCords);
	}

	public void newTurn() {
		pastTurns.add(currentTurnSteps.toTurn());
		// this is poor, try do some loop shit
		enemyMap.newTurn();
		shipMap.newTurn();
	}

	public void beginStep() {
		currentTurnSteps.beginStep();
	}

	public void endStep() {
		currentTurnSteps.endStep();
	}

	// looks for string in format row|row|row
	// row := tile,tile,...
	// tile := gameObjectCode:gameObjectCode:...
	private void interpretMapData(String mapData) {
		Log.i("MapData", "Interpreting map string: " + mapData);
		int x = 0;
		int y = 0;
		String gameObjectCode = "";
		int currentIndex = 0;
		try {
			for (int i = 0; i < mapData.length(); i++) {
				currentIndex = i;
				switch (mapData.charAt(i)) {
				case ':':// gameObject break
					interpretGameObjectCodes(new Cords(x, y), gameObjectCode);
					gameObjectCode = "";
					break;
				case ',':// column break
					interpretGameObjectCodes(new Cords(x, y), gameObjectCode);
					gameObjectCode = "";
					x += 1;
					break;
				case '|':// row break
					interpretGameObjectCodes(new Cords(x, y), gameObjectCode);
					gameObjectCode = "";
					x = 0;
					y += 1;
					break;
				default:
					gameObjectCode += mapData.charAt(i);
				}
			}
			// final object needs to be read
			interpretGameObjectCodes(new Cords(x, y), gameObjectCode);
			gameObjectCode = "";
		} catch (RuntimeException e) {
			Log.i("MapData",
					"interpreted up till: "
							+ mapData.substring(0, currentIndex));
			Log.i("MapData", "of: " + mapData);
			throw e;
		}
		// +1 because width and height aren't 0 based
		mapWidth = x + 1;
		mapHeight = y + 1;
	}

	private void interpretGameObjectCodes(Cords cords, String gameObjectCode) {
		//encdode value ends in gameObjectCode when the first [ appears
		//this allows gameObjects to be passed parameters
		Log.i("MapData", "gameObjectCode "+gameObjectCode);
		int indexOfParameterStart = gameObjectCode.indexOf("[");
		String encodeValue;
		String parameters;
		if(indexOfParameterStart != -1){
			encodeValue = gameObjectCode.substring(0, indexOfParameterStart);
			parameters = gameObjectCode.substring(indexOfParameterStart);			
		}else{
			encodeValue = gameObjectCode;
			parameters = "";
		}
		// to do: putin checks for bad maps, i.e. throw if a gameObject Map puts
		// 2 objects on the same key
		if (encodeValue.equals(Ship.ENCODE_VALUE)) {
			shipMap.put(cords, new Ship(cords, this));
			Log.i("MapData", "ship placed at:"+cords);
		} else if (encodeValue.equals(Venemy.ENCODE_VALUE)) {
			System.out.println("venemy added");
			enemyMap.put(cords, new Venemy(cords, (Enemy.Callbacks) this));
		} else if (encodeValue.equals(Henemy.ENCODE_VALUE)) {
			System.out.println("henemy added");
			enemyMap.put(cords, new Henemy(cords, (Enemy.Callbacks) this));
		} else if (encodeValue.equals(Aenemy.ENCODE_VALUE)) {
			System.out.println("aenemy added");
			enemyMap.put(cords, new Aenemy(cords, (Enemy.Callbacks) this));
		} else if (encodeValue.equals(Rock.ENCODE_VALUE)) {
			tileMap.put(cords, new Rock(cords));
		} else if (encodeValue.equals(Sea.ENCODE_VALUE)) {
			tileMap.put(cords, new Sea(cords));
			//Log.i("MapData", "Sea added");
		} else if (encodeValue.equals(Goal.ENCODE_VALUE)) {
			tileMap.put(cords, new Goal(cords));
		}else if(encodeValue.equals(PathEnemy.ENCODE_VALUE)){
			Log.i("MapData","Path enemy added");
			PathEnemy pathEnemy = new PathEnemy(cords, (Enemy.Callbacks) this, parameters);
			enemyMap.put(cords, pathEnemy);
		} else if (gameObjectCode.equals("")) {
			Log.i("MapData", "nothing added");
			// because empty tilees can appear on custom maps
		} else {
			throw new RuntimeException(
					"Unrecognised number as GameObjectCode when loading map xml "
							+ gameObjectCode.toString());
		}
	}

	// output formate matches input for
	// void interpretMapData(String mapData)
	public String encodeMapData() {
		Log.i("MapData", "mapWidth: " + mapWidth + " mapHeight: " + mapHeight);
		String encodedMap = "";
		encodedMap += encodeRow(0);
		for (int y = 1; y < mapHeight; y++) {
			encodedMap += "|";
			encodedMap += encodeRow(y);
		}
		Log.i("MapData", "Encoding map string: " + encodedMap);
		return encodedMap;
	}

	private String encodeRow(int y) {
		String encodedRow = "";
		encodedRow += encodeCords(0, y);
		for (int x = 1; x < mapWidth; x++) {
			encodedRow += ",";
			encodedRow += encodeCords(x, y);
		}
		return encodedRow;
	}

	// this features super hack reflection, to get static encode_value
	private String encodeCords(int x, int y) {
		String encodedCords = "";
		Cords cords = new Cords(x, y);
		// yet more evidence gameObject maps should be in an array/metamap
		GameObject[] cordsGameObjects = new GameObject[] { tileMap.get(cords),
				enemyMap.get(cords), shipMap.get(cords) };
		GameObject currentGameObject = cordsGameObjects[0];
		if (currentGameObject != null) {
			encodedCords += getGameObjectEncodeValue(currentGameObject);
		}
		for (int i = 1; i < cordsGameObjects.length; i++) {
			currentGameObject = cordsGameObjects[i];
			if (currentGameObject != null) {
				encodedCords += ":";
				encodedCords += getGameObjectEncodeValue(currentGameObject);
				encodedCords += currentGameObject.getEncodedParameters();
			}
		}
		return encodedCords;
	}

	public String getGameObjectEncodeValue(GameObject gameObject) {
		try {
			return (String) gameObject.getClass().getField("ENCODE_VALUE")
					.get(null);
		} catch (NoSuchFieldException e) {
			Log.i("MapEncoding",
					"GameObject did not possed a valid encode value field");
			e.printStackTrace();
			throw new RuntimeException(
					"gameObject did not possess an encode field "
							+ gameObject.toString());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("Encode field of "
					+ gameObject.toString() + "cannot be accessed");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException(
					"Illegal argument, trying to pass non-null object for static encode field?");
		}
	}

	public MapData(Bundle bundle) {
		loadState(bundle);
	}

	public void loadState(Bundle bundle) {
		// enemyMap = (GameObjectMap<Enemy>)
		enemyMap = (MovementGameObjectMap<Enemy>)bundle.getSerializable("ENEMY_MAP");
		tileMap = (GameObjectMap<Tile>) bundle.getSerializable("TILE_MAP");
		shipMap = (MovementGameObjectMap<Ship>) bundle.getSerializable("SHIP_MAP");
		mineMap = (GameObjectMap<Mine>) bundle.getSerializable("MINE_MAP");
		mapMap = (HashMap<Class<? extends GameObject>, GameObjectMap<?>>) bundle
				.getSerializable("MAP_MAP");
		mapWidth = bundle.getInt("MAP_WIDTH");
		mapHeight = bundle.getInt("MAP_HEIGHT");
	}

	public Bundle saveState() {
		Bundle bundle = new Bundle();
		bundle.putSerializable("ENEMY_MAP", enemyMap);
		bundle.putSerializable("SHIP_MAP", shipMap);
		bundle.putSerializable("MINE_MAP", mineMap);
		bundle.putSerializable("TILE_MAP", tileMap);
		bundle.putSerializable("MAP_MAP", mapMap);
		bundle.putInt("MAP_WIDTH", mapWidth);
		bundle.putInt("MAP_HEIGHT", mapHeight);
		return bundle;
	}

	// this should replace isSpaceOccupied, allowing "logic" to be down inside
	// object class
	// this will return nulls if key not present
	public CordData getInfoOnCords(Cords cords) {
		// this would be so much better if goMaps were in a list
		CordData cordData = new CordData(shipMap.get(cords),
				enemyMap.get(cords), tileMap.get(cords), mineMap.get(cords));
		return cordData;
	}

	// getters
	public int getMapWidth() {
		return mapWidth;
	}

	public int getMapHeight() {
		return mapHeight;
	}

	// setters
	public void setCordTile(Cords cord, Tile tile) {
		if (cord.x >= 0 && cord.y >= 0 && cord.x < getMapWidth()
				&& cord.y < getMapHeight()) {
			tileMap.put(cord, tile);
		} else {
			throw new IllegalArgumentException(
					"Tried setting a tile outside of map cord size");
		}
	}

	// should be function of ShipMap class extending GameObjectMap
	public void clearShipMoves() {
		for (Ship ship : shipMap.getAll()) {
			ship.clearPossibleMoves();
		}
	}
}
