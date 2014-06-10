package com.mehow.pirates.gameObjects.enemys;

import java.util.ArrayList;

import com.mehow.pirates.Cords;
import com.mehow.pirates.gameObjects.CordData;
import com.mehow.pirates.gameObjects.GameObject;
import com.mehow.pirates.gameObjects.Goal;
import com.mehow.pirates.gameObjects.Sea;

abstract public class Enemy extends GameObject{
    protected static int defNumOfMovesAllowed = 2;
    protected boolean moveLastTurn;//used to stop animation when not moving to dif cords
    protected int numOfMovesAllowed;
    protected int numOfMovesLeft;
    protected ArrayList<Cords> previousCords = new ArrayList<Cords>();

    //this is hackish, really should make use of a member var EnemyPathAlgs
    protected final Callbacks callbacks;
    
    public interface Callbacks {
        public CordData getInfoOnCords(Cords cord);
    }

    public Enemy(Cords startCords, int tempNumOfMovesAllowed, Callbacks tCallbacks) {
        super(startCords);
    	previousCords = new ArrayList<Cords>(0);
        numOfMovesAllowed = tempNumOfMovesAllowed;
        numOfMovesLeft = numOfMovesAllowed;
        callbacks = tCallbacks;
    }

    public Enemy(Cords startCords, Callbacks tCallbacks) {
        super(startCords);
    	previousCords = new ArrayList<Cords>(0);
        numOfMovesAllowed = defNumOfMovesAllowed;
        numOfMovesLeft = numOfMovesAllowed;
        callbacks = tCallbacks;
    }

    abstract public Cords computeMoveStep(Cords shipCords);
    
    //--------------------
    //used in child compute step functions
    protected Cords attemptXmove(Cords oldCords, int shipX) {
        if (shipX < oldCords.x && isValidMove(callbacks.getInfoOnCords(new Cords(oldCords.x-1, oldCords.y)))) {
            return new Cords(oldCords.x - 1, oldCords.y);
        } else if (shipX > oldCords.x && isValidMove(callbacks.getInfoOnCords(new Cords(oldCords.x+1, oldCords.y)))) {
            return new Cords(oldCords.x + 1, oldCords.y);
        } else {
            return oldCords;
        }
    }

    protected Cords attemptYmove(Cords oldCords, int shipY) {
        if (shipY < oldCords.y && isValidMove(callbacks.getInfoOnCords(new Cords(oldCords.x, oldCords.y-1)))) {
            return new Cords(oldCords.x, oldCords.y - 1);
        } else if (shipY > oldCords.y && isValidMove(callbacks.getInfoOnCords(new Cords(oldCords.x, oldCords.y+1)))) {
            return new Cords(oldCords.x, oldCords.y + 1);
        } else {
            return oldCords;
        }
    }

    //------------------------------------

    public static boolean isValidMove(CordData cordData) {
        boolean validTile = cordData.tile instanceof Sea || cordData.tile instanceof Goal;
        boolean notOccupied = cordData.enemy == null && cordData.mine == null;
        if (validTile && notOccupied) {
            return true;
        }else{
            return false;
        }
    }
    public int getNumOfMovesAllowed() {
        return numOfMovesAllowed;
    }

    public int getNumOfMovesLeft() {
        //	System.out.println("get numofmovesLeft: "+numOfMovesLeft);
        return numOfMovesLeft;
    }

    public void newTurn() {
        turnRecords.newTurn();
        resetMovesLeft();
    }
    public boolean canMakeMove(){
        return numOfMovesLeft >= 1;
    }

    public void makeStep(Cords cords){
        if(numOfMovesLeft >= 1){
            numOfMovesLeft -= 1;
            turnRecords.makeStep(cords);
        }else{
            throw new RuntimeException("Out of Moves");
        }
    }

    public boolean getMoveLastTurn() {
        return moveLastTurn;
    }

    public void resetMovesLeft() {
        numOfMovesLeft = numOfMovesAllowed;
    }
}
