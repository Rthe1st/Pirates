package com.mehow.pirates.menu.leveldata;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mehow.pirates.Consts;
import com.mehow.pirates.Consts.Achievements;
import com.mehow.pirates.R;
import com.mehow.pirates.menu.activities.MenuActivity;
import com.mehow.pirates.menu.fragments.LevelMenu;

public class LevelIconAdapter extends BaseAdapter{
	
	private LevelIconDataArray levelIconDataArray;
	private Context context;
	
	public LevelIconAdapter (Context tempContext){
		context = tempContext;
		LevelMenu levelMenu = (LevelMenu) ((MenuActivity)context).getContentFrag();
		this.levelIconDataArray = levelMenu.getLevelIconDataArray();
	}
	public int getCount() {
		return Consts.noOfMaps;
	}
	//no needed
	public Object getItem(int mapNo) {
		return 0;
	}
	//not needed
	public long getItemId(int mapNo) {
		return 0;
	}
	//INEFFICENT CAUSE DOSNT USE CONVERTVIEW, FIX DISS
	public View getView(int mapNo, View convertView, ViewGroup parent) {
		//System.out.println("getView in adapter-------------------------");
	//	System.out.println("getviewmapno: "+mapNo);
        TextView textView = setUpTextView(mapNo,context);
        Achievements levelAchievement = ((MenuActivity)context).databaseHelper.levelsTable.getLevelAchievment(mapNo+1);//+1 cause db is 1 based, grid is 0 based
        if (convertView == null ) {  // if it's not recycled, initialize some attributes
            if(levelIconDataArray.getCurHighlighted() == mapNo){
           // 	System.out.println("prev null highlight maps: "+mapNo);
            	textView.setBackgroundResource(highlightDrawType(levelAchievement));
            }else{
            	textView.setBackgroundResource(nonHighlightDrawType(levelAchievement));
            //	System.out.println("prev null nonhighlight maps: "+mapNo);
            }
        }else if(levelIconDataArray.getLastHighlighted() == mapNo) {
       // 	System.out.println("notnull null lastHighlighted: "+mapNo);
        	textView.setBackgroundResource(nonHighlightDrawType(levelAchievement));
        }else if(levelIconDataArray.getCurHighlighted() == mapNo){
       // 	System.out.println("not null curhighlighted maps: "+mapNo);
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
	private int nonHighlightDrawType(Achievements levelAchievement){
    	int drawableId;
		if(levelAchievement == Achievements.NOT_COMP){
    		drawableId = R.drawable.level_icon_not_comp;
    	}else if(levelAchievement == Achievements.NONE){
    		drawableId = R.drawable.level_icon_none;
    	}else if(levelAchievement == Achievements.BRONZE){
    		drawableId = R.drawable.level_icon_bronze;            	
    	}else if(levelAchievement == Achievements.SILVER){
    		drawableId = R.drawable.level_icon_silver;
    	}else{
    		drawableId = R.drawable.level_icon_gold;
    	}
    	return drawableId;
	}
	private int highlightDrawType(Achievements levelAchievement){
    	int drawableId;
		if(levelAchievement == Achievements.NOT_COMP){
    		drawableId = R.drawable.level_icon_not_comp_highlight;
    	}else if(levelAchievement == Achievements.NONE){
    		drawableId = R.drawable.level_icon_none_highlight;
    	}else if(levelAchievement == Achievements.BRONZE){
    		drawableId = R.drawable.level_icon_bronze_highlight;            	
    	}else if(levelAchievement == Achievements.SILVER){
    		drawableId = R.drawable.level_icon_silver_highlight;
    	}else{
    		drawableId = R.drawable.level_icon_gold_highlight;
    	}
    	return drawableId;
	}
}
