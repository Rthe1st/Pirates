package com.mehow.pirates.level.activites;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.mehow.pirates.Consts;
import com.mehow.pirates.LevelInfo;
import com.mehow.pirates.R;
import com.mehow.pirates.animation.ExplosionSequence;
import com.mehow.pirates.database.DatabaseHelper;
import com.mehow.pirates.database.LevelsTable;
import com.mehow.pirates.gameObjects.Goal;
import com.mehow.pirates.gameObjects.Mine;
import com.mehow.pirates.gameObjects.Rock;
import com.mehow.pirates.gameObjects.Sea;
import com.mehow.pirates.gameObjects.Ship;
import com.mehow.pirates.gameObjects.Tile;
import com.mehow.pirates.gameObjects.enemys.Aenemy;
import com.mehow.pirates.gameObjects.enemys.Henemy;
import com.mehow.pirates.gameObjects.enemys.PathEnemy;
import com.mehow.pirates.gameObjects.enemys.Venemy;
import com.mehow.pirates.level.GameLogic;
import com.mehow.pirates.level.TileView;
import com.mehow.pirates.level.TileView.LogicCallbacks;
import com.mehow.pirates.level.fragments.MapLeftOptions;
import com.mehow.pirates.level.fragments.MapRightOptions;
import com.mehow.pirates.level.fragments.dialog.ExitLevelDialog;
import com.mehow.pirates.level.fragments.dialog.GameOverFragment;
import com.mehow.pirates.level.fragments.dialog.LevelCompleteDialog;
import com.mehow.pirates.level.fragments.dialog.NewBestScoreDialog;
import com.mehow.pirates.level.fragments.dialog.RestartConfirmFrag;
import com.mehow.pirates.level.fragments.dialog.tutorial.TutorialInfoDialog;
import com.mehow.pirates.menu.activities.MenuActivity;

