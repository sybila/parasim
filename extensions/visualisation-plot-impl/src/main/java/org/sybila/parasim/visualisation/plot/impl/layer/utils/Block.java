package org.sybila.parasim.visualisation.plot.impl.layer.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class Block<T> {

    private T[][] data;

    private Block(Coordinate dimensions) {
        int dim = dimensions.getDimension();
        data = (T[][]) new Object[dim][];
        for (int i = 0; i < dim; i++) {
            data[i] = (T[]) new Object[dimensions.getCoordinate(dim)];
        }
    }

    public Coordinate getDimensions() {
        Coordinate.Builder build = new Coordinate.Builder(getDimension());
        for (int i = 0; i < getDimension(); i++) {
            build.setCoordinate(i, data[i].length);
        }
        return build.create();
    }

    public int getDimension() {
        return data.length;
    }

    public int getSize(int dimension) {
        return data[dimension].length;
    }

    public T get(int dimension, int index) {
        return data[dimension][index];
    }

    public class Builder<T> {
        private List<T>[] data;

        public Builder(int dimension) {
            data = new List[dimension];
            for (int i = 0; i < dimension; i++) {
                data[i] = new ArrayList<T>();
            }
        }

        public Builder(Block<T> source) {
            int dim = source.getDimension();
            data = new List[dim];
            for (int i = 0; i < dim; i++) {
                data[i] = new ArrayList<T>(Arrays.asList(source.data[i]));
            }
        }

        public Coordinate getDimensions() {
            Coordinate.Builder build = new Coordinate.Builder(getDimension());
            for (int i = 0; i < getDimension(); i++) {
                build.setCoordinate(i, getList(i).size());
            }
            return build.create();
        }

        public int getDimension() {
            return data.length;
        }

        public List<T> getList(int index) {
            return data[index];
        }

        public Block<T> create() {
            Block<T> result = new Block<T>(getDimensions());
            for (int i = 0; i < getDimension(); i++) {
                getList(i).toArray(result.data[i]);
            }
            return result;
        }
    }

}
