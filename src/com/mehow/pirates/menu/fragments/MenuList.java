package com.mehow.pirates.menu.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mehow.pirates.R;

public class MenuList extends Fragment{

    private Callbacks mCallbacks;

    public interface Callbacks {
        public void settingsLayout(View view);
        public void levelsLayout(View view);
        public void randlevelLayout(View view);
        public void statsLayout(View view);
        /*
        public void customLevelsLayout(View view);
        */
    };

	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState){
		View inflatedView = inflater.inflate(R.layout.menu_list,container,false);
		return inflatedView;
	}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement menulist fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }
}
