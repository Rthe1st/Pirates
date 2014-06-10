package com.mehow.pirates.gameObjects;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mehow.pirates.Cords;
import com.mehow.pirates.R;

public class Rock extends Tile{

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
				&& cordData.ship==null){
			return true;
		}else{
			return false;
		}}
   
   public static void loadSpecialBitmaps(Resources r){
	   	self = BitmapFactory.decodeResource(r, R.drawable.rock);
    }

   @Override
   protected Bitmap getSelf() {
	   return self;
   }
}
