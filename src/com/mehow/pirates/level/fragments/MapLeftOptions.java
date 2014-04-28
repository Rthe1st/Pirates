package com.mehow.pirates.level.fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mehow.pirates.R;

public class MapLeftOptions extends Fragment{
	
	TextView minesLeft;
	TextView turnsTaken;
	TextView score;
	TextView highscore;
	View inflatedView;

    public Callbacks mCallbacks;

    public interface Callbacks{

    };

	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState){
		Configuration config = this.getResources().getConfiguration();
		View layout = inflater.inflate(R.layout.map_left_options, container, false);
		if(config.orientation == Configuration.ORIENTATION_PORTRAIT){
			((LinearLayout)layout.findViewById(R.id.mapLeftOptionsLayout)).setOrientation(LinearLayout.HORIZONTAL);
		}

		return layout;
	}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement MapLeftOptions fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }
	public void updateMines(int tMinesLeft){
		//System.out.println("getview(): "+getView()+" id:"+getView().getId()+" minescountid:"+getView().findViewById(R.id.minesCount));
		minesLeft = (TextView)getView().findViewById(R.id.minesCount);
		minesLeft.setText(String.valueOf(tMinesLeft), TextView.BufferType.NORMAL);
	}
	public void updateTurns(int tTurnsTaken){
		turnsTaken = (TextView)getView().findViewById(R.id.turnsCount);
		turnsTaken.setText(String.valueOf(tTurnsTaken), TextView.BufferType.NORMAL);
	}
	public void updateScore(int tScore){
		score = (TextView)getView().findViewById(R.id.scoreCount);
		score.setText(String.valueOf(tScore), TextView.BufferType.NORMAL);
	}
	public void setHighscoreDisplay(int tHighscore){
		highscore = (TextView)getView().findViewById(R.id.highscoreData);
		highscore.setText(String.valueOf(tHighscore), TextView.BufferType.NORMAL);
	}
}
