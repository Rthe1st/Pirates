package com.mehow.pirates.menu.leveldata;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mehow.pirates.LevelInfo;
import com.mehow.pirates.R;

public class LevelInfoLayout extends RelativeLayout{
	
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
	public void changeInfo(LevelInfo levelInfo){
		TextView bestScoreView = (TextView) this.findViewById(R.id.levelInfoBestScoreData);
		TextView bestPlayerView = (TextView) this.findViewById(R.id.levelInfoBestPlayerData);
		TextView mineLimitView = (TextView) this.findViewById(R.id.levelInfoMineLimitData);
		TextView diffView = (TextView) this.findViewById(R.id.levelInfoDifficulty);
		TextView nameView = (TextView) this.findViewById(R.id.levelInfoName);
		TextView goldScoreView = (TextView) this.findViewById(R.id.levelInfoGoldScore);
		TextView silverScoreView = (TextView) this.findViewById(R.id.levelInfoSilverScore);
		TextView bronzeScoreView = (TextView) this.findViewById(R.id.levelInfoBronzeScore);
		bestScoreView.setText(String.valueOf(levelInfo.bestScore));
		bestPlayerView.setText(levelInfo.bestPlayer);
		mineLimitView.setText(String.valueOf(levelInfo.mineLimit));
		diffView.setText(String.valueOf((int)levelInfo.diff));
		nameView.setText(levelInfo.name);
		goldScoreView.setText(String.valueOf((int)levelInfo.goldScore));
		silverScoreView.setText(String.valueOf((int)levelInfo.silverScore));
		bronzeScoreView.setText(String.valueOf((int)levelInfo.bronzeScore));
	}
}
