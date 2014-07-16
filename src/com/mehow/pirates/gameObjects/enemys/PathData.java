package com.mehow.pirates.gameObjects.enemys;

import java.io.Serializable;
import java.util.Vector;

import android.util.Log;

import com.mehow.pirates.Cords;

//this class maintions the rotation and type of bitmap required for each added cords, based on its neighbours
//both rotation and bitmap type are read only externaly
//this classes main function is to keep path types, roation, and cords in sync

// THIS CLASS ASSUMES ALL MOVEMENT BITMAPS DEFAULT TO THESE SIDES:
// STRAIGHT: LEFT TO RIGHT
// CURVE: LEFT TO BOTTOM
// END POINT: LEFT TO MIDDLE
public class PathData implements Serializable{

	public static enum PathTypes implements Serializable{
		STRAIGHT, CURVE, END
	}

	// must all be kept in sync
	private Vector<Cords> moveCords;
	private Vector<PathTypes> pathTypes;
	private Vector<Integer> rotation;

	PathData() {
		moveCords = new Vector<Cords>();
		pathTypes = new Vector<PathTypes>();
		rotation = new Vector<Integer>();
	}

	public boolean contains(Cords cords){
		return moveCords.contains(cords);
	}
	
	public Cords getCords(int position){
		return moveCords.get(position);
	}
	
	public PathTypes getPathType(int position){
		return pathTypes.get(position);
	}
	
	public int getRotation(int position){
		return rotation.get(position);
	}
	
	public int getSize(){
		return moveCords.size();
	}
	
	public Cords getFirst(){
		return moveCords.firstElement();
	}
	
	public Cords getLast(){
		return moveCords.lastElement();
	}
	
	//convience method for appending new cords
	public void addHeadCords(Cords cords){
		addCords(cords, moveCords.size());
	}

	public void addTailCords(Cords cords){
		addCords(cords, 0);
	}
	
	private void addCords(Cords cords, int position) {
		Log.i("PathData","cords: "+cords+" postion:"+position);
		assert(position == 0 || position== moveCords.size());
		moveCords.add(position, cords);
		pathTypes.add(position, null);
		rotation.add(position, null);
		if(moveCords.size()==1){
			pathTypes.set(position,PathTypes.END);
			rotation.set(position, 0);
		}else{
			syncPath(0);
			syncPath(moveCords.size()-1);
			if (position == 0) {
				syncPath(1);
			}else if (position == moveCords.size() - 1) {
				syncPath(moveCords.size() - 2);
			}
		}
		assert (pathTypes.get(position) != null && rotation.get(position) != null);
	}
	
	public void deleteHeadCords(){
		deleteCords(moveCords.size()-1);
	}
	
	public void deleteTailCords(){
		deleteCords(0);
	}
	
	private void deleteCords(int position){
		assert(position == 0 || position==moveCords.size()-1);
		moveCords.remove(position);
		pathTypes.remove(position);
		rotation.remove(position);
		//this should really generalise to sync the 2 elements on either side of the deleted position
		syncPath(0);
		syncPath(moveCords.size()-1);
	}
	
