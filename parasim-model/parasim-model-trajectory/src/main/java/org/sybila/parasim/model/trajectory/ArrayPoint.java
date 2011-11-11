package org.sybila.parasim.model.trajectory;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ArrayPoint extends AbstractPoint {

	private float[] data;
	private int startIndex;

	public ArrayPoint(float[] data, float time) {
		this(data, time, 0, data.length);
	}

	public ArrayPoint(float[] data, float time, int startIndex, int dimension) {
        super(dimension, time);
		if (data == null) {
			throw new IllegalArgumentException("The parameter [data] is NULL.");
		}
		if (startIndex < 0 || startIndex >= data.length) {
			throw new IllegalArgumentException("The start index is out of the range [0, " + (data.length - 1) + "].");
		}
		if (data.length - startIndex < dimension) {
			throw new IllegalArgumentException("The length of piece of the array doesn't correspond to the dimension.");
		}
		this.data = data;
		this.startIndex = startIndex;
	}
    @Override
	public float getValue(int index) {
		if (index < 0 || index >= getDimension()) {
			throw new IllegalArgumentException("The index is out of the range [0, " + (getDimension() - 1) + "].");
		}
		return data[startIndex + index];
	}
}
