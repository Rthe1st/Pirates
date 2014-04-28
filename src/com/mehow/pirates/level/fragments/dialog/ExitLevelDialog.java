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

public class ExitLevelDialog extends DialogFragment{

    public Callbacks mCallbacks;

    public interface Callbacks{
        public void mainMenu();
        public void exit();
    };

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState){
		View inflatedView = inflater.inflate(R.layout.exit_level_layout, container, false);
        this.setCancelable(true);//pressed back twice resumes game
		Button resumeBtn = (Button)inflatedView.findViewById(R.id.resumeLevelBtn);
        resumeBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                resumeLevel(v);
            }
        });
        Button exitBtn = (Button)inflatedView.findViewById(R.id.menuBtn);
        exitBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                exitLevel(v);
            }
        });
        Button exitAppBtn = (Button)inflatedView.findViewById(R.id.exitAppBtn);
        exitAppBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                exitApp(v);
            }
        });
        return inflatedView;
	}

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement ExitLevelDialog dialogs callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    };
	public void resumeLevel(View view){
		this.dismiss();
	}
	public void exitLevel(View view){
		mCallbacks.mainMenu();
	}
	public void exitApp(View view){
		mCallbacks.exit();
		this.dismiss();
	}
	
}

