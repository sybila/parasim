package org.sybila.parasim.visualisation.projection.api;

import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.util.Coordinate;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class AbstractLayersTest {

    @Test
    public void testGetDimension() {
        final int[] dimensions = {1, 5, 20, 256};
        for (final int dim : dimensions) {
            Layers test = new AbstractLayers() {

                @Override
                protected int getDimensionImpl() {
                    return dim;
                }

                @Override
                protected int getSizeImpl(int dimension) {
                    return 0;
                }

                @Override
                protected Layer getImpl(int dimension, int index) {
                    return null;
                }
            };
            Assert.assertEquals(test.getDimension(), dim, "Method getDimension should not change value returned by getDimensionImpl.");
        }
    }

    @Test
    public void testGetSize() {
        final int[] dimensions = {1, 5, 10, 256};
        final int[] indices = {-20, -5, 0, 2, 4, 8, 16, 100, 1000};
        final int[] sizes = {5, 15, 20, 25};
        for (final int dim : dimensions) {
            for (final int index : indices) {
                for (final int size : sizes) {
                    Layers test = new AbstractLayers() {

                        @Override
                        protected int getDimensionImpl() {
                            return dim;
                        }

                        @Override
                        protected int getSizeImpl(int dimension) {
                            if (dimension == index) {
                                return size;
                            } else {
                                return 0;
                            }
                        }

                        @Override
                        protected Layer getImpl(int dimension, int index) {
                            return null;
                        }
                    };
                    if (index < 0) {
                        try {
                            test.getSize(index);
                            Assert.fail("Should throw an IllegalArgumentException when testing negative index.");
                        } catch (IllegalArgumentException iae) {
                            // OK
                        }
                    } else if (index >= dim) {
                        try {
                            test.getSize(index);
                            Assert.fail("Should throw an IllegalArgumentException when testing index greater than dimension.");
                        } catch (IllegalArgumentException iae) {
                            // OK
                        }
                    } else {
                        Assert.assertEquals(test.getSize(index), size, "Method getSize should not change the value returned by getSizeImpl.");
                    }
                }
            }
        }
    }

    private static class UniqueLayer implements Layer {

        @Override
        public boolean isIn(float x) {
            return false;
        }

        @Override
        public float getValue() {
            return 0;
        }
    }

    @Test
    public void testGet() {
        final int[] dimensions = {1, 5, 25, 256};
        final int[] dimIndices = {-5, 0, 3, 12, 50, 75, 1000};
        final int[] sizes = {1, 7, 10, 32, 129};
        final int[] indices = {-10, -1, 0, 5, 12, 24, 85, 500};
        final Layer[] layers = {new UniqueLayer(), new UniqueLayer(), new UniqueLayer()};

        for (final int dim : dimensions) {
            for (final int dimIndex : dimIndices) {
                for (final int size : sizes) {
                    for (final int ind : indices) {
                        for (final Layer layer : layers) {
                            Layers test = new AbstractLayers() {

                                @Override
                                protected int getDimensionImpl() {
                                    return dim;
                                }

                                @Override
                                protected int getSizeImpl(int dimension) {
                                    if (dimension == dimIndex) {
                                        return size;
                                    } else {
                                        return 0;
                                    }
                                }

                                @Override
                                protected Layer getImpl(int dimension, int index) {
                                    if (dimension == dimIndex && index == ind) {
                                        return layer;
                                    } else {
                                        return null;
                                    }
                                }
                            };
                            if (dimIndex < 0) {
                                try {
                                    test.get(dimIndex, ind);
                                    Assert.fail("Should throw IllegalArgumentException when dimension index is lower than zero.");
                                } catch (IllegalArgumentException iae) {
                                    // OK
                                }
                            } else if (dimIndex >= dim) {
                                try {
                                    test.get(dimIndex, ind);
                                    Assert.fail("Should throw IllegalArgumentException when dimension index is greater than or equal to dimension numbe.r");
                                } catch (IllegalArgumentException iae) {
                                    // OK
                                }
                            } else if (ind < 0) {
                                try {
                                    test.get(dimIndex, ind);
                                    Assert.fail("Should throw IllegalArgumentException when layer index is lower than zero.");
                                } catch (IllegalArgumentException iae) {
                                    // OK
                                }
                            } else if (ind >= size) {
                                try {
                                    test.get(dimIndex, ind);
                                    Assert.fail("Should throw IllegalArgumentException when layer index is greater than dimension size.");
                                } catch (IllegalArgumentException iae) {
                                    // OK
                                }
                            } else {
                                Assert.assertEquals(test.get(dimIndex, ind), layer, "Method get should return the same layer as getImpl.");
                            }
                        }
                    }
                }
            }
        }
    }
    private static final int[] SIZES = {5, 1, 2, 4, 10, 12, 1, 3, 1, 8};
    private static final float[] VALUES = {0.1f, 0.18f, 2.1f, 3.4f, 5.7f, 8.2f, 9.4f, 10.1f, 12.8f, 13.3f};

    @Test
    public void testIsFlat() {
        Layers test = new AbstractLayers() {

            @Override
            protected int getDimensionImpl() {
                return SIZES.length;
            }

            @Override
            protected int getSizeImpl(int dimension) {
                return SIZES[dimension];
            }

            @Override
            protected Layer getImpl(int dimension, int index) {
                return null;
            }
        };
        for (int i = 0; i < SIZES.length; i++) {
            if (SIZES[i] == 1) {
                Assert.assertTrue(test.isFlat(i), "Dimension " + i + " should be flat (size " + SIZES[i] + ").");
            } else {
                Assert.assertFalse(test.isFlat(i), "Dimension " + i + " should not be flat (size " + SIZES[i] + ").");
            }
        }
    }

    private static class ConstantLayer implements Layer {

        private float val;

        public ConstantLayer(float value) {
            val = value;
        }

        @Override
        public float getValue() {
            return val;
        }

        @Override
        public boolean isIn(float x) {
            return false;
        }
    }

    @Test
    public void testGetFlatValue() {
        Layers test = new AbstractLayers() {

            @Override
            protected int getDimensionImpl() {
                return SIZES.length;
            }

            @Override
            protected int getSizeImpl(int dimension) {
                return SIZES[dimension];
            }

            @Override
            protected Layer getImpl(int dimension, int index) {
                if (SIZES[dimension] == 1 && index == 0) {
                    return new ConstantLayer(VALUES[dimension]);
                } else {
                    return null;
                }
            }
        };
        for (int i = 0; i < SIZES.length; i++) {
            if (SIZES[i] == 1) {
                Assert.assertEquals(test.getFlatValue(i), VALUES[i], "The value returned by getFlatValue should correspond to value of Layer.");
            } else {
                try {
                    test.getFlatValue(i);
                    Assert.fail("The dimension " + i + " is not flat -- should throw IllegalArgumentException.");
                } catch (IllegalArgumentException iae) {
                    // OK
                }
            }
        }
    }

    @Test
    public void testGetNonFlatDimensionNumber() {
        final int[][] sizes = {{1, 5, 7, 1, 2}, {4, 3, 2, 5}, {2, 1, 5, 2}, {1, 1, 1, 1}, {2, 1, 3, 2}};
        final int[] nonFlatNum = {3, 4, 3, 0, 3};
        for (int i = 0; i < sizes.length; i++) {
            final int index = i;
            Layers test = new AbstractLayers() {

                @Override
                protected int getDimensionImpl() {
                    return sizes[index].length;
                }

                @Override
                protected int getSizeImpl(int dimension) {
                    return sizes[index][dimension];
                }

                @Override
                protected Layer getImpl(int dimension, int index) {
                    return null;
                }
            };
            Assert.assertEquals(test.getNonFlatDimensionNumber(), nonFlatNum[i], "Wrong number of non-flat dimensions.");
        }
    }

    @Test
    public void testGetPoint() {
        final float[][] layers = {{-3.2f, -1.8f, 0.2f, 1.2f}, {0.8f, 3.5f, 4.9f},
            {-5.1f, -3.9f, -0.3f, 2.8f, 3.9f}, {4.1f, 8.2f, 9.7f, 10.8f, 13.1f},
            {2.1f, 5.3f}};
        AbstractLayers test = new AbstractLayers() {

            @Override
            protected int getDimensionImpl() {
                return layers.length;
            }

            @Override
            protected int getSizeImpl(int dimension) {
                return layers[dimension].length;
            }

            @Override
            protected Layer getImpl(int dimension, int index) {
                return new ConstantLayer(layers[dimension][index]);
            }
        };

        Coordinate.Builder builder = new Coordinate.Builder(test.getDimensionImpl());
        int tries = 1;
        for (int i = 0; i < test.getDimensionImpl(); i++) {
            tries *= test.getSizeImpl(i);
        }

        for (int i = 0; i < tries; i++) {
            int index = i;
            // fill the coordinate //
            for (int j = 0; j < test.getDimensionImpl(); j++) {
                int size = test.getSizeImpl(j);
                builder.setCoordinate(j, index % size);
                index = index / size;
            }
            Coordinate coord = builder.create();
            Point result = test.getPoint(coord);
            // test the point //
            for (int j = 0; j < test.getDimensionImpl(); j++) {
                Assert.assertEquals(result.getValue(j), test.getImpl(j, coord.getCoordinate(j)).getValue(),
                        "Point should have the value of correct layer.");
            }
        }

    }

    private static class ThreePointLayer implements Layer {

        private float min, val, max;

        public ThreePointLayer(float min, float val, float max) {
            if ((min > val) || (val > max)) {
                throw new IllegalArgumentException("Values are not ordered.");
            }
            this.min = min;
            this.val = val;
            this.max = max;
        }

        @Override
        public float getValue() {
            return val;
        }

        @Override
        public boolean isIn(float x) {
            if (x < min) {
                return false;
            }
            if (x > max) {
                return false;
            }
            return true;
        }
    }

    @Test
    public void testGetCoordinate() {
        final Layer universal = new Layer() {

            @Override
            public boolean isIn(float x) {
                if (Float.isNaN(x)) {
                    return false;
                }
                if (Float.isInfinite(x)) {
                    return false;
                }
                return true;
            }

            @Override
            public float getValue() {
                return 0;
            }
        };
        final Layer[] layers = {
            new ThreePointLayer(0f, 0.5f, 1.1f),
            new ThreePointLayer(1.2f, 1.5f, 2.3f),
            new ThreePointLayer(2.5f, 3f, 3.5f)
        };
        final Point[] points = {
            new ArrayPoint(0, 0, 0),
            new ArrayPoint(0, 0.7f, 250),
            new ArrayPoint(0, 2.6f, 84600),
            new ArrayPoint(0, 2.1f, -180500)
        };
        final int[] results = {0, 0, 2, 1};
        final Point[] outPoints = {
            new ArrayPoint(0, -1, 0),
            new ArrayPoint(0, 1.15f, 0),
            new ArrayPoint(0, 2.4f, 0),
            new ArrayPoint(0, 3.7f, 0)
        };

        Layers test = new AbstractLayers() {

            @Override
            protected int getDimensionImpl() {
                return 2;
            }

            @Override
            protected int getSizeImpl(int dimension) {
                if (dimension == 0) {
                    return layers.length;
                } else if (dimension == 1) {
                    return 1;
                } else {
                    throw new IndexOutOfBoundsException("There is no such dimension: " + dimension);
                }
            }

            @Override
            protected Layer getImpl(int dimension, int index) {
                if (dimension == 0) {
                    return layers[index];
                } else if (dimension == 1) {
                    return universal;
                } else {
                    throw new IndexOutOfBoundsException("There is no such dimension: " + dimension);
                }
            }
        };


        // test including //
        for (int i = 0; i < points.length; i++) {
            Coordinate result = test.getCoordinate(points[i]);
            Assert.assertEquals(result.getCoordinate(0), results[i], "Failure on non-flat dimension.");
            Assert.assertEquals(result.getCoordinate(1), 0, "Failure on flat dimension.");
        }
        for (Point p : outPoints) {
            Assert.assertNull(test.getCoordinate(p), "The point should be outside all layers.");
        }
    }
}
