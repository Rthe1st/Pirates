package com.mehow.pirates.gameObjects;

import java.util.ArrayList;

import android.os.Bundle;

import com.mehow.pirates.Cords;
import com.mehow.pirates.gameObjects.enemys.Aenemy;
import com.mehow.pirates.gameObjects.enemys.Enemy;
import com.mehow.pirates.gameObjects.enemys.Henemy;
import com.mehow.pirates.gameObjects.enemys.Venemy;

//replaced by Map for each type of object (enemey, tile, ship, etc) --(group enemies in one list?)
//this acts as a kind of implicit grid, query every map for a given cord value and whatever returns is what on the space

//based on 1 type of object per map, code assumes no 2 same classed object may occupy the same co-ordinate

//the ship map provides vague support for multiple player ships.
//however, this would require a user interface change
//(along the lines of: Click ship to select, then click button to alternate between mine mode/move mode
//current "mine mode" button straight of screws this by requireing ship cords to be queried
//so gameLogic and others have references to shipMap.getFirstKey etc

public class MapData implements PathAlgorithms.Callbacks {
	// only needd local to loading xml now?
	private int mapWidth;
	private int mapHeight;

	// instead of being public, create a gameObject enum, use as key in hashmap
	// with these
	// public HashMap<GameObjectTypes, TreeMap<Cords, GameObject>> gameObjects;
	// only drawback is having to cast for class specific stuff

	// int goTypeCount = 4;

	public GameObjectMap<Enemy> enemyMap;

	public GameObjectMap<Tile> tileMap;

	public GameObjectMap<Ship> shipMap;

	public GameObjectMap<Mine> mineMap;

	public MapData(String mapData) {
		enemyMap = new GameObjectMap<Enemy>();
		tileMap = new GameObjectMap<Tile>();
		shipMap = new GameObjectMap<Ship>();
		mineMap = new GameObjectMap<Mine>();
		interpretMapData(mapData);
	}
	
	//looks for string in format row|row|row
	//row := tile,tile,...
	//tile := gameObjectCode:gameObjectCode:...
	private void interpretMapData(String mapData){
		int x = 0;
		int y = 0;
		String gameObjectCode = "";
		for(int i=0;i<mapData.length();i++){
			switch(mapData.charAt(i)){
			case ':'://gameObject break
				interpretGameObjectCodes(new Cords(x,y), gameObjectCode);
				gameObjectCode = "";
				break;
			case ','://column break
				interpretGameObjectCodes(new Cords(x,y), gameObjectCode);
				gameObjectCode = "";
				x += 1;
				break;
			case '|'://row break
				interpretGameObjectCodes(new Cords(x,y), gameObjectCode);
				gameObjectCode = "";
				x = 0;
				y += 1;
				break;
			default:
				gameObjectCode += mapData.charAt(i);
			}
		}
		mapWidth = x;
		mapHeight = y;
	}

	// const for location in tileTypes of different bitmaps
	public static final String SEA_TILE = "0";
	public static final String ROCK_TILE = "1";
	public static final String SHIP_TILE = "2";
	public static final String MINE_TILE = "3";
	public static final String VENEMY_SHIP = "4";
	public static final String LEVEL_GOAL = "5";
	public static final String HENEMY_SHIP = "6";
	public static final String AENEMY_SHIP = "7";
	
	private void interpretGameObjectCodes(Cords cords, String gameObjectCode){
		//to do: putin checks for bad maps, i.e. throw if a gameObject Map puts 2 objects on the same key
		if(gameObjectCode.equals(SHIP_TILE)){
			shipMap.put(cords, new Ship(cords, this));
			//tileMap.put(cords, new SeaTile(cords));
		}else if(gameObjectCode.equals(VENEMY_SHIP)){
			System.out.println("venemy added");
			enemyMap.put(cords, new Venemy(cords,
					(PathAlgorithms.Callbacks) this));
			//tTileMap.put(cords, new SeaTile(cords));
		}else if(gameObjectCode.equals(HENEMY_SHIP)){
			System.out.println("henemy added");
			enemyMap.put(cords, new Henemy(cords,
					(PathAlgorithms.Callbacks) this));
			//tTileMap.put(cords, new SeaTile(cords));
		}else if(gameObjectCode.equals(AENEMY_SHIP)){
			System.out.println("aenemy added");
			enemyMap.put(cords, new Aenemy(cords,
					(PathAlgorithms.Callbacks) this));
			//tTileMap.put(cords, new SeaTile(cords));
		}else if(gameObjectCode.equals(ROCK_TILE)){
			tileMap.put(cords, new RockTile(cords));
		}else if(gameObjectCode.equals(SEA_TILE)){
			tileMap.put(cords, new SeaTile(cords));
		}else if(gameObjectCode.equals(LEVEL_GOAL)){
			tileMap.put(cords, new GoalTile(cords));
		}else{
			throw new RuntimeException(
					"Unrecognised number as GameObjectCode when loading map xml "+gameObjectCode);
		}
	}
	
	public MapData(Bundle bundle) {
		loadState(bundle);
	}

	public void loadState(Bundle bundle) {
		enemyMap = (GameObjectMap<Enemy>)bundle.getSerializable("ENEMY_MAP");
		tileMap = (GameObjectMap<Tile>)bundle.getSerializable("TILE_MAP");
		shipMap = (GameObjectMap<Ship>)bundle.getSerializable("SHIP_MAP");
		mineMap = (GameObjectMap<Mine>)bundle.getSerializable("MINE_MAP");
		mapWidth = bundle.getInt("MAP_WIDTH");
		mapHeight = bundle.getInt("MAP_HEIGHT");
	}

	public Bundle saveState() {
		Bundle bundle = new Bundle();
		bundle.putSerializable("ENEMY_MAP", enemyMap);
		bundle.putSerializable("SHIP_MAP", shipMap);
		bundle.putSerializable("MINE_MAP", mineMap);
		bundle.putSerializable("TILE_MAP", tileMap);
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

	// make sure each enemy:
	// starts turn
	// records each step
	// ends turn
	
	//MOVE INTO GAMELOGIC?
	public void enemyMoveAlgorithm() {
		boolean enemyMoved;
		boolean gameOver = false;
		ArrayList<Enemy> enemies = new ArrayList<Enemy>(enemyMap.getAll());
		// new turn must not take place between steps being added and steps
		// being animated
		// because animation code only read the latest turn
		for (Enemy enemy : enemies) {
			enemy.newTurn();
		}
		do {
			enemyMoved = false;
			for (Enemy enemy : enemies) {
				Cords oldCords = enemy.getLatestCords();
				if (enemy.canMakeMove()) {
					ArrayList<Ship> shipList = new ArrayList<Ship>(
							shipMap.getAll());
					Ship ship = shipList.get(0);// hack, theres only 1 player
												// ship for now
												// in future the enemy
												// computeMoveStep AI would take
												// a collection of ship cords as
												// args
					Cords newCords = enemy.computeMoveStep(ship
							.getLatestCords());
					enemyMap.makeStep(oldCords, newCords);
					if (shipMap.containsAt(newCords)) {
						shipMap.kill(newCords);
						if (shipMap.getLivingCount() == 0) {// replace with
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

	// should be function of ShipMap class extending GameObjectMap
	// or wouldn't be needed if multi-ships were implemented properly
	// as the ships cords to be cleared could be passed in
	public void clearShipMoves() {
		for (Ship ship : shipMap.getAll()) {
			ship.clearPossibleMoves();
		}
	}
}
