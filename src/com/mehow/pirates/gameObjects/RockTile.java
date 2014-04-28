package com.mehow.pirates.gameObjects;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mehow.pirates.Cords;
import com.mehow.pirates.R;

public class RockTile extends Tile{
    //------------
    //ANIMATION
    //------------
	private static Bitmap self;
	
   public RockTile(Cords cords) {
		super(cords);
	}

   public static void loadSpecialBitmaps(Resources r){
	   	self = BitmapFactory.decodeResource(r, R.drawable.rock);
    }

   @Override
   protected Bitmap getSelf() {
	   return self;
   }
}