public class LevelActivity extends FragmentActivity implements
		MapLeftOptions.Callbacks, MapRightOptions.Callbacks,
		ExitLevelDialog.Callbacks, GameOverFragment.Callbacks,
		LevelCompleteDialog.Callbacks, NewBestScoreDialog.Callbacks,
		RestartConfirmFrag.Callbacks, GameLogic.Callbacks,
		TileView.ActivityCallbacks {

	private long mapNum;
	private DatabaseHelper databaseHelper;

	// used to display tutorial only if not restarting
	// intialy always true, gets changed soon as read by start()
	private boolean restartFlag = false;

	// RESULT consts (to tell menuActivity if it should finish
	public static int EXITED_APP = 2;
	public static int EXITED_LEVEL = 3;

	public GameLogic gameLogic;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// get map user selected
		// hacky way of working out xml element to extract
		Intent userChoice = getIntent();
		mapNum = userChoice.getLongExtra(MenuActivity.LEVEL_ID_EXTRA, -1);
		databaseHelper = DatabaseHelper.getInstance(this);
		Consts.loadAnimations(this.getResources());
		if (savedInstanceState != null) {
			gameLogic = new GameLogic(this,
					savedInstanceState.getBundle("GAME_LOGIC"));
		} else {
			LevelInfo levelInfo = databaseHelper.levelsTable
					.getLevelInfo(mapNum);
			gameLogic = new GameLogic(this, levelInfo);
		}
		// ---------
		Configuration config = getResources().getConfiguration();
		if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
			setContentView(R.layout.map_vertical);
		} else if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setContentView(R.layout.map_horizontal);
		} else {
			// if orientation undefined, should really use a 3rd "generic"
			// layout
			setContentView(R.layout.map_vertical);
		}
		ImageButton mineButton = ((ImageButton) this.findViewById(R.id.mineBtn));
		// lol super hack
		mineButton.setImageResource(R.drawable.ship_btn);
	}

	@Override
	public void onStart() {
		super.onStart();
		System.out.println("started");
		TileView tileView = (TileView) findViewById(R.id.map);
		Runnable userInput = new Runnable() {
			@Override
			public void run() {
				gameLogic.changeTurnCount(0);
				gameLogic.changeMineCount(0);
				setHighscoreDisplay(gameLogic.levelInfo.bestScore);
			}
		};
		// tileView.addUserInput(userInput);
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.i("LevelActivity", "onstop");
		// not closing may be bad practie
		// databaseHelper.close();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!restartFlag) {
			// showTutorial((int) mapNum);
			restartFlag = true;
		}
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.activity_main, menu); return true; }
	 */
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		restartFlag = savedInstanceState.getBoolean("restartFlag");
		System.out.println("restarted");
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		System.out.println("saved");
		savedInstanceState.putBundle("GAME_LOGIC", gameLogic.saveState());
		Log.i("LevelActivity", "GameLogic saved");
		savedInstanceState.putBoolean("restartFlag", restartFlag);
		// super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void submitScore(int newScore, String playerName) {
		databaseHelper.levelsTable.newBestScore(mapNum, newScore, playerName);
		DialogFragment levelCompleteDialog = LevelCompleteDialog
				.newInstance(newScore);
		levelCompleteDialog.show(getSupportFragmentManager(),
				"levelCompleteDialog");
	}

	@Override
	public boolean isMaxLevel() {
		return mapNum == databaseHelper.levelsTable
				.countLevels(LevelsTable.LevelTypes.PRE_MADE);
	}

	// whole tutorial system needs a rewrite
	public void showTutorial(int mapNum) {
		System.out.println("mapNum: " + mapNum);
		if (gameLogic.levelInfo.type == LevelsTable.LevelTypes.PRE_MADE) {
			int mapInfoId = this.getResources().getIdentifier(
					"level" + mapNum + "_info", "array", "com.mehow.pirates");
			TypedArray mapInfo = this.getResources()
					.obtainTypedArray(mapInfoId);
			int tutorialNum = mapInfo.getInt(Consts.mapInfoTutIndex, 0);
			int slidesNum = mapInfo.getInt(Consts.mapInfoTutSlidesNumIndex, 0);
			mapInfo.recycle();// google what this does
			if (tutorialNum != 0) {
				TutorialInfoDialog tutDialog = TutorialInfoDialog.newInstance(
						tutorialNum, slidesNum);
				tutDialog.show(getSupportFragmentManager(), "tutDialog");
			}
		}
	}

	/*
	 * public void invalidateMap(){ TileView map =
	 * (TileView)findViewById(R.id.map); map.invalidate(); }
	 */

	public void setHighscoreDisplay(final int highScore) {
		Runnable uiRunnable = new Runnable() {
			@Override
			public void run() {
				MapLeftOptions leftOpFrag = (MapLeftOptions) getSupportFragmentManager()
						.findFragmentById(R.id.mapLeftOptions);
				leftOpFrag.setHighscoreDisplay(highScore);
			}
		};
		this.runOnUiThread(uiRunnable);
	}

	@Override
	public void onBackPressed() {
		DialogFragment exitDialog = new ExitLevelDialog();
		exitDialog.show(getSupportFragmentManager(), "exitLevelDialog");
	}

	@Override
	public void updateCounts(final int mineChange, final int turnChange,
			final int scoreChange) {
		Runnable uiRunnable = new Runnable() {
			@Override
			public void run() {
				MapLeftOptions leftOpFrag = (MapLeftOptions) getSupportFragmentManager()
						.findFragmentById(R.id.mapLeftOptions);
				leftOpFrag.updateMines(mineChange);
				leftOpFrag.updateTurns(turnChange);
				leftOpFrag.updateScore(scoreChange);
			}
		};
		this.runOnUiThread(uiRunnable);
	}

	@Override
	public void showGameOverDialog() {
		Runnable uiRunnable = new Runnable() {
			@Override
			public void run() {
				System.out.println("gameoverdialog called");
				DialogFragment gameOverFrag = new GameOverFragment();
				gameOverFrag.show(getSupportFragmentManager(), "gameOverFrag");
			}
		};
		this.runOnUiThread(uiRunnable);
	}

	@Override
	public void showLevelCompleteDialog(final boolean setNewScore,
			final int score) {
		System.out.println("levelCompleteDialog called");
		Runnable uiRunnable = new Runnable() {
			@Override
			public void run() {
				if (setNewScore == false) {
					DialogFragment levelCompleteDialog = LevelCompleteDialog
							.newInstance(score);
					levelCompleteDialog.show(getSupportFragmentManager(),
							"levelCompleteDialog");
				} else {
					DialogFragment newBestScoreDialog = NewBestScoreDialog
							.newInstance(score);
					newBestScoreDialog.show(getSupportFragmentManager(),
							"newBestScoreDialog");
				}
			}
		};
		this.runOnUiThread(uiRunnable);
	}

	// mapRightOptions interface
	@Override
	public void changeMineBtnState(final boolean state) {
		Runnable uiRunnable = new Runnable() {
			@Override
			public void run() {
				((MapRightOptions) getSupportFragmentManager()
						.findFragmentById(R.id.mapRightOptions))
						.changeMineBtnState(state);
			}
		};
		this.runOnUiThread(uiRunnable);
	}

	@Override
	public void mineBtn(View mineBtnView) {
		TileView tileView = (TileView) findViewById(R.id.map);
		Runnable userInput = new Runnable() {
			@Override
			public void run() {
				gameLogic.mineButtonPressed();
			}
		};
		tileView.addUserInput(userInput);
	}

	public void changeMineButtonImage(final GameLogic.GameStates gameState) {
		Runnable uiRunnable = new Runnable() {
			@Override
			public void run() {
				int buttonImage;
				ImageButton mineButton = ((ImageButton) LevelActivity.this
						.findViewById(R.id.mineBtn));
				if (gameState.equals(GameLogic.GameStates.MINE_MODE)) {
					buttonImage = R.drawable.mine_btn;
					mineButton.setImageResource(buttonImage);
				} else if (gameState.equals(GameLogic.GameStates.MOVE_MODE)) {
					buttonImage = R.drawable.ship_btn;
					mineButton.setImageResource(buttonImage);
				}
			}
		};
		this.runOnUiThread(uiRunnable);
	}

	@Override
	public void restartBtn(View restartBtn) {
		DialogFragment restartConfirmFrag = new RestartConfirmFrag();
		restartConfirmFrag.show(getSupportFragmentManager(),
				"restartConfirmFrag");
	}

	@Override
	public void undoBtn(View undoBtn) {
		TileView tileView = (TileView) findViewById(R.id.map);
		Runnable userInput = new Runnable() {
			@Override
			public void run() {
				gameLogic.undo();
			}
		};
		tileView.addUserInput(userInput);
	}

	@Override
	public void endBtn(View endBtnView) {
		TileView tileView = (TileView) findViewById(R.id.map);
		Runnable userInput = new Runnable() {
			@Override
			public void run() {
				gameLogic.endTurn();
			}
		};
		tileView.addUserInput(userInput);
	}

	// various ingame dialoge fragment Callbacks
	@Override
	public void mainMenu() {
		// setResult(EXITED_LEVEL);
		Intent mainMenu = new Intent(this, MenuActivity.class);
		startActivity(mainMenu);
		this.finish();
	}

	@Override
	public void exit() {
		// setResult(EXITED_APP);
		this.finish();
	}

	@Override
	public void levelRetry() {
		Intent userChoice = new Intent(this, LevelActivity.class);
		userChoice.putExtra(MenuActivity.LEVEL_ID_EXTRA, mapNum);
		startActivity(userChoice);
		this.finish();
	}

	@Override
	public void nextLevel() {
		Intent userChoice = new Intent(this, LevelActivity.class);
		userChoice.putExtra(MenuActivity.LEVEL_ID_EXTRA, mapNum + 1);
		startActivity(userChoice);
		this.finish();
	}

	@Override
	public LogicCallbacks getLogicInstance() {
		return gameLogic;
	}
}
