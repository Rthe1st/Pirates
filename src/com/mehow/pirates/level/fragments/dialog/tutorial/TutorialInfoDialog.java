package com.mehow.pirates.level.fragments.dialog.tutorial;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mehow.pirates.R;

//to fix tutorials: add tutorial data as element of mapdata. Requires better mapdata level elelment in reasource file
//                  or a new (non-resource file) way of loading levels.
public class TutorialInfoDialog extends DialogFragment{
	private static String TUTORIAL_NUM = "tutorial_number";
	private static String SLIDES_NUM = "slides_number";
	private static String resIDpre = "tut";
	private static String resIDmid = "_";
	private static String resIDpost = "Info";
    public static TutorialInfoDialog newInstance(int tutNum, int numOfSlides) {
        TutorialInfoDialog f = new TutorialInfoDialog();
        // Supply tutnum input as an argument.
        Bundle args = new Bundle();
        System.out.println("tutNum in newInstacne: "+tutNum);
        args.putInt(TUTORIAL_NUM, tutNum);
        args.putInt(SLIDES_NUM, numOfSlides);
        f.setArguments(args);
        return f;
    }
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState){
		View inflatedView;
		Button tutButton;
		final int tutorialNum = this.getArguments().getInt(TUTORIAL_NUM);
		final int slidesNum = this.getArguments().getInt(SLIDES_NUM);
		String[] tutData = getTutArray(tutorialNum, slidesNum);
		int stringID = this.getResources().getIdentifier(tutData[0], "string" , "com.mehow.pirates");
		String tutText = this.getResources().getString(stringID);
		int picID;
		//System.out.println("tutdata length: "+tutData.length);
		if(tutData.length == 2){
		System.out.println("pic name: "+tutData[1]);
			picID = this.getResources().getIdentifier(tutData[1], "drawable" , "com.mehow.pirates");
		}else{
			picID = 0;
		}
		if(slidesNum == 1){
			inflatedView = inflater.inflate(R.layout.tutorial_dialog_last, container, false);
			tutButton = (Button)inflatedView.findViewById(R.id.tutStartLevelBtn);
	        tutButton.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	                startLevel(v);
	            }
	        });
		}else{
			inflatedView = inflater.inflate(R.layout.tutorial_dialog, container, false);
			tutButton = (Button)inflatedView.findViewById(R.id.tutNextSlideBtn);
	        tutButton.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	                nextSlide(v, slidesNum-1, tutorialNum);
	            }
	        });
		}
		this.setCancelable(false);
        TextView infoText = (TextView)inflatedView.findViewById(R.id.tutorialText);
        infoText.setText(tutText);
        if(picID != 0){
        	ImageView infoPic = (ImageView)inflatedView.findViewById(R.id.tutorialPic);
        	infoPic.setBackgroundResource(picID);
        	 // Get the background, which has been compiled to an AnimationDrawable object.
        	final AnimationDrawable frameAnimation = (AnimationDrawable) infoPic.getBackground();

        	 // Start the animation (looped playback by default).
        	infoPic.post(new Runnable(){
        	    public void run(){
        	        frameAnimation.start();
        	    }
        	});
        }
        return inflatedView;
	}
	private String[] getTutArray(int tutNum, int slideNum){
        //replace swithc with if-else
		int stringID;
		String resIdentifier;
		switch(tutNum){
			case 1: resIdentifier = resIDpre+1;break;
			case 2: resIdentifier = resIDpre+2;break;
			case 3: resIdentifier = resIDpre+3;break;
			case 4: resIdentifier = resIDpre+4;break;
			case 5: resIdentifier = resIDpre+5;break;
			default: resIdentifier = resIDpre+0;System.out.println("add another case to switch in tutinfoDialog dumbass");
		}
		switch(slideNum){
			case 1: resIdentifier += resIDmid+1;break;
			case 2: resIdentifier += resIDmid+2;break;
			case 3: resIdentifier += resIDmid+3;break;
			case 4: resIdentifier += resIDmid+4;break;
			case 5: resIdentifier += resIDmid+5;break;
			default: resIdentifier += resIDpre+0;System.out.println("add another case to switch in tutinfoDialog dumbass");
		}
		stringID = this.getResources().getIdentifier(resIdentifier, "array" , "com.mehow.pirates");
		return this.getResources().getStringArray(stringID);
	}
	public void startLevel(View view){
		this.dismiss();
	}
	public void nextSlide(View view, int slidesLeft, int tutorialNum){
		TutorialInfoDialog tutDialog = TutorialInfoDialog.newInstance(tutorialNum, slidesLeft);
		tutDialog.show(this.getActivity().getSupportFragmentManager(), "tutDialog");
		this.dismiss();
	}
}
