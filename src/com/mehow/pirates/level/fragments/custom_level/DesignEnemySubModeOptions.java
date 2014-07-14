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

public class DesignEnemySubModeOptions extends Fragment {	
	
    public Callbacks mCallbacks;

    public interface Callbacks {
    	public void venemyBtn(View view);
    	public void henemyBtn(View view);
    	public void aenemyBtn(View view);
    	public void pathEnemyBtn(View view);
    	public void undoBtn(View view);
    };
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState){
		Configuration config = this.getResources().getConfiguration();
			View inflatedView = inflater.inflate(R.layout.design_enemy_sub_mode_options, container, false);
			LinearLayout castLayout = ((LinearLayout)inflatedView.findViewById(R.id.enemySubMode));
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
