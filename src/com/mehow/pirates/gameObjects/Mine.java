package com.mehow.pirates.gameObjects;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;

import com.mehow.pirates.Cords;
import com.mehow.pirates.R;

public class Mine extends GameObject{//implements  TurnRecord.Undoable{

	private static Paint minePaint;
	
	public Mine(Cords startCords){
		super(startCords);
	}
	
    //------------
    //ANIMATION
    //------------
    
    
	private static Bitmap self;

    @Override
    protected Bitmap getSelf() {
 	   return self;
    }
    
    public static void loadSpecialBitmaps(Resources r){
	   	self = BitmapFactory.decodeResource(r, R.drawable.mine);
	   	minePaint = new Paint();
		minePaint.setARGB(100, 0, 100, 0);
    }
}
