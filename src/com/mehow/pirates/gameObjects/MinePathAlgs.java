package com.mehow.pirates.gameObjects;


import com.mehow.pirates.Cords;

public class MinePathAlgs extends PathAlgorithms{

    public MinePathAlgs(Callbacks tCallbacks){
        super(tCallbacks);
    }

    protected void computePossibleMoves(Cords cords,int range){
        CordData cordData = callbacks.getInfoOnCords(cords);
        if(range > 0){
    		if(cordData.enemy == null
    				&& cordData.ship == null
    				&& cordData.tile instanceof SeaTile
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
