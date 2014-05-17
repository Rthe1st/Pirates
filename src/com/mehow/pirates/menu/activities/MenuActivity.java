package com.mehow.pirates.menu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.mehow.pirates.LevelInfo;
import com.mehow.pirates.R;
import com.mehow.pirates.level.activites.LevelActivity;
import com.mehow.pirates.menu.fragments.LevelMenu;
import com.mehow.pirates.menu.fragments.MenuList;
import com.mehow.pirates.menu.fragments.RandomLevelMenu;
import com.mehow.pirates.menu.fragments.SettingsMenu;
import com.mehow.pirates.menu.fragments.StatsMenu;
import com.mehow.pirates.database.DatabaseHelper;
import com.mehow.pirates.database.LevelsTable.LevelTypes;


// reflection could be used here, pass in a fragment class and
// class.newInstance()
// could throw errors though

//for this to be real helpful, a class mapping would have to be set up to match bundle strings to classes
//then the classwide contentFragmentType could be limited/removed
public class MenuActivity extends FragmentActivity implements
		MenuList.Callbacks, LevelMenu.Callbacks {

	// used by level activity to return to main screen
	public static final String LEVEL_FRAG_KEY = "MENU_LIST_FRAGMENT";
	public static final int LEVEL_FRAG_VAL = 2;

	public static final String MAP_CHOICE_EXTRA = "MAP_CHOICE";
	public int mapChoice;// level currently selected
	public DatabaseHelper databaseHelper;
	
	// public static final int MAIN_ACTIVITY_RC = 1;//RC stands for request code

	private static enum MenuFragmentType {
		LEVELS, RANDOM_LEVEL, STATS, SETTINGS, MENU
	}

	// try to keep this only used in android life cycle (out of app logic)
	private static MenuFragmentType contentFragmentType = MenuFragmentType.MENU;
	public static final String FRAG_CONTENT_TAG = "CONTENT_FRAGMENT";

	// bundle constants
	private static String CONTENT_FRAGMENT_TYPE = "CONTENT_FRAGMENT_TYPE";
	private FragmentManager fragmentManager = getSupportFragmentManager();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		//dbUi = new DefaultLevelDatabaseUIFunctions(this, null);
		databaseHelper = DatabaseHelper.getInstance(this);
		mapChoice = 1;// delay until loading levels fragments?
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_layout);
		if (savedInstanceState != null) {
			contentFragmentType = MenuFragmentType.valueOf(savedInstanceState
					.getString(CONTENT_FRAGMENT_TYPE));
			System.out.println("content frag type; "+contentFragmentType);
		} else {
			menuListLayout();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		System.out.println("content frag type; "+contentFragmentType);
		switch (contentFragmentType) {
		case SETTINGS:
			settingsLayout();
			break;
		case LEVELS:
			levelsLayout();
			break;
		case RANDOM_LEVEL:
			randlevelLayout();
			break;
		case STATS:
			statsLayout();
			break;
		default:
			menuListLayout();
		}
	}

	/*
	 * @Override public void onResume(){ super.onResume();
	 * ((MenuBkSurfaceView)this.findViewById(R.id.background)).resume(); }
	 */
	@Override
	public void onStop() {
		super.onStop();
		//not closing may be bad practise
		//databaseHelper.close();
		//databaseHelper.clearDatabase();
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		contentFragmentType = MenuFragmentType.valueOf(savedInstanceState
				.getString(CONTENT_FRAGMENT_TYPE));
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putString(CONTENT_FRAGMENT_TYPE,
				contentFragmentType.toString());
	}

	public Fragment getContentFrag() {
		return this.getSupportFragmentManager().findFragmentByTag(
				FRAG_CONTENT_TAG);
	}

	@Override
	public void onBackPressed() {
		if (getContentFrag().getClass().equals(MenuList.class)) {
			this.finish();
		} else {
			menuListLayout();
		}
	}

	public void clearData(View view) {
		databaseHelper.levelsTable.clearScores();
	}

	public <T extends Fragment> void setContentFragment(T fragment) {
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.menuContentFrame, fragment,
				FRAG_CONTENT_TAG);
		fragmentTransaction.commit();
	}

	public void menuListLayout() {
		MenuList fragment = new MenuList();
		setContentFragment(fragment);
		
	}

	// -----------------------------------------
	// MenuList interface
	// -----------------------------------------
	@Override
	public void settingsLayout(View view) {
		settingsLayout();
	}

	public void settingsLayout() {
		SettingsMenu fragment = new SettingsMenu();
		setContentFragment(fragment);
		contentFragmentType = MenuFragmentType.SETTINGS;
	}

	@Override
	public void levelsLayout(View view) {
		levelsLayout();
	}

	public void levelsLayout() {
		LevelMenu fragment = new LevelMenu();
		setContentFragment(fragment);
		contentFragmentType = MenuFragmentType.LEVELS;
	}

	@Override
	public void randlevelLayout(View view) {
		randlevelLayout();
	}

	public void randlevelLayout() {
		RandomLevelMenu fragment = new RandomLevelMenu();
		setContentFragment(fragment);
		contentFragmentType = MenuFragmentType.RANDOM_LEVEL;
	}

	@Override
	public void statsLayout(View view) {
		statsLayout();
	}

	public void statsLayout() {
		StatsMenu fragment = new StatsMenu();
		setContentFragment(fragment);
		contentFragmentType = MenuFragmentType.STATS;
	}

	/*
	 * @Override public void customLevelsLayout(View view){ FragmentTransaction
	 * fragmentTransaction = fragmentManager.beginTransaction();
	 * CustomLevelsMenu fragment = new CustomLevelsMenu();
	 * fragmentTransaction.replace(R.id.menuContentFrame, fragment,
	 * FRAG_CONTENT_TAG); fragmentTransaction.commit(); }
	 */
	// -------------------------------
	// interface for LevelMenu
	// -------------------------------
	@Override
	public void startLevel(View view) {
		Intent userChoice = new Intent(this, LevelActivity.class);
		userChoice.putExtra(MAP_CHOICE_EXTRA, mapChoice);
		startActivity(userChoice);
		this.finish();
	}

	@Override
	public int getMapChoice() {
		return mapChoice;
	}

	@Override
	public void setMapChoice(int tMapChoice) {
		mapChoice = tMapChoice;
	}

	@Override
	public LevelInfo[] getLevelInfos(LevelTypes type) {
		return databaseHelper.levelsTable.getLevelInfos(type);
	}

	/*
	 * public void editLevel(View view){ /*Intent userChoice = new Intent(this,
	 * LevelActivity.class); userChoice.putExtra(MAP_CHOICE_EXTRA, mapChoice);
	 * startActivity(userChoice); this.finish(); } public void createLevel(View
	 * view){ /*Intent userChoice = new Intent(this, LevelActivity.class);
	 * userChoice.putExtra(MAP_CHOICE_EXTRA, mapChoice);
	 * startActivity(userChoice); this.finish(); }
	 */
}
