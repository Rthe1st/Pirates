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

public class RestartConfirmFrag extends DialogFragment{

    public Callbacks mCallbacks;

    public interface Callbacks{
        public void levelRetry();
    };

    @Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState){
		View inflatedView =  inflater.inflate(R.layout.restart_confirm_layout, container, false);
	    Button confirmYes = (Button) inflatedView.findViewById(R.id.restart_confirm_yes);
	    confirmYes.setOnClickListener(new OnClickListener() {
	    		public void onClick(View v) {
	    			confirmYes(v);
	    		}
	    	});
	    Button confirmNo = (Button) inflatedView.findViewById(R.id.restart_confirm_no);
	    confirmNo.setOnClickListener(new OnClickListener() {
	    		public void onClick(View v) {
	    			dismiss();
	    		}
	    	});
	    return inflatedView;
	}
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement RestartConfirmFrag fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }
	private void confirmYes(View view){
		mCallbacks.levelRetry();
	}
}
