package com.mehow.pirates;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.mehow.pirates.gameObjects.GameObject;
import com.mehow.pirates.gameObjects.InterStep;

public interface Moves extends GameObject{
	//syncing animation steps after mapdata manipulation
    public void makeStep(Cords newCords);
    public void undoStep(Cords newCurrent);
    public void undoTurn(Cords newCords);
    public void newTurn();
	
    public InterStep getCurrentInterStep(int interStepNo);
	public boolean hasMoreSteps(int interStepNumber);//this is only for animating, use canMakeMove or similar for gamelogic
	public void drawSelf(Canvas canvas, int interStepNo, float animationOffset, RectF drawArea);
}
