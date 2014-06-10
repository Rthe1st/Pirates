package com.mehow.pirates.level.fragments.custom_level;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.mehow.pirates.LevelInfo;
import com.mehow.pirates.R;

public class DesignSettingSubModeOptions extends Fragment {	
	
    public Callbacks mCallbacks;

    public interface Callbacks {
    	public void venemyBtn(View view);
    	public void henemyBtn(View view);
    	public void aenemyBtn(View view);
    	public void undoBtn(View view);
    	public void updateLevelInfo(int bronze, int silver, int gold, String name, int mines);
    	public LevelInfo getLevelInfo();
    };
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState){
		Configuration config = this.getResources().getConfiguration();
			View inflatedView = inflater.inflate(R.layout.design_settings_sub_mode_options, container, false);
			LinearLayout castLayout = ((LinearLayout)inflatedView.findViewById(R.id.settingsSubMode));
			if(config.orientation == Configuration.ORIENTATION_PORTRAIT){
				castLayout.setOrientation(LinearLayout.HORIZONTAL);
			}else{
				castLayout.setOrientation(LinearLayout.VERTICAL);
			}
			TextWatcher numberTextWatcher = new TextWatcher(){

				@Override
				public void afterTextChanged(Editable s) {
					if(s.toString() == ""){
						s.append("0");//to prevent nullpointer with parseInt
					}
					updateLevelInfo(); 
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {}

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {}
				
			};
			TextWatcher stringTextWatcher = new TextWatcher(){

				@Override
				public void afterTextChanged(Editable s) {
					updateLevelInfo();
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {}

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {}
				
			};
			LevelInfo levelInfo = mCallbacks.getLevelInfo();
			EditText bronzeScore = ((EditText)inflatedView.findViewById(R.id.bronzeScore));
			bronzeScore.setText(String.valueOf(levelInfo.bronzeScore));
			bronzeScore.addTextChangedListener(numberTextWatcher);
			EditText silverScore = ((EditText)inflatedView.findViewById(R.id.silverScore));
			silverScore.setText(String.valueOf(levelInfo.silverScore));
			silverScore.addTextChangedListener(numberTextWatcher);
			EditText goldScore = ((EditText)inflatedView.findViewById(R.id.goldScore));
			goldScore.setText(String.valueOf(levelInfo.goldScore));
			goldScore.addTextChangedListener(numberTextWatcher);
			EditText mineAmount = ((EditText)inflatedView.findViewById(R.id.mineAmount));
			mineAmount.setText(String.valueOf(levelInfo.mineLimit));
			mineAmount.addTextChangedListener(numberTextWatcher);
			EditText levelName = ((EditText)inflatedView.findViewById(R.id.levelName));
			levelName.setText(levelInfo.name);
			levelName.addTextChangedListener(stringTextWatcher);
		return inflatedView;
	}
	
	private void updateLevelInfo(){
		//update all attributes per change is very inefficent, but avoid multiple callback functions
		Activity activity = getActivity();
		int bronze = Integer.parseInt(((EditText)activity.findViewById(R.id.bronzeScore)).getText().toString());
		int silver = Integer.parseInt(((EditText)activity.findViewById(R.id.silverScore)).getText().toString());
		int gold = Integer.parseInt(((EditText)activity.findViewById(R.id.goldScore)).getText().toString());
		int mines = Integer.parseInt(((EditText)activity.findViewById(R.id.mineAmount)).getText().toString());
		String name = ((EditText)activity.findViewById(R.id.levelName)).getText().toString();
		mCallbacks.updateLevelInfo(bronze, silver, gold, name, mines);
	}
	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement "+this.getClass()+" fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }
}