	private void syncPath(int position) {
		Log.i("PathData", "syncing position:"+position);
		if(moveCords.size()==1){
			//if only 1 point left, it doesnt need updating
			//because it has nothing to match with
			return;
		}
		// does it have a before and after
		//if(its an end point
		//and the start is not adjacent to the end (i.e. no loop) unless the size is 2
		//because then the traditional loop doesnt appply because all squares are end points
		else if ((position == 0 || position == moveCords.size() - 1)
				&& (!moveCords.firstElement().isNextTo(
						moveCords.lastElement())||moveCords.size()==2)) {
			pathTypes.set(position, PathTypes.END);
			int rotationAmount;
			if (position == 0) {
				rotationAmount = getCorrectEndPointRotation(
						moveCords.get(position),
						moveCords.get(position + 1));
			} else {
				rotationAmount = getCorrectEndPointRotation(
						moveCords.get(position),
						moveCords.get(position - 1));
			}
			rotation.set(position, rotationAmount);
		} else {
			// % modulus allows us to wrap around the end of the list easily
			// i.e. from moveCords[-1] to moveCords[last]
			
			//required because java is a dickhole: -1%6 gives -1
			//adding the divisor (6) gives the correct wrap around value
			int beforeIndex = (position - 1) % moveCords.size();
			if(beforeIndex < 0){
				beforeIndex += moveCords.size();
			}
			Cords before = moveCords.get(beforeIndex);
			Cords middle = moveCords.get(position);
			Cords after = moveCords.get((position + 1) % moveCords.size());
			if (before.x == after.x) {//x's equal, y's must be different
				pathTypes.set(position, PathTypes.STRAIGHT);
				rotation.set(position, 90);
			} else if (before.y == after.y) {//y's equal, x's must be different
				pathTypes.set(position, PathTypes.STRAIGHT);
				rotation.set(position, 0);
			} else {
				pathTypes.set(position, PathTypes.CURVE);
				rotation.set(position, calculateCurveRotation(before, middle, after));
			}
		}
	}

	// return clockwise rotation needed
	private int getCorrectEndPointRotation(Cords changedCords,
			Cords adjacentCords) {
		// this assert allows use to use if else
		// because one and only one of x or y must differ for the 2 cords
		assert (changedCords.isNextTo(adjacentCords));
		if (changedCords.x > adjacentCords.x) {
			// no rotation required
			return 0;
		} else if (changedCords.x < adjacentCords.x) {
			return 180;
		} else if (changedCords.y < adjacentCords.y) {
			return 270;
		} else {
			return 90;
		}
	}

	// return clockwise rotation needed
	private int calculateCurveRotation(Cords before, Cords middle,
			Cords after) {
		assert (!before.isNextTo(after));
		// |_|2|_| ^top of screen^
		// |1|M|3| ^y-axis goes towards 0^
		// |_|4|_|
		// M = middle
		// works out which number before and after represent relative to
		// middle
		// then gives rotation based on this
		// 4 and 1 gives 0
		// 1 and 2 gives 90
		// 2 and 3 gives 180
		// 3 and 4 gives 270
		// this is good because its reversible,
		// the same result is given if before is 1 and after is 2 or if
		// before is 2 and after is 1
		int beforeCode = calculateCode(middle, before);
		int afterCode = calculateCode(middle, after);
		if (beforeCode == 1 || afterCode == 1) {
			if (beforeCode == 2 || afterCode == 2) {
				// 1 and 2
				return 90;
			} else if(beforeCode == 4 || afterCode == 4){
				// 1 and 4
				return 0;
			}else{
				throw new RuntimeException("Invalid code combination: before: "
						+ beforeCode + " after Code: " + afterCode
						+ " before cords: " + before + " middlecords: "
						+ middle + " after cords: " + after);
			}
		} else if (beforeCode == 3 || afterCode == 3) {
			if (beforeCode == 2 || afterCode == 2) {
				// 3 and 2
				return 180;
			} else if(beforeCode == 4 || afterCode == 4) {
				// 3 and 4
				return 270;
			}else{
				throw new RuntimeException("Invalid code combination: before: "
						+ beforeCode + " after Code: " + afterCode
						+ " before cords: " + before + " middlecords: "
						+ middle + " after cords: " + after);
			}
		} else {
			throw new RuntimeException("Invalid code combination: before: "
					+ beforeCode + " after Code: " + afterCode
					+ " before cords: " + before + " middlecords: "
					+ middle + " after cords: " + after);
		}
	}

	private int calculateCode(Cords middle, Cords other) {
		if (other.x < middle.x) {
			return 1;
		} else if (other.x > middle.x) {
			return 3;
		} else if (other.y > middle.y) {
			return 4;
		} else {
			return 2;
		}
	}
}
