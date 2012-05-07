package org.sybila.parasim.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 2-dimensional block of objects. Array of lists where each list may
 * possibly have different size. This structure is immutable, i.e.
 * although contained objects may be modified, they may not be moved,
 * nor may be new objects added.
 *
 * Instances of this class are created via the {@link Builder#create()} method.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 * @param T type of objects created by this block.
 */
public class Block<T> {

    private T[][] data;

    private Block(Coordinate dimensions) {
        int dim = dimensions.getDimension();
        data = (T[][]) new Object[dim][];
        for (int i = 0; i < dim; i++) {
            data[i] = (T[]) new Object[dimensions.getCoordinate(i)];
        }
    }

    /**
     * Return sizes of all lists.
     * @return Object specifying size of each list.
     */
    public Coordinate getDimensions() {
        Coordinate.Builder build = new Coordinate.Builder(getDimension());
        for (int i = 0; i < getDimension(); i++) {
            build.setCoordinate(i, data[i].length);
        }
        return build.create();
    }

    /**
     * Return number of lists contained in this block.
     * @return Number of contained lists.
     */
    public int getDimension() {
        return data.length;
    }

    /**
     * Return size of given list.
     * @param dimension Index of a list.
     * @return Size of list at given index.
     */
    public int getSize(int dimension) {
        return data[dimension].length;
    }

    /**
     * Return object at given list at given index.
     * @param dimension Index of target list.
     * @param index Index of target object in target list.
     * @return Object contained in target list at target index.
     */
    public T get(int dimension, int index) {
        return data[dimension][index];
    }

    /**
     * Builds block by adding objects to given lists.
     * @param <T>
     */
    public static class Builder<T> {
        private List<T>[] data;

        /**
         * Specifies number of lists.
         * @param dimension Number of lists.
         */
        public Builder(int dimension) {
            data = new List[dimension];
            for (int i = 0; i < dimension; i++) {
                data[i] = new ArrayList<T>();
            }
        }

        /**
         * Creates builder from an immutable block. All references
         * are copied, so that this builder is independent on the source block.
         * @param source Source block.
         */
        public Builder(Block<T> source) {
            int dim = source.getDimension();
            data = new List[dim];
            for (int i = 0; i < dim; i++) {
                data[i] = new ArrayList<T>(Arrays.asList(source.data[i]));
            }
        }

        /**
         * Creates builder from an array of lists.
         * This builder is backed by the array, i.e.
         * when the contents of array or lists is changed,
         * contents of this builder is changed and vice versa.
         * @param source Array of lists.
         */
        public Builder(List<T>[] source) {
            data = source;
        }

        /**
         * Return sizes of all lists.
         * @return Object specifying size of each list.
         */
        public Coordinate getDimensions() {
            Coordinate.Builder build = new Coordinate.Builder(getDimension());
            for (int i = 0; i < getDimension(); i++) {
                build.setCoordinate(i, getList(i).size());
            }
            return build.create();
        }

        /**
         * Return number of lists.
         * @return Number of lists.
         */
        public int getDimension() {
            return data.length;
        }

        /**
         * Access given contained list.
         * @param index Index of given list.
         * @return List at given index.
         */
        public List<T> getList(int index) {
            return data[index];
        }

        /**
         * Create block from this builder. Each call returns a new
         * immutable block (references are copied).
         * @return Block with data specified by this builder.
         */
        public Block<T> create() {
            Block<T> result = new Block<T>(getDimensions());
            for (int i = 0; i < getDimension(); i++) {
                getList(i).toArray(result.data[i]);
            }
            return result;
        }
    }

}
