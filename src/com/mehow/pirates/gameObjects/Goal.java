package com.mehow.pirates.gameObjects;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mehow.pirates.Cords;
import com.mehow.pirates.R;

public class Goal extends Tile{
	
	public static final String ENCODE_VALUE = "5";
	
	public Goal(Cords cords){
		super(cords);
	}
	
	//this is deleberatly not limited to 1, partly because a check would require a search of treemap
	//but also cause multipel goals could be fun
	public static boolean isValidMove(CordData cordData){
		if(cordData.enemy == null && cordData.ship==null){
			return true;
		}else{
			return false;
		}

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
