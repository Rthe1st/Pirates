package com.mehow.pirates.level.fragments.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mehow.pirates.R;

public class LevelCompleteDialog extends DialogFragment{
	private static final String SCORE = "SCORE";

    public Callbacks mCallbacks;

    public interface Callbacks{
        public void mainMenu();
        public void levelRetry();
        public void nextLevel();
        public boolean isMaxLevel();
    };

    public static LevelCompleteDialog newInstance(int score) {
        LevelCompleteDialog f = new LevelCompleteDialog();
        // Supply tutnum input as an argument.
        Bundle args = new Bundle();
        args.putInt(SCORE, score);
        f.setArguments(args);
        return f;
    }
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState){
		View inflatedView;
		String dialogMode;
		int score;
		if(mCallbacks.isMaxLevel()){
			dialogMode = "AllLevelsComplete";
		}else{
			dialogMode = "singleLevelComplete";
		}
		if(dialogMode == "singleLevelComplete"){
			inflatedView = inflater.inflate(R.layout.level_complete, container, false);
	        Button nextLevelBtn = (Button)inflatedView.findViewById(R.id.nextLevelBtn);
	        nextLevelBtn.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	                nextLevel(v);
	            }
	        });
		}else{
			inflatedView = inflater.inflate(R.layout.all_levels_complete, container, false);
		}
        this.setCancelable(false);
        if(this.getArguments().containsKey(SCORE)){
        	score = this.getArguments().getInt(SCORE);
        	TextView scoreDisplay = (TextView)inflatedView.findViewById(R.id.scoreDisplay);
        	scoreDisplay.setText(String.valueOf(score));
        }
        Button menuBtn = (Button)inflatedView.findViewById(R.id.menuBtn);
        menuBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mainMenu(v);
            }
        });
        Button retryBtn = (Button)inflatedView.findViewById(R.id.retryBtn);
        retryBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                retryLevel(v);
            }
        });
        return inflatedView;
	}

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement LevelCompleteDialog dialogs callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    };

	public void mainMenu(View view){
		mCallbacks.mainMenu();
	}
	public void retryLevel(View view){
        mCallbacks.levelRetry();
	}
	public void nextLevel(View view){
        mCallbacks.nextLevel();
	}
}
