package com.mehow.pirates.gameObjects;

import java.io.Serializable;
import java.util.ArrayList;

import android.os.Bundle;

import com.mehow.pirates.Cords;

public abstract class PathAlgorithms implements Serializable{

    public interface Callbacks{
        public int getMapHeight();
        public int getMapWidth();
        public CordData getInfoOnCords(Cords cord);
    }
    protected final Callbacks callbacks;

    //class used to evaulate/animate player moves
    protected ArrayList<Cords> storedCords = new ArrayList<Cords>(0);

    public PathAlgorithms(Callbacks tCallbacks){
        callbacks = tCallbacks;
    }

	//abstracts
	//this would be abstract, except abstract methods can't be static
	//so this should always be over-written
    abstract void computePossibleMoves(Cords cords,int range);

	public Bundle pathAlgSaveState(){
		Bundle bundle = new Bundle();
		bundle.putIntArray("STORED_CORDS",flatten(storedCords));
		return bundle;
	}
	public void loadState(Bundle bundle){
		storedCords = inflate(bundle.getIntArray("STORED_CORDS"));
	}
	//get
	public Cords getStoredCords(int index){
		return storedCords.get(index);
	}
	public int getStoredCordsSize(){
		return storedCords.size();
	}
	public ArrayList<Cords> getAllCords(){
		return storedCords;//this should really make a copy
	}
    //used to display possible player movement
    //simulates every possible move, but stores resulting tiles without duplication.
    //this may be very ineffecent
    //also better if returns result rather then sending straight to static array
    //also better if (maybe by splitting to 2 functions), start space not included
    //if split into 2 functions, all duplicate checking can be done in initiator function
    public ArrayList<Cords> findPossibleMoves(Cords cords, int range){
    	storedCords = new ArrayList<Cords>();
    	//could have if in computePoss but would be inefficent
    	quadDirectCompute(cords, range);
    	return storedCords;
    }
    public ArrayList<Cords> getPossibleMoves(){
        return storedCords;
    }
    //------------------------------
    protected int[] flatten(ArrayList<Cords> notFlat){
    	int[] flatData = new int[notFlat.size()*2];
    	for(int i=0; i<notFlat.size(); i++){
    		flatData[i*2] = notFlat.get(i).x;
    		flatData[(i*2)+1] = notFlat.get(i).y;
    	}
    	return flatData;
    }
    protected ArrayList<Cords> inflate(int[] flatData){
    	ArrayList<Cords> inflatted = new ArrayList<Cords>();
    	for(int i=0; i<flatData.length;i+=2){
    		inflatted.add(new Cords(flatData[i], flatData[i+1]));
    	}
    	return inflatted;
    }
    protected void quadDirectCompute(Cords cords, int range){
		if(cords.y-1 >= 0){
			computePossibleMoves(new Cords(cords.x,cords.y-1), range);
		}
		if(cords.x-1 >= 0){
			computePossibleMoves(new Cords(cords.x-1,cords.y), range);
		}
		if(cords.y+1 < callbacks.getMapHeight()){
			computePossibleMoves(new Cords(cords.x,cords.y+1), range);
		}
		if(cords.x+1 < callbacks.getMapWidth()){
			computePossibleMoves(new Cords(cords.x+1,cords.y), range);
		}
    }
    public boolean contains(Cords cord){
    	for(int index = 0; index< storedCords.size(); index++){
    		if(cord.equals(storedCords.get(index))){
    			return true;
    		}
    	}
    	return false;
    }
    /*protected void findPath(Cords startCords, Cords endCords, int range){
    	ArrayList<Cords> pathList = new ArrayList<Cords>();
    }*/
    public void clear(){
        storedCords.clear();
    }
}
