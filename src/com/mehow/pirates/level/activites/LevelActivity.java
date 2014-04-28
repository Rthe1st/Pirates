package com.mehow.pirates.level.activites;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.mehow.pirates.Consts;
import com.mehow.pirates.R;
import com.mehow.pirates.database.DefaultLevelDatabaseUIFunctions;
import com.mehow.pirates.level.GameLogic;
import com.mehow.pirates.level.TileView;
import com.mehow.pirates.level.fragments.MapLeftOptions;
import com.mehow.pirates.level.fragments.MapRightOptions;
import com.mehow.pirates.level.fragments.dialog.ExitLevelDialog;
import com.mehow.pirates.level.fragments.dialog.GameOverFragment;
import com.mehow.pirates.level.fragments.dialog.LevelCompleteDialog;
import com.mehow.pirates.level.fragments.dialog.NewBestScoreDialog;
import com.mehow.pirates.level.fragments.dialog.RestartConfirmFrag;
import com.mehow.pirates.level.fragments.dialog.tutorial.TutorialInfoDialog;
import com.mehow.pirates.menu.activities.MenuActivity;

public class LevelActivity extends FragmentActivity
    implements MapLeftOptions.Callbacks
        , MapRightOptions.Callbacks
        , ExitLevelDialog.Callbacks
        , GameOverFragment.Callbacks
        , LevelCompleteDialog.Callbacks
        , NewBestScoreDialog.Callbacks
        , RestartConfirmFrag.Callbacks
        , GameLogic.Callbacks {
	
	private int mapId;
	private int mapNum;
	private DefaultLevelDatabaseUIFunctions dbUi;
	TileView tileView;
	//used to display tutorial only if not restarting
	//intialy always true, gets changed soon as read by start()
	private boolean restartFlag = false;
	
	//RESULT consts (to tell menuActivity if it should finish
	public static int EXITED_APP = 2;
	public static int EXITED_LEVEL = 3;

    public GameLogic gameLogic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get map user selected
        //hacky way of working out xml element to extract
        Intent userChoice = getIntent();
    	mapNum = userChoice.getIntExtra(MenuActivity.MAP_CHOICE_EXTRA, -1);
    	String xmlMapping = "level"+mapNum;
        mapId = this.getResources().getIdentifier(xmlMapping, "array", "com.mehow.pirates");
        dbUi = new DefaultLevelDatabaseUIFunctions(this, null);
        if(savedInstanceState!=null){
        	gameLogic = new GameLogic(this, savedInstanceState.getBundle("GAME_LOGIC"));
        }else{
        	gameLogic = new GameLogic(this);
        }
        //---------
        Configuration config = getResources().getConfiguration();
        if(config.orientation == Configuration.ORIENTATION_PORTRAIT){
        	setContentView(R.layout.map_vertical);
        }else if(config.orientation == Configuration.ORIENTATION_LANDSCAPE){
        	setContentView(R.layout.map_horizontal);
        }else{
        	//if orientation undefined, should really use a 3rd "generic" layout
        	setContentView(R.layout.map_vertical);
        }
        tileView = ((TileView)findViewById(R.id.map));
    }
    @Override
    public void onStart(){
    	super.onStart();
    	System.out.println("started");
    	gameLogic.changeTurnCount(0);
        gameLogic.changeMineCount(0);
        setHighscoreDisplay();
    }
    @Override
    public void onStop(){
    	super.onStop();
    	dbUi.closeDb();
    }
    @Override
    public void onResume(){
    	super.onResume();
    	if(!restartFlag){
    		showTutorial(mapNum);
    		restartFlag = true;
    	}
    }
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }*/
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
    	super.onRestoreInstanceState(savedInstanceState);
    	restartFlag = savedInstanceState.getBoolean("restartFlag");
    	System.out.println("restarted");
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
    	System.out.println("saved");
    	savedInstanceState.putBundle("GAME_LOGIC", gameLogic.saveState());
    	savedInstanceState.putBoolean("restartFlag", restartFlag);
    	super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public void submitScore(int newScore, String playerName){
        dbUi.newBestScore(mapNum, newScore, playerName);
        DialogFragment levelCompleteDialog = LevelCompleteDialog.newInstance(newScore);
        levelCompleteDialog.show(getSupportFragmentManager(), "levelCompleteDialog");
    }
    @Override
    public int getMineLimit(){
        return dbUi.getMineLimit(mapNum);
    }
    @Override
    public int getLevelBestScore(){
        return dbUi.getLevelBestScore(mapNum);
    }
    @Override
    public boolean isMaxLevel(){
        return Consts.noOfMaps == mapNum;
    }
    public void showTutorial(int mapNum){
    	System.out.println("mapNum: " + mapNum);
    	int mapInfoId = this.getResources().getIdentifier("level"+mapNum+"_info", "array", "com.mehow.pirates");
    	TypedArray mapInfo =  this.getResources().obtainTypedArray(mapInfoId);
    	int tutorialNum = mapInfo.getInt(Consts.mapInfoTutIndex,0);
    	int slidesNum = mapInfo.getInt(Consts.mapInfoTutSlidesNumIndex,0);
    	mapInfo.recycle();//google what this does
    	if(tutorialNum != 0){
    		TutorialInfoDialog tutDialog = TutorialInfoDialog.newInstance(tutorialNum, slidesNum);
    		tutDialog.show(getSupportFragmentManager(), "tutDialog");
    	}
    }

    public void invalidateMap(){
        TileView map = (TileView)findViewById(R.id.map);
        map.invalidate();
    }

    public void setHighscoreDisplay(){
    	MapLeftOptions leftOpFrag = (MapLeftOptions)getSupportFragmentManager().findFragmentById(R.id.mapLeftOptions);
    	int highscore = dbUi.getLevelBestScore(mapNum);
    	leftOpFrag.setHighscoreDisplay(highscore);
    }

    @Override
    public void onBackPressed(){
   		DialogFragment exitDialog = new ExitLevelDialog();
		exitDialog.show(getSupportFragmentManager(), "exitLevelDialog");
    }

    //-------------------
    //TileView callbacks
    //-------------------
    @Override
    public int getMapId(){
        return mapId;
    }
    @Override
    public void updateCounts(int mineChange, int turnChange, int scoreChange){
        MapLeftOptions leftOpFrag = (MapLeftOptions)getSupportFragmentManager().findFragmentById(R.id.mapLeftOptions);
        leftOpFrag.updateMines(mineChange);
        leftOpFrag.updateTurns(turnChange);
        leftOpFrag.updateScore(scoreChange);
    }
    @Override
    public void showGameOverDialog(){
        System.out.println("gameoverdialog called");
        DialogFragment gameOverFrag = new GameOverFragment();
        gameOverFrag.show(getSupportFragmentManager(), "gameOverFrag");
    }
    @Override
    public void showLevelCompleteDialog(boolean setNewScore, int score){
        System.out.println("levelCompleteDialog called");
        if(setNewScore == false){
            DialogFragment levelCompleteDialog = LevelCompleteDialog.newInstance(score);
            //	DialogFragment levelCompleteDialog = new LevelCompleteDialog();
            levelCompleteDialog.show(getSupportFragmentManager(), "levelCompleteDialog");
        }else{
            DialogFragment newBestScoreDialog = NewBestScoreDialog.newInstance(score);
            newBestScoreDialog.show(getSupportFragmentManager(), "newBestScoreDialog");
        }
    }
    //mapRightOptions interface
    @Override
    public void changeMineBtnState(boolean state){
        ((MapRightOptions)this.getSupportFragmentManager().findFragmentById(R.id.mapRightOptions)).changeMineBtnState(state);
    }
    @Override
    public void mineBtn(View mineBtnView){
        TileView map = (TileView)findViewById(R.id.map);
        gameLogic.mineButtonPressed();
        if(gameLogic.invalidate()){
            map.invalidate();
        }
    }
    @Override
    public void restartBtn(View restartBtn){
        DialogFragment restartConfirmFrag = new RestartConfirmFrag();
        restartConfirmFrag.show(getSupportFragmentManager(), "restartConfirmFrag");
    }
    @Override
    public void undoBtn(View undoBtn){
        gameLogic.undo();
    }
    @Override
    public void endBtn(View endBtnView){
        boolean turnEnded = gameLogic.endTurn();
        System.out.println("end turn pressed");
        if(turnEnded){
            this.changeMineBtnState(true);
            tileView.animationLogic.updateScreen(true);
        }else{
            System.out.println("not ending turn");
        }
    }
    //various ingame dialoge fragment Callbacks
    @Override
    public void mainMenu(){
        //setResult(EXITED_LEVEL);
        Intent mainMenu = new Intent(this, MenuActivity.class);
        mainMenu.putExtra(MenuActivity.LEVEL_FRAG_KEY, MenuActivity.LEVEL_FRAG_VAL);
        startActivity(mainMenu);
        this.finish();
    }
    @Override
    public void exit(){
        //setResult(EXITED_APP);
        this.finish();
    }
    @Override
    public void levelRetry(){
        Intent userChoice = new Intent(this, LevelActivity.class);
        userChoice.putExtra(MenuActivity.MAP_CHOICE_EXTRA, mapNum);
        startActivity(userChoice);
        this.finish();
    }
    @Override
    public void nextLevel(){
        Intent userChoice = new Intent(this, LevelActivity.class);
        userChoice.putExtra(MenuActivity.MAP_CHOICE_EXTRA, mapNum+1);
        startActivity(userChoice);
        this.finish();
    }
}
