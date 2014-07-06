package com.mehow.pirates;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

import com.mehow.pirates.gameObjects.GameObject;
import com.mehow.pirates.gameObjects.GameObjectMap;

public class Steps implements Serializable{

	private Stack<Vector<GameObjectMove<?>>> steps;
	private boolean inStep;// flag to show when a (multipart) step has begun but
							// not yet ended
							// they begin with beginStep() and end with
							// endStep();
	private Vector<GameObjectMove<?>> subSteps;

	private int lastAnimated;
	
	public Steps() {
		steps = new Stack<Vector<GameObjectMove<?>>>();
		lastAnimated = 0;
	}
	
	public <T extends GameObject> void makeStep(GameObjectMap<T> goMap, T go,
			Cords startCords, Cords endCords) {
		subSteps = new Vector<GameObjectMove<?>>();
		subSteps.add(new GameObjectMove<T>(goMap, go, startCords, endCords));
		steps.add(subSteps);
		subSteps = null;
	}

	public boolean atTurnStart() {
		return steps.size() == 0;
	}

	public Vector<GameObjectMove<?>> undoStep() {
		if (inStep == true) {
			throw new RuntimeException(
					"cannot undp ste because a step has begun");
		}
		return steps.pop();
	}

	public Vector<GameObjectMove<?>> toTurn() {
		if (inStep == true) {
			throw new RuntimeException(
					"cannot convert to turn because a step has begun");
		}
		HashMap<GameObject, GameObjectMove<?>> distinctMoves = new HashMap<GameObject, GameObjectMove<?>>();
		while (steps.size() != 0) {
			Vector<GameObjectMove<?>> goMoves = steps.pop();
			for (GameObjectMove<?> goMove : goMoves) {
				if (distinctMoves.containsKey(goMove.go)) {
					distinctMoves.get(goMove.go).setStartCords(
							goMove.getStartCords());
				} else {
					distinctMoves.put(goMove.go, goMove);
				}
			}
		}
		/*
		 * warning these logs cause an error thrwow for(GameObjectMove goMove :
		 * distinctMoves.values()){ Log.i("Steps", "distinct gomoves");
		 * Log.i("Steps"
		 * ,"go map Type: "+goMove.gameObjectMap.getClass().toString());
		 * Log.i("Steps",
		 * "gostart: "+goMove.getStartCords().toString()+" goEnd: "
		 * +goMove.endCords); }
		 */
		return new Vector<GameObjectMove<?>>(distinctMoves.values());
	}

	public void beginStep() {
		if (inStep == true) {
			throw new RuntimeException(
					"cannot begin step because a step has already begun");
		}
		inStep = true;
		subSteps = new Vector<GameObjectMove<?>>();
	}

	public <T extends GameObject> void makeSubStep(GameObjectMap<T> goMap,
			T go, Cords startCords, Cords endCords) {
		if (inStep == false) {
			throw new RuntimeException(
					"cannot make sub-step because a step has not begun");
		}
		//add at start so that when undos are run, the last thing to happen is undoen first
		subSteps.add(0, new GameObjectMove<T>(goMap, go, startCords, endCords));
	}

	public void endStep() {
		if (inStep == false) {
			throw new RuntimeException(
					"cannot end step because a step has not yet begun");
		}
		inStep = false;
		steps.add(subSteps);
		subSteps = null;
	}

}