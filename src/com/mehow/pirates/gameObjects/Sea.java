package com.mehow.pirates.gameObjects;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mehow.pirates.Cords;
import com.mehow.pirates.R;

public class Sea extends Tile{

	public static final String ENCODE_VALUE = "0";
	
    public Sea(Cords cords) {
		super(cords);
	}
  
    public static boolean isValidMove(CordData cordData){
    	return true;
    }
    
    //------------
    //ANIMATION
    //------------
    
	private static Bitmap self;
	
    public static void loadSpecialBitmaps(Resources r){
 	   	self = BitmapFactory.decodeResource(r, R.drawable.sea);
     }

    @Override
    protected Bitmap getSelf() {
 	   return self;
    }
}
