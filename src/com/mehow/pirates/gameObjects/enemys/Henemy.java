package com.mehow.pirates.gameObjects.enemys;


import java.io.Serializable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mehow.pirates.Cords;
import com.mehow.pirates.R;

public class Henemy extends Enemy implements Serializable{
	protected static int defNumOfMovesAllowed = 2;

	public static final String ENCODE_VALUE = "6";
	
	//move horizontaly before vert
	public Henemy(Cords cords, int tempNumOfMovesAllowed, Callbacks tCallbacks) {
		super(cords, tempNumOfMovesAllowed, tCallbacks);
	}
	public Henemy(Cords cords, Callbacks tCallbacks) {
		super(cords, defNumOfMovesAllowed, tCallbacks);
	}
	public Cords computeMoveStep(Cords shipCords){
		Cords oldCords = currentCords;//turnRecords.getLatestCords();
		Cords newCords;
		//really dum lol make me smart please im alive i have rights
		//add log for landing on ship
		newCords = attemptXmove(oldCords, shipCords.x);
		return newCords;
	}
    
    //------------
    //ANIMATION
    //------------
    
	private static Bitmap self;
	
    public static void loadSpecialBitmaps(Resources r){
 	   	self = BitmapFactory.decodeResource(r, R.drawable.henemy_ship);
     }

    @Override
    public Bitmap getSelf() {
 	   return self;
    }
}
