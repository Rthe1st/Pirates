package com.mehow.pirates.level.fragments.custom_level;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mehow.pirates.R;

public class DesignMapModeOptions extends Fragment{
	
    public Callbacks mCallbacks;

    public interface Callbacks {
    	public void enemyModeBtn(View view);
    	public void playerModeBtn(View view);
    	public void tileModeBtn(View view);
    	public void settingsBtn(View view);
    	public void deleteBtn(View view);
    };
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState){
		Configuration config = this.getResources().getConfiguration();
			View inflatedView = inflater.inflate(R.layout.design_map_mode_options, container, false);
			LinearLayout castLayout = ((LinearLayout)inflatedView.findViewById(R.id.designMapModeOptions));
			if(config.orientation == Configuration.ORIENTATION_PORTRAIT){
				castLayout.setOrientation(LinearLayout.HORIZONTAL);
			}else{
				castLayout.setOrientation(LinearLayout.VERTICAL);				
			}
		return inflatedView;
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
