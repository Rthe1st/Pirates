package com.mehow.pirates;

import java.io.Serializable;

//Orderd on Y then x, so (1,2) is more then (2,1)

public final class Cords implements Comparable<Cords>, Serializable {

	// creates co-ordinate objects
	public final int x;
	public final int y;

	private final int hashCode;

	// a constructor!
	public Cords(int a, int b) {
		x = a;
		y = b;
		hashCode = (x + "," + y).hashCode();
	}

	public Cords(Cords cord) {
		x = cord.x;
		y = cord.y;
		hashCode = (x + "," + y).hashCode();
	}

	public int compareTo(Cords cords2) {
		// sort based on y
		// if y equal, sort on x
		if (this.y < cords2.y) {
			return -1;
		} else if (this.y > cords2.y) {
			return 1;
		} else {
			if (this.x < cords2.x) {
				return -1;
			} else if (this.x > cords2.x) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	public boolean equals(Object other) {
		if (other == null || other.getClass() != this.getClass()) {
			return false;
		} else {
			Cords otherCord = (Cords) other;
			return (this.x == otherCord.x && this.y == otherCord.y);
		}
	}

	// be this good?
	public int hashCode() {
		return hashCode;
	}

	@Override
	public String toString() {
		return "x: " + x + " y: " + y;
	}
}
