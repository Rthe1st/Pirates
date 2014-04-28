package com.mehow.pirates.gameObjects;

import android.os.Bundle;

import com.mehow.pirates.Cords;

import java.util.ArrayList;

public class PastMoveCordsList {
	
	private ArrayList<PastMoveCords> previousMoves;
	
		class PastMoveCords{
			private ArrayList<Cords> cords;
			public PastMoveCords(){
				cords = new ArrayList<Cords>(0);
			}
			public PastMoveCords(ArrayList<Cords> curCords){
				cords = curCords;
			}
			public boolean contains(Cords cord){
				for(int i=0;i<cords.size();i++){
					if(cord.equals(cords.get(i))){
						return true;
					}
				}
				return false;
			}
			public void addCord(Cords cord){
				cords.add(cord);
			}
			public int size(){
				return cords.size();
			}
			public Cords getCord(int pos){
				return cords.get(pos);
			}
			public ArrayList<Cords> getAllCords(){
				return cords;
			}
		}
		
	public PastMoveCordsList(){
		previousMoves = new ArrayList<PastMoveCords>(0);
		previousMoves.add(new PastMoveCords());
	}
	public void addNewTurn(ArrayList<Cords> curCords){
		previousMoves.add(new PastMoveCords(curCords));		
	}
	public ArrayList<Cords> getPastTurn(int i){
		return previousMoves.get(i).getAllCords();
	}
	public void removeLastTurn()throws ArrayIndexOutOfBoundsException{
			previousMoves.remove(previousMoves.size()-1);
	}
	public ArrayList<Cords> getLastTurnCordNum() throws ArrayIndexOutOfBoundsException{
			return previousMoves.get(previousMoves.size()-1).getAllCords();
	}
	public int size(){
		return previousMoves.size();
	}
	public void addCord(Cords cord){
		previousMoves.get(previousMoves.size()-1).addCord(cord);
	}
	public ArrayList<Cords> undoTurn() throws ArrayIndexOutOfBoundsException{
		ArrayList<Cords> cords =  previousMoves.get(previousMoves.size()-1).getAllCords();
		previousMoves.remove(previousMoves.size()-1);
		return cords;
	}
	public boolean contains(Cords cord){
		for(int i=0;i<previousMoves.size();i++){
			if(previousMoves.get(i).contains(cord)){
				return true;
			}
		}
		return false;
	}
	public Cords getLastCord(){
		PastMoveCords lastTurn = previousMoves.get(previousMoves.size()-1);
		return lastTurn.getCord(lastTurn.size()-1);
	}
	public Bundle saveState(){
		Bundle bundle = new Bundle();
		Cords curCords;
		int cordsPerTurn[] = new int[previousMoves.size()];
		for(int i=0; i<previousMoves.size();i++){
			cordsPerTurn[i] = previousMoves.get(i).size();
			for(int g=0;g<previousMoves.get(i).size();g++){
				curCords = previousMoves.get(i).getCord(g);
				bundle.putInt("mine_turn_"+i+"_x", curCords.x);
				bundle.putInt("mine_turn_"+i+"_y", curCords.y);
			}
		}
		bundle.putIntArray("minesPerTurn", cordsPerTurn);
		return bundle;
	}
	public void loadState(Bundle bundle){
		Cords curCords;
		int cordsPerTurn[] = bundle.getIntArray("minesPerTurn");
		for(int i=0; i<cordsPerTurn.length;i++){
			previousMoves.add(new PastMoveCords());
			for(int g=0;g<cordsPerTurn[i];g++){
				curCords = new Cords(bundle.getInt("mine_turn_"+i+"_x"), bundle.getInt("mine_turn_"+i+"_y"));
				previousMoves.get(i).addCord(curCords);
			}
		}
	}
}
