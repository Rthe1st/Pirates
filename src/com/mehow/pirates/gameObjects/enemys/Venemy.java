package com.mehow.pirates.gameObjects.enemys;


import java.io.Serializable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mehow.pirates.Cords;
import com.mehow.pirates.R;

public class Venemy extends Enemy implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//move horizontaly before vert
	protected static int defNumOfMovesAllowed = 2;

	public static final String ENCODE_VALUE = "4";
	
	public Venemy(Cords cords, int tempNumOfMovesAllowed, Callbacks tCallbacks) {
		super(cords, tempNumOfMovesAllowed, tCallbacks);
	}
	public Venemy(Cords cords, Callbacks tCallbacks) {
		super(cords, defNumOfMovesAllowed, tCallbacks);
	}

	public Cords computeMoveStep(Cords shipCords){
		Cords oldCords = currentCords;//turnRecords.getLatestCords();
		Cords newCords;
		//really dum lol make me smart please im alive i have rights
		//add log for landing on ship
		newCords = attemptYmove(oldCords, shipCords.y);
		return newCords;
	}
    
    //------------
    //ANIMATION
    //------------
    
	private static Bitmap self;
	private static Bitmap frozen_self;
	
    public static void loadSpecialBitmaps(Resources r){
 	   	self = BitmapFactory.decodeResource(r, R.drawable.venemy_ship);
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
