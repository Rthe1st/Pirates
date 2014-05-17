package com.mehow.pirates.menu.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.mehow.pirates.R;
import com.mehow.pirates.menu.leveldata.LevelIconAdapter;
import com.mehow.pirates.menu.leveldata.LevelInfoLayout;

public class CustomLevelsMenu extends Fragment{
	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.custom_levels_layout,container,false);
        final LevelInfoLayout levelInfo = (LevelInfoLayout) view.findViewById(R.id.levelInfo);
        GridView gridView = (GridView) view.findViewById(R.id.levelIcons);
        //gridView.setAdapter(new LevelIconAdapter(view.getContext())); // uses the view to get the context instead of getActivity().
    /*    gridView.setOnItemClickListener(new OnItemClickListener(){
        public void onItemClick(AdapterView<?> parent, 
            View v, int position, long id){                
        		mCallbacks.setMapChoice(position+1);
                //call database
                MenuActivity activity = ((MenuActivity)parent.getContext());
                Cursor cursor = activity.customDbUI.getLevelInfo(mCallbacks.getMapChoice());
                cursor.moveToFirst();
                int mineLimit = cursor.getInt(cursor.getColumnIndexOrThrow(DefaultLevelDatabaseHelper.MINELIMIT));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DefaultLevelDatabaseHelper.LEVELNAME));
                int bestScore = cursor.getInt(cursor.getColumnIndexOrThrow(DefaultLevelDatabaseHelper.BESTSCORE));
                String bestPlayerName = cursor.getString(cursor.getColumnIndexOrThrow(DefaultLevelDatabaseHelper.BESTPLAYER));
                int difficulty = cursor.getInt(cursor.getColumnIndexOrThrow(DefaultLevelDatabaseHelper.DIFFICULTY));
                int goldScore = cursor.getInt(cursor.getColumnIndexOrThrow(DefaultLevelDatabaseHelper.GOLDSCORE));
                int silverScore = cursor.getInt(cursor.getColumnIndexOrThrow(DefaultLevelDatabaseHelper.SILVERSCORE));
                int bronzeScore = cursor.getInt(cursor.getColumnIndexOrThrow(DefaultLevelDatabaseHelper.BRONZESCORE));
                levelInfo.changeInfo(name, difficulty,mineLimit,bestScore,bestPlayerName, goldScore, silverScore, bronzeScore); 
            }
        });*/
        return view;
	}
}
