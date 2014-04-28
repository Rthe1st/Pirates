package com.mehow.pirates.gameObjects;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mehow.pirates.Cords;
import com.mehow.pirates.R;

public class GoalTile extends Tile{
	
	public GoalTile(Cords cords){
		super(cords);
	}
    //------------
    //ANIMATION
    //------------
    
	private static Bitmap self;
	
    //replace with drawables
	private static Bitmap[] bitmaps;
    public static Bitmap getBitmap(int tileType){
    	return bitmaps[tileType];
    }

   public static void loadBitmaps(Resources r){
	   	self = BitmapFactory.decodeResource(r, R.drawable.sea);
    }

   @Override
   protected Bitmap getSelf() {
	   return self;
   }
   
   public static void loadSpecialBitmaps(Resources r){
	   	self = BitmapFactory.decodeResource(r, R.drawable.level_goal);
   }
}
