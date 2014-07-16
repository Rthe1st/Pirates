package com.mehow.pirates.gameObjects.enemys;

import java.io.Serializable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mehow.pirates.Cords;
import com.mehow.pirates.R;

public class Aenemy extends Enemy implements Serializable{
	protected static int defNumOfMovesAllowed = 1;

	public static final String ENCODE_VALUE = "7";
	
	//move horizontaly before vert
	public Aenemy(Cords cords, int tempNumOfMovesAllowed, Callbacks tCallbacks) {
		super(cords,tempNumOfMovesAllowed, tCallbacks);
	}
	public Aenemy(Cords cords, Callbacks tCallbacks) {
		super(cords, defNumOfMovesAllowed, tCallbacks);
	}

	//moves x and y, chooses axis based on displacement
	public Cords computeMoveStep(Cords shipCords){
        Cords oldCords = currentCords;//turnRecords.getLatestCords();
        Cords newCords;
		int yDisplacement = Math.abs(shipCords.y-oldCords.y);
		int xDisplacement = Math.abs(shipCords.x-oldCords.x);
		if(yDisplacement > xDisplacement){
			newCords = attemptYmove(oldCords, shipCords.y);
			if(oldCords.equals(newCords)){
				newCords = attemptXmove(oldCords, shipCords.x);
			}
		}else{
			newCords = attemptXmove(oldCords, shipCords.x);
			if(oldCords.equals(newCords)){
				newCords = attemptYmove(oldCords, shipCords.y);
			}		
		}
		return newCords;
	}

    //------------
    //ANIMATION
    //------------
    
	private static Bitmap self;

	private static Bitmap frozen_self;
	
    public static void loadSpecialBitmaps(Resources r){
 	   	self = BitmapFactory.decodeResource(r, R.drawable.aenemy_ship);
 	   	frozen_self = BitmapFactory.decodeResource(r, R.drawable.venemy_ship_frozen);
     }

    @Override
    public Bitmap getSelf() {
 	   if(this.frozenTurnCount == 0){
 		   return self;
 	   }else{
 		   return frozen_self;
 	   }
    }
}
