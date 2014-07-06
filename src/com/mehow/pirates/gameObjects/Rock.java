package com.mehow.pirates.gameObjects;

import java.io.Serializable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mehow.pirates.Cords;
import com.mehow.pirates.R;

public class Rock extends Tile implements Serializable{

	public static final String ENCODE_VALUE = "1";
	
	//------------
    //ANIMATION
    //------------
	private static Bitmap self;
	
   public Rock(Cords cords) {
		super(cords);
	}

   public static boolean isValidMove(CordData cordData){
		if(cordData.enemy==null
				&& cordData.ship==null && (cordData.tile == null || !cordData.tile.getClass().equals(Rock.class))){
			return true;
		}else{
			return false;
		}
	}
   
   public static void loadSpecialBitmaps(Resources r){
	   	self = BitmapFactory.decodeResource(r, R.drawable.rock);
    }

   @Override
   public Bitmap getSelf() {
	   return self;
   }
}
