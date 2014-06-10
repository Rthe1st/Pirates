package com.mehow.pirates.gameObjects;


import com.mehow.pirates.Cords;

public class ShipPathAlgs extends PathAlgorithms{

    public ShipPathAlgs(Callbacks tCallbacks){
        super(tCallbacks);
    }

	protected void computePossibleMoves(Cords cords,int range){
        CordData cordData = callbacks.getInfoOnCords(cords);
    	if(range > 0){
    		if(Ship.isValidMove(cordData)){
    			if(!contains(cords)){
					storedCords.add(cords);
    			}
    			quadDirectCompute(cords, range-1);
   			}
    	}
    }
}
