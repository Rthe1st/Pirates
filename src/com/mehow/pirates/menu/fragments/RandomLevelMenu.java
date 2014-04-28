package com.mehow.pirates.menu.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mehow.pirates.R;

public class RandomLevelMenu extends Fragment{
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState){
		return inflater.inflate(R.layout.randlevel_layout, container, false);
	}
}
