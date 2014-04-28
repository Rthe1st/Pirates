package com.mehow.pirates.gameObjects;

import java.util.ArrayList;
import java.util.TreeMap;

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

	private boolean isMapSet;

	// instead of being public, create a gameObject enum, use as key in hashmap
	// with these
	// public HashMap<GameObjectTypes, TreeMap<Cords, GameObject>> gameObjects;
	// only drawback is having to cast for class specific stuff

	// int goTypeCount = 4;

	public GameObjectMap<Enemy> enemyMap;

	public GameObjectMap<Tile> tileMap;

	public GameObjectMap<Ship> shipMap;

	public GameObjectMap<Mine> mineMap;

	public MapData() {
		// why is map init not here?
	}

	public MapData(Bundle bundle) {
		loadState(bundle);
	}

	public void loadState(Bundle bundle) {
		/*mapWidth = bundle.getInt("MAP_WIDTH");
		mapHeight = bundle.getInt("MAP_HEIGHT");
		mapData = inflateMap(bundle.getIntArray("FLAT_MAP"));
		ship.loadState(bundle.getBundle("SHIP"));
		mines.loadState(bundle.getBundle("MINES"));
		enemyContainer.inflateEnemies(bundle.getBundle("FLAT_ENEMIES"));
	*/
		enemyMap = (GameObjectMap<Enemy>)bundle.getSerializable("ENEMY_MAP");
		tileMap = (GameObjectMap<Tile>)bundle.getSerializable("TILE_MAP");
		shipMap = (GameObjectMap<Ship>)bundle.getSerializable("SHIP_MAP");
		mineMap = (GameObjectMap<Mine>)bundle.getSerializable("MINE_MAP");
		mapWidth = bundle.getInt("MAP_WIDTH");
		mapHeight = bundle.getInt("MAP_HEIGHT");
	}

	public Bundle saveState() {
		Bundle bundle = new Bundle();
		/*
		 * bundle.putIntArray("FLAT_MAP", flattenMap());
		 * bundle.putBundle("SHIP", ship.shipSaveState());
		 * bundle.putInt("MAP_WIDTH", mapWidth); bundle.putInt("MAP_HEIGHT",
		 * mapHeight); bundle.putBundle("MINES",mines.saveState());
		 * bundle.putBundle("FLAT_ENEMIES", enemyContainer.flattenEnemies());
		 */
		bundle.putSerializable("ENEMY_MAP", enemyMap);
		bundle.putSerializable("SHIP_MAP", shipMap);
		bundle.putSerializable("MINE_MAP", mineMap);
		bundle.putSerializable("TILE_MAP", tileMap);
		bundle.putInt("MAP_WIDTH", mapWidth);
		bundle.putInt("MAP_HEIGHT", mapHeight);
		return bundle;
	}

	public void setupBlankMap(int[] levelSize) {
		mapWidth = levelSize[0];
		mapHeight = levelSize[1];
		isMapSet = true;
		// mapData = new int[mapWidth][mapHeight];
	}

	public void setUpSavedMap(int[] flatMap) {

	}

	public void setupXMLMap(int[] levelSize, String[] levelData) {
		mapWidth = levelSize[0];
		mapHeight = levelSize[1];
		isMapSet = true;
		// mapData = new int[mapWidth][mapHeight];
		loadXMLmap(levelData);
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

	public boolean isMapSet() {
		return isMapSet;
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

	// const for location in tileTypes of different bitmaps
	public static final int SEA_TILE = 0;
	public static final int ROCK_TILE = 1;
	public static final int SHIP_TILE = 2;
	public static final int MINE_TILE = 3;
	public static final int VENEMY_SHIP = 4;
	public static final int LEVEL_GOAL = 5;
	public static final int HENEMY_SHIP = 6;
	public static final int AENEMY_SHIP = 7;

	// breaks if wrong map size in xml
	// also stick in stuff for bad maps (<1 ship extra)
	// little weird because x and y are flipped to make designing maps easier
	public void loadXMLmap(String[] levelData) {
		// set up all object lists
		TreeMap<Cords, Tile> tTileMap = new TreeMap<Cords, Tile>();
		TreeMap<Cords, Enemy> tEnemyMap = new TreeMap<Cords, Enemy>();
		TreeMap<Cords, Ship> tShipMap = new TreeMap<Cords, Ship>();
		TreeMap<Cords, Mine> tMineMap = new TreeMap<Cords, Mine>();
		System.out.println("xml loading");
		String[] columnData;
		int tileType;
		for (int y = 0; y < getMapWidth(); y++) {
			columnData = levelData[y].split(",");
			for (int x = 0; x < getMapHeight(); x++) {
				Cords cords = new Cords(x, y);
				tileType = Integer.parseInt(columnData[x]);
				switch (tileType) {
				case SHIP_TILE:
					tShipMap.put(cords, new Ship(cords, this));
					tTileMap.put(cords, new SeaTile(cords));
					break;
				case VENEMY_SHIP:
					System.out.println("venemy added");
					tEnemyMap.put(cords, new Venemy(cords,
							(PathAlgorithms.Callbacks) this));
					tTileMap.put(cords, new SeaTile(cords));
					break;
				case HENEMY_SHIP:
					System.out.println("henemy added");
					tEnemyMap.put(cords, new Henemy(cords,
							(PathAlgorithms.Callbacks) this));
					tTileMap.put(cords, new SeaTile(cords));
					break;
				case AENEMY_SHIP:
					System.out.println("aenemy added");
					tEnemyMap.put(cords, new Aenemy(cords,
							(PathAlgorithms.Callbacks) this));
					tTileMap.put(cords, new SeaTile(cords));
					break;
				case ROCK_TILE:
					tTileMap.put(cords, new RockTile(cords));
					break;
				case SEA_TILE:
					tTileMap.put(cords, new SeaTile(cords));
					break;
				case LEVEL_GOAL:
					tTileMap.put(cords, new GoalTile(cords));
					break;
				default:
					throw new RuntimeException(
							"Unrecognised number as GameObject when loading map xml");
				}
			}
		}
		tileMap = new GameObjectMap<Tile>(tTileMap);
		enemyMap = new GameObjectMap<Enemy>(tEnemyMap);
		mineMap = new GameObjectMap<Mine>(tMineMap);
		shipMap = new GameObjectMap<Ship>(tShipMap);
	}

	// make sure each enemy:
	// starts turn
	// records each step
	// ends turn
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
