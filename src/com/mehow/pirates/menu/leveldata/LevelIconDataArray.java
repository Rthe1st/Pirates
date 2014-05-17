package com.mehow.pirates.menu.leveldata;

public class LevelIconDataArray {
    private int lastHighlighted = -1;
    private int curHighlighted = -1;

    public LevelIconDataArray() {
        super();
    }
    public void setCurHighlighted(int i){
    	if(curHighlighted != -1){
    		setLastHighlighted(curHighlighted);
    	}
    	curHighlighted = i;
    }
    public int getCurHighlighted(){
    	return curHighlighted;
    }
    public int getLastHighlighted(){
    	return lastHighlighted;
    }

    private void setLastHighlighted(int i){
    	lastHighlighted = i;
    }
}