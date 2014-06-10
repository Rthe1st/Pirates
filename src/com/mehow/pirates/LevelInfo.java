package com.mehow.pirates;

import java.io.Serializable;

import com.mehow.pirates.database.LevelsTable;

public class LevelInfo implements Serializable {

	//Changing this to a builder pattern would allow the getLevelInfo function in LevelsTable to take Coloumns as params
	//may be good?
	
	public String name, bestPlayer, mapData;
	public int diff, mineLimit, bestScore, goldScore, silverScore,
			bronzeScore;
	public final long id;
	public final LevelsTable.LevelTypes type;

	public LevelInfo(long tId, String tName, int tDiff, int tMineLimit, int tBestScore,
			String tBestPlayer, int tGoldScore, int tSilverScore,
			int tBronzeScore, String tMapData, LevelsTable.LevelTypes tType) {
		id = tId;
		name = tName;
		diff = tDiff;
		mineLimit = tMineLimit;
		bestScore = tBestScore;
		bestPlayer = tBestPlayer;
		goldScore = tGoldScore;
		silverScore = tSilverScore;
		bronzeScore = tBronzeScore;
		mapData = tMapData;
		type = tType;
	}
}
