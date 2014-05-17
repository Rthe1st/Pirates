package com.mehow.pirates;

public class LevelInfo {

	public final String name, bestPlayer, mapData;
	public final int diff, mineLimit, bestScore, goldScore, silverScore,
			bronzeScore;

	public LevelInfo(String tName, int tDiff, int tMineLimit, int tBestScore,
			String tBestPlayer, int tGoldScore, int tSilverScore,
			int tBronzeScore, String tMapData) {
		name = tName;
		diff = tDiff;
		mineLimit = tMineLimit;
		bestScore = tBestScore;
		bestPlayer = tBestPlayer;
		goldScore = tGoldScore;
		silverScore = tSilverScore;
		bronzeScore = tBronzeScore;
		mapData = tMapData;
	}
}
