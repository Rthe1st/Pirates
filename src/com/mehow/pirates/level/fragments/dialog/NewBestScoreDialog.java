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

public class NewBestScoreDialog extends DialogFragment{

    public Callbacks mCallbacks;

    public interface Callbacks{
        public void submitScore(int newScore, String playerName);
    };

	int score;
	private static final String SCORE_KEY = "score";
	private View inflatedView;
    public static NewBestScoreDialog newInstance(int storedScore) {
        NewBestScoreDialog f = new NewBestScoreDialog();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt(SCORE_KEY, storedScore);
        f.setArguments(args);

        return f;
    }
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState){
		score = this.getArguments().getInt(SCORE_KEY);
		inflatedView = inflater.inflate(R.layout.submit_high_score, container, false);
	    TextView scoreDisplay = (TextView) inflatedView.findViewById(R.id.scoreDisplay);
	    scoreDisplay.setText(String.valueOf(score));
		Button submitBtn = (Button)inflatedView.findViewById(R.id.submitBtn);
	    submitBtn.setOnClickListener(new OnClickListener() {
	    		public void onClick(View v) {
	    			submitScore(v);
	    		}
	    	});
        this.setCancelable(false);
        return inflatedView;
	}
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement NewBestScoreDialog dialogs callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    };
	public void submitScore(View view){
        /*the following comments have been kept for nostalgic value

		//the following line is so shitty I'd laugh if I wasn't stuck with this useless brain
		//ok its better now but still lame
		*/
		TextView nameField = (TextView)inflatedView.findViewById(R.id.highScoreName);
		String name = String.valueOf(nameField.getText());
		mCallbacks.submitScore(score, name);
		this.dismiss();
	}

}
