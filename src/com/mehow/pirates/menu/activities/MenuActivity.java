package com.mehow.pirates.menu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageButton;

import com.mehow.pirates.level.activites.LevelActivity;
import com.mehow.pirates.R;
import com.mehow.pirates.database.CustomLevelDbUIFunctions;
import com.mehow.pirates.database.DefaultLevelDatabaseUIFunctions;
import com.mehow.pirates.menu.fragments.LevelMenu;
import com.mehow.pirates.menu.fragments.MenuList;
import com.mehow.pirates.menu.fragments.RandomLevelMenu;
import com.mehow.pirates.menu.fragments.SettingsMenu;
import com.mehow.pirates.menu.fragments.StatsMenu;

public class MenuActivity extends FragmentActivity
   implements MenuList.Callbacks, LevelMenu.Callbacks{
	public static final String FRAG_CONTENT_TAG = "menuContentFrag";
	public static final String MAP_CHOICE_EXTRA = "MAP_CHOICE";
	public static final String LEVEL_FRAG_KEY = "MENU_LIST_FRAGMENT";
	public static final int LEVEL_FRAG_VAL = 2;
	public static int mapChoice = 1;//level currently selected
	public DefaultLevelDatabaseUIFunctions dbUi;
	public CustomLevelDbUIFunctions customDbUI;
	
	//public static final int MAIN_ACTIVITY_RC = 1;//RC stands for request code
	private static final String FRAG_TYPE = "FRAG_TYPE";
	
	private static int fragType = -1;//currently selected frag type
	
    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	System.out.println("intent val: "+this.getIntent().getIntExtra(LEVEL_FRAG_KEY, -1));
    	setFragType(this.getIntent().getIntExtra(LEVEL_FRAG_KEY, -1));
        dbUi = new DefaultLevelDatabaseUIFunctions(this, null);
        customDbUI = new CustomLevelDbUIFunctions(this,null);
        if(savedInstanceState != null){
        	fragType = savedInstanceState.getInt(FRAG_TYPE);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);
    }
    //overridden so that levelsMenu is always shown at start
    @Override
    public void onStart(){
    	super.onStart();
    	if(getFragType() == -1){
        	attachMenuListFrag();
    	}else if(getFragType() == 2){
    		attachLevelFrag();
    	}

    }
    @Override
    public void onResume(){
    	super.onResume();
  //     ((MenuBkSurfaceView)this.findViewById(R.id.background)).resume();
    }
    @Override
    public void onStop(){
    	super.onStop();
    	dbUi.closeDb();
    	customDbUI.closeDb();
    }
    @Override
    public void onRestoreInstanceState(Bundle  savedInstanceState){
    	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    	switch(fragType){
    		case 1: SettingsMenu settingsFrag = new SettingsMenu();
    					fragmentTransaction.replace(R.id.menuContentFrame, settingsFrag, FRAG_CONTENT_TAG);
    					break;
    		case 2: attachLevelFrag();
    					break;
    		case 3: RandomLevelMenu randFrag = new RandomLevelMenu();
						fragmentTransaction.replace(R.id.menuContentFrame, randFrag, FRAG_CONTENT_TAG);
    					break;
    		case 4: StatsMenu statsFrag = new StatsMenu();
    					fragmentTransaction.replace(R.id.menuContentFrame, statsFrag, FRAG_CONTENT_TAG);
    					break;
    		default: attachMenuListFrag();
    	}
    	fragmentTransaction.commit();
    }
    private void attachLevelFrag(){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LevelMenu fragment = new LevelMenu();
        fragmentTransaction.replace(R.id.menuContentFrame, fragment, FRAG_CONTENT_TAG);
        fragmentTransaction.commit();
        setFragType(LEVEL_FRAG_VAL);
        backBtnEnabled(true);
    }
    private void attachMenuListFrag(){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MenuList fragment = new MenuList();
        fragmentTransaction.replace(R.id.menuContentFrame, fragment, FRAG_CONTENT_TAG);
        fragmentTransaction.commit();
        setFragType(0);
        backBtnEnabled(false);
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
    	savedInstanceState.putInt(FRAG_TYPE, fragType);
    }
    public Fragment getContentFrag(){
    	return this.getSupportFragmentManager().findFragmentByTag(FRAG_CONTENT_TAG);
    }
    /*
    View parameters not used because they are automatically required when calling from layout file onClick
    resolve by setting up custom listners?
    */
    public void menuListLayout(View view){
    	attachMenuListFrag();
    }

    private void backBtnEnabled(boolean state){
    	ImageButton btn = (ImageButton)this.findViewById(R.id.menuBkBtn);
    	btn.setEnabled(state);
    }

    private void setFragType(int type){
    	fragType = type;
    }
    private int getFragType(){
    	return fragType;
    }
    @Override
    public void onBackPressed(){
    	if(getFragType() != 0){
    		menuListLayout(new View(this));
    	}else{
    		this.finish();
    	}
    }
    /*
    public void editLevel(View view){
    	/*Intent userChoice = new Intent(this, LevelActivity.class);
    	userChoice.putExtra(MAP_CHOICE_EXTRA, mapChoice);
    	startActivity(userChoice);
    	this.finish();
    }
    public void createLevel(View view){
    	/*Intent userChoice = new Intent(this, LevelActivity.class);
    	userChoice.putExtra(MAP_CHOICE_EXTRA, mapChoice);
    	startActivity(userChoice);
    	this.finish();
    }*/
    public void clearData(View view){
    	dbUi.clearScores();
    }
    //-----------------------------------------
    //MenuList interface
    //-----------------------------------------
    @Override
    public void settingsLayout(View view){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SettingsMenu fragment = new SettingsMenu();
        fragmentTransaction.replace(R.id.menuContentFrame, fragment, FRAG_CONTENT_TAG);
        fragmentTransaction.commit();
        setFragType(1);
        backBtnEnabled(true);
    }
    @Override
    public void levelsLayout(View view){
        attachLevelFrag();
    }
    @Override
    public void randlevelLayout(View view){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        RandomLevelMenu fragment = new RandomLevelMenu();
        fragmentTransaction.replace(R.id.menuContentFrame, fragment, FRAG_CONTENT_TAG);
        fragmentTransaction.commit();
        setFragType(3);
        backBtnEnabled(true);
    }
    @Override
    public void statsLayout(View view){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        StatsMenu fragment = new StatsMenu();
        fragmentTransaction.replace(R.id.menuContentFrame, fragment, FRAG_CONTENT_TAG);
        fragmentTransaction.commit();
        setFragType(4);
        backBtnEnabled(true);
    }
    /*
    @Override
    public void customLevelsLayout(View view){
    	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		CustomLevelsMenu fragment = new CustomLevelsMenu();
    	fragmentTransaction.replace(R.id.menuContentFrame, fragment, FRAG_CONTENT_TAG);
    	fragmentTransaction.commit();
    }*/
    //-------------------------------
    //interface for LevelMenu
    //-------------------------------
    @Override
    public void startLevel(View view){
        Intent userChoice = new Intent(this, LevelActivity.class);
        userChoice.putExtra(MAP_CHOICE_EXTRA, mapChoice);
        startActivity(userChoice);
        this.finish();
    }
}
