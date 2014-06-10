package com.mehow.pirates.menu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.mehow.pirates.LevelInfo;
import com.mehow.pirates.R;
import com.mehow.pirates.database.DatabaseHelper;
import com.mehow.pirates.database.LevelsTable;
import com.mehow.pirates.database.LevelsTable.LevelTypes;
import com.mehow.pirates.level.activites.CustomLevelActivity;
import com.mehow.pirates.level.activites.LevelActivity;
import com.mehow.pirates.menu.fragments.CustomLevelsMenu;
import com.mehow.pirates.menu.fragments.LevelMenu;
import com.mehow.pirates.menu.fragments.MenuList;
import com.mehow.pirates.menu.fragments.RandomLevelMenu;
import com.mehow.pirates.menu.fragments.SettingsMenu;
import com.mehow.pirates.menu.fragments.StatsMenu;
import com.mehow.pirates.menu.leveldata.LevelIconAdapter;

public class MenuActivity extends FragmentActivity implements
		MenuList.Callbacks, LevelMenu.Callbacks, CustomLevelsMenu.Callbacks, LevelIconAdapter.Callbacks {

	public static final String LEVEL_ID_EXTRA = "LEVEL_ID";
	//set my gridview adapters (LevelIconAdapter) via callbacks
	public long levelId;// level currently selected
	public DatabaseHelper databaseHelper;
	
	// public static final int MAIN_ACTIVITY_RC = 1;//RC stands for request code
	
	private static final String CONTENT_FRAGMENT_CLASS = "CONTENT_FRAGMENT_CLASS"; 
	
	public static final String FRAG_CONTENT_TAG = "CONTENT_FRAGMENT";

	private FragmentManager fragmentManager = getSupportFragmentManager();

	LevelIconAdapter levelIconAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		databaseHelper = DatabaseHelper.getInstance(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_layout);
		if (savedInstanceState != null) {
			onRestoreInstanceState(savedInstanceState);
		} else {
			setContentFragment(new MenuList());
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		//not closing may be bad practise
		//databaseHelper.close();
		//databaseHelper.clearDatabase();
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		//this is dodgy because it could be null
		levelIconAdapter = (LevelIconAdapter)savedInstanceState.getSerializable("ADAPTER");
		@SuppressWarnings("unchecked")
		Class<Fragment> contentFragmentClass = (Class<Fragment>)savedInstanceState.getSerializable(CONTENT_FRAGMENT_CLASS);
		try {
			setContentFragment(contentFragmentClass.newInstance());
		} catch (IllegalAccessException e) {
			Log.i("MenuActivity", e.getClass().getSimpleName()+" trying to get constructor for "+contentFragmentClass.getSimpleName());
			Log.i("MenuActivty", "Attemping to restore contentfragment to MenuList instead");
			e.printStackTrace();
			setContentFragment(new MenuList());
		} catch (InstantiationException e) {
			Log.i("MenuActivity", e.getClass().getSimpleName()+" trying to get constructor for "+contentFragmentClass.getSimpleName());
			Log.i("MenuActivty", "Attemping to restore contentfragment to MenuList instead");
			e.printStackTrace();
			setContentFragment(new MenuList());
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putSerializable(CONTENT_FRAGMENT_CLASS, getContentFrag().getClass());
		savedInstanceState.putSerializable("ADAPTER", levelIconAdapter);
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
			setContentFragment(new MenuList());
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

	// -----------------------------------------
	// MenuList interface
	// ----------------------------------------
	@Override
	public void settingsLayout(View view) {
		setContentFragment(new SettingsMenu());
	}

	@Override
	public void levelsLayout(View view) {
		levelIconAdapter = new LevelIconAdapter(LevelsTable.LevelTypes.PRE_MADE, this);
		setContentFragment(new LevelMenu());
	}

	@Override
	public void randlevelLayout(View view) {
		setContentFragment(new RandomLevelMenu());
	}

	@Override
	public void statsLayout(View view) {
		setContentFragment(new StatsMenu());
	}

	@Override
	public void customLevelsLayout(View view) {

		levelId = 1;// delay until loading levels fragments?
		levelIconAdapter = new LevelIconAdapter(LevelsTable.LevelTypes.CUSTOM, this);
		setContentFragment(new CustomLevelsMenu());
	}
	
	@Override
	public void startLevel(View view) {
		Intent userChoice = new Intent(this, LevelActivity.class);
		userChoice.putExtra(LEVEL_ID_EXTRA, levelId);
		startActivity(userChoice);
		this.finish();
	}

	@Override
	public void setLevelChoice(long tLevelId) {
		levelId = tLevelId;
	}

	@Override
	public LevelInfo[] getLevelInfos(LevelTypes type) {
		return databaseHelper.levelsTable.getLevelInfos(type);
	}

	@Override
	public void createLevel(View view) {
		Intent userChoice = new Intent(this, CustomLevelActivity.class);
		startActivity(userChoice);
		this.finish();
	}

	@Override
	public void editLevel(View view) {
		Intent userChoice = new Intent(this, CustomLevelActivity.class);
		userChoice.putExtra(LEVEL_ID_EXTRA, levelId);
		Log.i("MenuActivity", "map choice: "+levelId);
		startActivity(userChoice);
		this.finish();
	}

	@Override
	public void deleteLevel(View view){
		databaseHelper.levelsTable.deleteLevel(levelId);
		levelIconAdapter.refreshData();
	}

	@Override
	public LevelIconAdapter getLevelIconAdapter() {
		return levelIconAdapter;
	}
	
}
