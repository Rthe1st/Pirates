package com.mehow.pirates.menu.leveldata;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mehow.pirates.R;

public class LevelInfoLayout extends RelativeLayout{
	int level;
	int difficulty;
	boolean complete;
	int bestTime;
	int fewestMines;
	public LevelInfoLayout(Context context){
		super(context);
		inflateXML(context);
	}
	public LevelInfoLayout(Context context, AttributeSet attrs){
		super(context, attrs);
		inflateXML(context);
	}
	public LevelInfoLayout(Context context, AttributeSet attrs, int defStyle){
		super(context,attrs,defStyle);
		inflateXML(context);
	}
	private void inflateXML(Context context){
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	inflater.inflate(R.layout.level_info_layout, this);
	}
	public void changeInfo(String name, int diff, int mineLimit, int bestScore, String bestPlayer, int goldScore, int silverScore, int bronzeScore){
		TextView bestScoreView = (TextView) this.findViewById(R.id.levelInfoBestScoreData);
		TextView bestPlayerView = (TextView) this.findViewById(R.id.levelInfoBestPlayerData);
		TextView mineLimitView = (TextView) this.findViewById(R.id.levelInfoMineLimitData);
		TextView diffView = (TextView) this.findViewById(R.id.levelInfoDifficulty);
		TextView nameView = (TextView) this.findViewById(R.id.levelInfoName);
		System.out.println("goldScore: "+goldScore);
		TextView goldScoreView = (TextView) this.findViewById(R.id.levelInfoGoldScore);
		TextView silverScoreView = (TextView) this.findViewById(R.id.levelInfoSilverScore);
		TextView bronzeScoreView = (TextView) this.findViewById(R.id.levelInfoBronzeScore);
		bestScoreView.setText(String.valueOf(bestScore));
		bestPlayerView.setText(String.valueOf(bestPlayer));
		mineLimitView.setText(String.valueOf(mineLimit));
		diffView.setText(String.valueOf(diff));
		nameView.setText(String.valueOf(name));
		goldScoreView.setText(String.valueOf(goldScore));
		silverScoreView.setText(String.valueOf(silverScore));
		bronzeScoreView.setText(String.valueOf(bronzeScore));
	}
}
