package com.mehow.pirates.gameObjects.enemys;

import java.util.ArrayList;

import android.os.Bundle;

import com.mehow.pirates.Cords;
import com.mehow.pirates.gameObjects.CordData;
import com.mehow.pirates.gameObjects.GameObject;
import com.mehow.pirates.gameObjects.GoalTile;
import com.mehow.pirates.gameObjects.PathAlgorithms;
import com.mehow.pirates.gameObjects.SeaTile;

abstract public class Enemy extends GameObject{
    protected static int defNumOfMovesAllowed = 2;
    protected boolean moveLastTurn;//used to stop animation when not moving to dif cords
    protected int numOfMovesAllowed;
    protected int numOfMovesLeft;
    protected ArrayList<Cords> previousCords = new ArrayList<Cords>();

    //this is hackish, really should make use of a member var EnemyPathAlgs
    protected final PathAlgorithms.Callbacks callbacks;

    public Enemy(Cords startCords, int tempNumOfMovesAllowed, PathAlgorithms.Callbacks tCallbacks) {
        super(startCords);
    	previousCords = new ArrayList<Cords>(0);
        numOfMovesAllowed = tempNumOfMovesAllowed;
        numOfMovesLeft = numOfMovesAllowed;
        callbacks = tCallbacks;
    }

    public Enemy(Cords startCords, PathAlgorithms.Callbacks tCallbacks) {
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
        if (shipX < oldCords.x && isValidMove(new Cords(oldCords.x - 1, oldCords.y), callbacks)) {
            return new Cords(oldCords.x - 1, oldCords.y);
        } else if (shipX > oldCords.x && isValidMove(new Cords(oldCords.x + 1, oldCords.y), callbacks)) {
            return new Cords(oldCords.x + 1, oldCords.y);
        } else {
            return oldCords;
        }
    }

    protected Cords attemptYmove(Cords oldCords, int shipY) {
        if (shipY < oldCords.y && isValidMove(new Cords(oldCords.x, oldCords.y - 1), callbacks)) {
            return new Cords(oldCords.x, oldCords.y - 1);
        } else if (shipY > oldCords.y && isValidMove(new Cords(oldCords.x, oldCords.y + 1), callbacks)) {
            return new Cords(oldCords.x, oldCords.y + 1);
        } else {
            return oldCords;
        }
    }

    //------------------------------------

    protected static boolean isValidMove(Cords cords, PathAlgorithms.Callbacks callbacks) {
        CordData cordData = callbacks.getInfoOnCords(cords);
        if (cordData.enemy != null
                || cordData.mine != null) {
            return false;
        } else if (cordData.tile.getClass().equals(SeaTile.class)) {
            return true;
        } else if (cordData.tile instanceof GoalTile) {
            return true;
        } else {
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
    
    public Bundle saveState() {
        Bundle bundle = new Bundle();
        bundle.putInt("CUR_X", turnRecords.getStartCords().x);//this could be wrong
        bundle.putInt("CUR_Y", turnRecords.getStartCords().y);
        bundle.putInt("TURNS_LEFT", numOfMovesLeft);
        bundle.putBundle("PREVIOUS_CORDS", flattenPrevCords());
        return bundle;
    }

    public void loadState(Bundle bundle) {
        //currentCords = new Cords(bundle.getInt("CUR_X"), bundle.getInt("CUR_Y"));
        numOfMovesLeft = bundle.getInt("TURNS_LEFT");
        inflatePrevCords(bundle.getBundle("PREVIOUS_CORDS"));
    }

    private Bundle flattenPrevCords() {
        Bundle bundle = new Bundle();
        bundle.putInt("PREV_CORDS_SIZE", previousCords.size());
        for (int i = 0; i < previousCords.size(); i++) {
            bundle.putInt("TURN_" + i + "_X", previousCords.get(i).x);
            bundle.putInt("TURN_" + i + "_Y", previousCords.get(i).y);
        }
        return bundle;
    }

    private void inflatePrevCords(Bundle bundle) {
        int previousCordsSize = bundle.getInt("PREV_CORDS_SIZE");
        for (int i = 0; i < previousCordsSize; i++) {
            previousCords.add(new Cords(bundle.getInt("TURN_" + i + "_X"), bundle.getInt("TURN_" + i + "_Y")));
        }
    }
}
