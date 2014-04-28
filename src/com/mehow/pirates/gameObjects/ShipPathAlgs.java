package com.mehow.pirates.gameObjects;


import com.mehow.pirates.Cords;
import com.mehow.pirates.level.TileView;

public class ShipPathAlgs extends PathAlgorithms{

    public ShipPathAlgs(Callbacks tCallbacks){
        super(tCallbacks);
    }

	protected void computePossibleMoves(Cords cords,int range){
        CordData cordData = callbacks.getInfoOnCords(cords);
    	if(range > 0){
    		if(cordData.enemy == null
    				&& cordData.ship == null
    				&& (cordData.tile instanceof SeaTile || cordData.tile instanceof GoalTile)
    				&& cordData.mine == null){
    			if(!contains(cords)){
    			//	System.out.println("cord x: "+curX+" y: "+curY);
					storedCords.add(cords);
    			}
    			quadDirectCompute(cords, range-1);
   			}
    	}
    }
}
