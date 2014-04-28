package com.mehow.pirates.level.fragments.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.mehow.pirates.R;

public class GameOverFragment extends DialogFragment{

    public Callbacks mCallbacks;

    public interface Callbacks{
        public void mainMenu();
        public void levelRetry();
    };

    @Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState){
		View inflatedView = inflater.inflate(R.layout.gameover_layout, container, false);
        this.setCancelable(false);
		Button menuBtn = (Button)inflatedView.findViewById(R.id.menuBtn);
        menuBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                gameOverMenu(v);
            }
        });
        Button retryBtn = (Button)inflatedView.findViewById(R.id.retryBtn);
        retryBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                gameOverRetry(v);
            }
        });
        return inflatedView;
	}

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement GameOverFragment Fragments callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    };

	public void gameOverMenu(View view){
		mCallbacks.mainMenu();
	}
	public void gameOverRetry(View view){
		mCallbacks.levelRetry();
	}
}
