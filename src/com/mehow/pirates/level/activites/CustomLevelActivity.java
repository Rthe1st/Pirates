package com.mehow.pirates.level.activites;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mehow.pirates.Consts;
import com.mehow.pirates.LevelInfo;
import com.mehow.pirates.R;
import com.mehow.pirates.database.DatabaseHelper;
import com.mehow.pirates.level.DesignLogic;
import com.mehow.pirates.level.TileView;
import com.mehow.pirates.level.TileView.LogicCallbacks;
import com.mehow.pirates.level.fragments.custom_level.DesignEnemySubModeOptions;
import com.mehow.pirates.level.fragments.custom_level.DesignMapModeOptions;
import com.mehow.pirates.level.fragments.custom_level.DesignSettingSubModeOptions;
import com.mehow.pirates.level.fragments.custom_level.DesignShipSubModeOptions;
import com.mehow.pirates.level.fragments.custom_level.DesignTileSubModeOptions;
import com.mehow.pirates.menu.activities.MenuActivity;

public class CustomLevelActivity extends FragmentActivity implements
		DesignLogic.Callbacks, DesignMapModeOptions.Callbacks,
		TileView.ActivityCallbacks, DesignEnemySubModeOptions.Callbacks,
		DesignShipSubModeOptions.Callbacks, DesignTileSubModeOptions.Callbacks,
		DesignSettingSubModeOptions.Callbacks {

	private DatabaseHelper databaseHelper;

	// used to display tutorial only if not restarting
	// intialy always true, gets changed soon as read by start()
	private boolean restartFlag = false;

	private static final String FRAGMENT_SUB_OPTIONS_TAG = "FRAGMENT_SUB_OPTIONS_TAG";

	// RESULT consts (to tell menuActivity if it should finish)
	public static int EXITED_APP = 2;
	public static int EXITED_LEVEL = 3;

	public DesignLogic designLogic;

	private FragmentManager fragmentManager = getSupportFragmentManager();

	private static final String SUBMODE_FRAGMENT_CLASS = "SUBMODE_FRAGMENT_CLASS";

	long levelId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent userChoice = getIntent();
		levelId = userChoice.getLongExtra(MenuActivity.LEVEL_ID_EXTRA, -1);
		databaseHelper = DatabaseHelper.getInstance(this);
		Log.i("CustomLevelActivity", "levelid extra: "+levelId);
		// --------
		if (savedInstanceState != null) {
			designLogic = new DesignLogic(this,
					savedInstanceState.getBundle(DesignLogic.BUNDLE_ID));
		} else {
			LevelInfo levelInfo;
			if (levelId == -1) {
				levelInfo = databaseHelper.levelsTable.createCustomLevel();
			} else {
				levelInfo = databaseHelper.levelsTable.getLevelInfo(levelId);
			}
			designLogic = new DesignLogic(this, levelInfo);
		}
		// ---------------
		Configuration config = getResources().getConfiguration();
		if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
			setContentView(R.layout.design_map_vertical);
		} else if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setContentView(R.layout.design_map_horizontal);
		} else {
			setContentView(R.layout.design_map_horizontal);
		}
		if (savedInstanceState != null) {
			onRestoreInstanceState(savedInstanceState);
		} else {
			setContentFragment(new DesignEnemySubModeOptions());
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		System.out.println("started");
	}

	@Override
	public void onStop() {
		super.onStop();
		saveMapToDatabase(designLogic.getLevelInfo());
		// not closing may be bad practie
		// databaseHelper.close();
	}

	private void saveMapToDatabase(LevelInfo levelInfo) {
		databaseHelper.levelsTable.updateLevel(levelInfo);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!restartFlag) {
			restartFlag = true;
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		restartFlag = savedInstanceState.getBoolean("restartFlag");
		@SuppressWarnings("unchecked")
		Class<Fragment> contentFragmentClass = (Class<Fragment>) savedInstanceState
				.getSerializable(SUBMODE_FRAGMENT_CLASS);
		try {
			setContentFragment(contentFragmentClass.newInstance());
		} catch (IllegalAccessException e) {
			Log.i(this.getClass().getSimpleName(),
					e.getClass().getSimpleName()
							+ " trying to get constructor for "
							+ contentFragmentClass.getSimpleName());
			Log.i(this.getClass().getSimpleName(),
					"Attemping to restore contentfragment to DesignEnemySubModeOptions instead");
			e.printStackTrace();
			setContentFragment(new DesignEnemySubModeOptions());
		} catch (InstantiationException e) {
			Log.i(this.getClass().getSimpleName(),
					e.getClass().getSimpleName()
							+ " trying to get constructor for "
							+ contentFragmentClass.getSimpleName());
			Log.i(this.getClass().getSimpleName(),
					"Attemping to restore contentfragment to DesignEnemySubModeOptions instead");
			e.printStackTrace();
			setContentFragment(new DesignEnemySubModeOptions());
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		System.out.println("saved");
		savedInstanceState.putBundle(DesignLogic.BUNDLE_ID,
				designLogic.saveState());
		savedInstanceState.putBoolean("restartFlag", restartFlag);
		savedInstanceState.putSerializable(SUBMODE_FRAGMENT_CLASS,
				getContentFrag().getClass());
		super.onSaveInstanceState(savedInstanceState);
	}

	public <T extends Fragment> void setContentFragment(T fragment) {
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.designMapSubModeOptions, fragment,
				FRAGMENT_SUB_OPTIONS_TAG);
		fragmentTransaction.commit();
	}

	public Fragment getContentFrag() {
		return this.getSupportFragmentManager().findFragmentByTag(
				FRAGMENT_SUB_OPTIONS_TAG);
	}

	@Override
	public void onBackPressed() {
		Intent mainMenu = new Intent(this, MenuActivity.class);
		// mainMenu.putExtra(MenuActivity.LEVEL_FRAG_KEY, MenuActivity.);
		startActivity(mainMenu);
		this.finish();
	}

	@Override
	public void undoBtn(View undoBtn) {
		designLogic.undo();
	}

	@Override
	public void enemyModeBtn(View view) {
		designLogic.setGameObjectSuperType(Consts.DesignModeSuperTypes.ENEMY);
		setContentFragment(new DesignEnemySubModeOptions());
	}

	@Override
	public void playerModeBtn(View view) {
		designLogic.setGameObjectSuperType(Consts.DesignModeSuperTypes.SHIP);
		setContentFragment(new DesignShipSubModeOptions());
	}

	@Override
	public void tileModeBtn(View view) {
		designLogic.setGameObjectSuperType(Consts.DesignModeSuperTypes.TILE);
		setContentFragment(new DesignTileSubModeOptions());
	}

	@Override
	public void settingsBtn(View view) {
		// should wipe on gameObject super/sub
		setContentFragment(new DesignSettingSubModeOptions());
	}

	public void mainMenu() {
		// done in stop anway? done here because im tired and cba to check if
		// finish causes stop before new activity
		this.databaseHelper.levelsTable.updateLevel(this.getLevelInfo());
		// setResult(EXITED_LEVEL);
		Intent mainMenu = new Intent(this, MenuActivity.class);
		startActivity(mainMenu);
		this.finish();
	}

	@Override
	public LogicCallbacks getLogicInstance() {
		return designLogic;
	}

	@Override
	public void venemyBtn(View view) {
		designLogic.setGameObjectSubType(Consts.DesignModeSubTypes.VENEMY);
	}

	@Override
	public void henemyBtn(View view) {
		designLogic.setGameObjectSubType(Consts.DesignModeSubTypes.HENEMY);
	}

	@Override
	public void aenemyBtn(View view) {
		designLogic.setGameObjectSubType(Consts.DesignModeSubTypes.AENEMY);
	}

	@Override
	public void pathEnemyBtn(View view) {
		designLogic.setGameObjectSubType(Consts.DesignModeSubTypes.PATHENEMY);
	}
	
	@Override
	public void rockBtn(View view) {
		designLogic.setGameObjectSubType(Consts.DesignModeSubTypes.ROCK);
	}

	@Override
	public void seaBtn(View view) {
		designLogic.setGameObjectSubType(Consts.DesignModeSubTypes.SEA);
	}

	@Override
	public void goalBtn(View view) {
		designLogic.setGameObjectSubType(Consts.DesignModeSubTypes.GOAL);
	}

	@Override
	public void shipBtn(View view) {
		designLogic.setGameObjectSubType(Consts.DesignModeSubTypes.SHIP);
	}

	@Override
	public void deleteBtn(View view){
		designLogic.setGameObjectSuperType(Consts.DesignModeSuperTypes.DELETE);
	}
	
	@Override
	public void selectBtn(View view){
		designLogic.setGameObjectSuperType(Consts.DesignModeSuperTypes.SELECT);
	}
	
	@Override
	public void updateScreen(boolean animate) {
		TileView tileView = (TileView) findViewById(R.id.map);
		tileView.animationLogic.updateScreen(animate);
	}

	@Override
	public void updateLevelInfo(int bronze, int silver, int gold, String name,
			int mines) {
		designLogic.updateLevelInfo(bronze, silver, gold, name, mines);
	}

	@Override
	public LevelInfo getLevelInfo() {
		return designLogic.getLevelInfo();
	}
	
	@Override
	public void showToast(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
