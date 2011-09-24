package org.sybila.parasim.model.trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ArrayPoint implements Point {

	private float[] data;
	private int dimension;
	private int startIndex;
	private float time;
	private float[] dataInArray;

	public ArrayPoint(float[] data, float time) {
		this(data, time, 0, data.length);
	}

	public ArrayPoint(float[] data, float time, int startIndex, int dimension) {
		if (data == null) {
			throw new IllegalArgumentException("The parameter [data] is NULL.");
		}
		if (dimension <= 0) {
			throw new IllegalArgumentException("The dimension has to be a positive number.");
		}
		if (startIndex < 0 || startIndex >= data.length) {
			throw new IllegalArgumentException("The start index is out of the range [0, " + (data.length - 1) + "].");
		}
		if (data.length - startIndex < dimension) {
			throw new IllegalArgumentException("The length of piece of the array doesn't correspond to the dimension.");
		}
		this.data = data;
		this.dimension = dimension;
		this.startIndex = startIndex;
		this.time = time;
	}

    @Override
	public int getDimension() {
		return dimension;
	}

    @Override
	public float getValue(int index) {
		if (index < 0 || index >= dimension) {
			throw new IllegalArgumentException("The index is out of the range [0, " + (dimension - 1) + "].");
		}
		return data[startIndex + index];
	}

    @Override
	public float getTime() {
		return time;
	}

    @Override
	public float[] toArray() {
		if (dataInArray == null) {
			dataInArray = new float[dimension];
			System.arraycopy(data, startIndex, dataInArray, 0, dimension);
		}
		return dataInArray;
	}
}
