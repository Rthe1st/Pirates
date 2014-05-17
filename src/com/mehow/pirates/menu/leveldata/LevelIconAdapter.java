package com.mehow.pirates.menu.leveldata;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mehow.pirates.LevelInfo;
import com.mehow.pirates.R;
import com.mehow.pirates.database.LevelsTable;

public class LevelIconAdapter extends BaseAdapter{
	
	public HightlightedInfo highLightedInfo;
	
	public static class HightlightedInfo {
	    private int lastHighlighted = -1;
	    private int curHighlighted = -1;

	    public HightlightedInfo() {}
	    
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
	
	private LevelInfo[] levelInfos;
	
	public LevelIconAdapter (LevelInfo[] tLevelInfos){
		this.highLightedInfo = new HightlightedInfo();
		levelInfos = tLevelInfos;
	}
	public int getCount() {
		return levelInfos.length;
	}

	public LevelInfo getItem(int mapNo) {
		return levelInfos[mapNo];
	}
	//not needed
	public long getItemId(int mapNo) {
		return 0;
	}

	public View getView(int mapNo, View convertView, ViewGroup parent) {
		if(convertView != null && mapNo != highLightedInfo.getCurHighlighted() && mapNo != highLightedInfo.getLastHighlighted()){
			return convertView;
		}
		Context context = parent.getContext();
        TextView textView = setUpTextView(mapNo,context);
        LevelsTable.Achievement levelAchievement = LevelsTable.calculateAchievement(levelInfos[mapNo].bestScore, levelInfos[mapNo].bronzeScore, levelInfos[mapNo].silverScore, levelInfos[mapNo].goldScore);
       if(highLightedInfo.getLastHighlighted() == mapNo) {
    	   textView.setBackgroundResource(nonHighlightDrawType(levelAchievement));
        }else if(highLightedInfo.getCurHighlighted() == mapNo){
        	textView.setBackgroundResource(highlightDrawType(levelAchievement)); 	
        }else{
        	textView.setBackgroundResource(nonHighlightDrawType(levelAchievement));
        }
        return textView;
	}
	private TextView setUpTextView(int mapNo, Context context){
		TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setText(Integer.toString(mapNo+1));
        return textView;
	}
	private int nonHighlightDrawType(LevelsTable.Achievement levelAchievement){
    	int drawableId;
		if(levelAchievement == LevelsTable.Achievement.NOT_COMP){
    		drawableId = R.drawable.level_icon_not_comp;
    	}else if(levelAchievement == LevelsTable.Achievement.NONE){
    		drawableId = R.drawable.level_icon_none;
    	}else if(levelAchievement == LevelsTable.Achievement.BRONZE){
    		drawableId = R.drawable.level_icon_bronze;            	
    	}else if(levelAchievement == LevelsTable.Achievement.SILVER){
    		drawableId = R.drawable.level_icon_silver;
    	}else{
    		drawableId = R.drawable.level_icon_gold;
    	}
    	return drawableId;
	}
	private int highlightDrawType(LevelsTable.Achievement levelAchievement){
    	int drawableId;
		if(levelAchievement == LevelsTable.Achievement.NOT_COMP){
    		drawableId = R.drawable.level_icon_not_comp_highlight;
    	}else if(levelAchievement == LevelsTable.Achievement.NONE){
    		drawableId = R.drawable.level_icon_none_highlight;
    	}else if(levelAchievement == LevelsTable.Achievement.BRONZE){
    		drawableId = R.drawable.level_icon_bronze_highlight;            	
    	}else if(levelAchievement == LevelsTable.Achievement.SILVER){
    		drawableId = R.drawable.level_icon_silver_highlight;
    	}else{
    		drawableId = R.drawable.level_icon_gold_highlight;
    	}
    	return drawableId;
	}
}
