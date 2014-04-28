package com.mehow.pirates.gameObjects;

import com.mehow.pirates.gameObjects.enemys.Enemy;

/**
 * Created by User on 15/02/14.
 */
public class CordData {
    //hard to keep in sync with hashmaps in mapdata, improve

    public final Ship ship;
    public final Enemy enemy;
    public final Tile tile;
    public final Mine mine;

    public CordData(Ship tShip, Enemy tEnemy, Tile tTile, Mine tMine){
        ship = tShip;
        enemy = tEnemy;
        tile = tTile;
        mine = tMine;
    }
}
