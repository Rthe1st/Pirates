package com.mehow.pirates.gameObjects;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mehow.pirates.Cords;
import com.mehow.pirates.R;

public class SeaTile extends Tile{

    public SeaTile(Cords cords) {
		super(cords);
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
