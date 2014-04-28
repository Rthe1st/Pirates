package com.mehow.pirates.gameObjects.enemys;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mehow.pirates.Cords;
import com.mehow.pirates.R;
import com.mehow.pirates.gameObjects.PathAlgorithms;

public class Venemy extends Enemy{
	//move horizontaly before vert
	protected static int defNumOfMovesAllowed = 2;

	public Venemy(Cords cords, int tempNumOfMovesAllowed, PathAlgorithms.Callbacks tCallbacks) {
		super(cords, tempNumOfMovesAllowed, tCallbacks);
	}
	public Venemy(Cords cords, PathAlgorithms.Callbacks tCallbacks) {
		super(cords, defNumOfMovesAllowed, tCallbacks);
	}

	public Cords computeMoveStep(Cords shipCords){
		Cords oldCords = turnRecords.getLatestCords();
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
	
    public static void loadSpecialBitmaps(Resources r){
 	   	self = BitmapFactory.decodeResource(r, R.drawable.venemy_ship);
     }

    @Override
    protected Bitmap getSelf() {
 	   return self;
    }
}
