package com.mehow.pirates.gameObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.mehow.pirates.Cords;
import com.mehow.pirates.level.GameLogic;

public interface GameObject {

	public boolean trySelect(GameLogic.GameStates gameState);

    public void noLongerExists();
    public void kill();
    public void revive(Cords newCords);
    
    public Cords getCurrentCords();
    
    public String getEncodedParameters();
    
    //animation
   // public void drawSelf(Canvas canvas, int interStepNo, float animationOffset, RectF drawArea);
   // public void drawSelfNoAnimate(Canvas canvas, RectF drawArea);
    public void selectedDraw(Canvas canvas, RectF drawArea);
    public Paint getSelfPaint();
    public void setSelfPaint(Paint newPaint);
    
    public boolean exists();
    public void drawSelfNoAnimate(Canvas canvas, RectF drawArea);
    public void update(long time);
}
