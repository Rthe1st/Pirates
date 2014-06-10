package com.mehow.pirates.gameObjects.enemys;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mehow.pirates.Cords;
import com.mehow.pirates.R;
import com.mehow.pirates.gameObjects.PathAlgorithms;

public class Aenemy extends Enemy{
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
        Cords oldCords = turnRecords.getLatestCords();
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
	
    public static void loadSpecialBitmaps(Resources r){
 	   	self = BitmapFactory.decodeResource(r, R.drawable.aenemy_ship);
     }

    @Override
    protected Bitmap getSelf() {
 	   return self;
    }
}
