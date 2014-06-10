package com.mehow.pirates.level.fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.mehow.pirates.R;

public class MapRightOptions extends Fragment{

    public Callbacks mCallbacks;

    public interface Callbacks {
        public void mineBtn(View mineBtnView);
        public void endBtn(View endBtnView);
        public void undoBtn(View undoBtn);
        public void restartBtn(View restartBtn);
        public void changeMineBtnState(boolean state);
    };

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState){
		System.out.println("create viewghug");
		Configuration config = this.getResources().getConfiguration();
			View inflatedView = inflater.inflate(R.layout.map_right_options, container, false);
			LinearLayout castLayout = ((LinearLayout)inflatedView.findViewById(R.id.mapRightOptionsLayout));
			LayoutParams restartBtnParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			if(config.orientation == Configuration.ORIENTATION_PORTRAIT){
				castLayout.setOrientation(LinearLayout.HORIZONTAL);
			}else{
				castLayout.setOrientation(LinearLayout.VERTICAL);				
			}
			castLayout.findViewById(R.id.restartBtn).setLayoutParams(restartBtnParams);
		return inflatedView;
	}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement LevelMenu fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

	public void changeMineBtnState(boolean state){
		this.getView().findViewById(R.id.mineBtn).setEnabled(state);
	}
}
