package com.mehow.pirates.menu.fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.mehow.pirates.R;
import com.mehow.pirates.menu.leveldata.LevelIconAdapter;
import com.mehow.pirates.menu.leveldata.LevelInfoLayout;

//CURRENTLY DOESNT CHANGE LAYOUT BASED ON ROTATION
//BECAUSE ONCREATEVIEW NOT CALLED WHEN ROTATED, ONLY WHEN MINIMISED ETC
//fix dis
public class LevelMenu extends Fragment{
    GridView gridView;
    LevelInfoLayout levelInfo;
    LevelIconAdapter myAdapter;

    public interface Callbacks{
        public void startLevel(View view);
        public LevelIconAdapter getLevelIconAdapter();
    }
    Callbacks mCallbacks;

	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState){
		System.out.println("oncreatefrfg");
        View view;

        Configuration config = this.getResources().getConfiguration();
        
        if(config.orientation == Configuration.ORIENTATION_PORTRAIT){
        	view = inflater.inflate(R.layout.levels_layout_vertical,container,false);
        }else{
        	view = inflater.inflate(R.layout.levels_layout_horizontal,container,false);
        }
        
        levelInfo = (LevelInfoLayout)view.findViewById(R.id.levelInfo);
        gridView = (GridView)view.findViewById(R.id.levelIcons);
        
        //myAdapter = new LevelIconAdapter(mCallbacks.getLevelInfos(LevelsTable.LevelTypes.PRE_MADE));
        myAdapter = mCallbacks.getLevelIconAdapter();
        
        gridView.setAdapter(myAdapter);
        gridView.setOnItemClickListener(new OnItemClickListener(){
        	public void onItemClick(AdapterView<?> parent, 
            View v, int position, long id){
        		gridViewItemClickListener(parent, 
        	            v, position, id);
        	}
        });
        return view;
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
    
	public void gridViewItemClickListener(AdapterView<?> parent, 
            View v, int position, long id){
				myAdapter.updateSelected(position);
                levelInfo.changeInfo(myAdapter.getItem(position));
	}
}
