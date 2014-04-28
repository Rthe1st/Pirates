package com.mehow.pirates.gameObjects;

public class ScoreCalc {
    //make this a lot simpler - helped by graphicly showing when user "loses" points
	private final static int baseScore = 50;
	public static int getScore(int minesLeft, int turns){
		int score = baseScore+(minesLeft*10)-turns;
		if(score < 0){
			return 0;
		}else{
			return score;
		}
	}
}
