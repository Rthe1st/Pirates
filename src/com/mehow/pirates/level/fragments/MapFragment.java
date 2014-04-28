package com.mehow.pirates.level.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mehow.pirates.R;

public class MapFragment extends Fragment{
    //call backs from tile view propergate here?
	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState){
		return inflater.inflate(R.layout.map_fragment, container, false);
	}
}
