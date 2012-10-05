package org.sybila.parasim.extension.visualisation.projection.model.points;

import org.sybila.parasim.util.Coordinate;
import org.sybila.parasim.visualisation.projection.api.layers.Layers;
import org.sybila.parasim.visualisation.projection.api.points.LayerSpecification;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class CoordinateLayerSpecification implements LayerSpecification {

    private Coordinate coordinate;
    private int x, y;

    private CoordinateLayerSpecification(Coordinate coord, int xDim, int yDim) {
        coordinate = coord;
        x = xDim;
        y = yDim;
    }

    @Override
    public int getDimension() {
        return coordinate.getDimension();
    }

    @Override
    public int getLayer(int index) {
        return coordinate.getCoordinate(index);
    }

    @Override
    public int getXDimension() {
        return x;
    }

    @Override
    public int getYDimension() {
        return y;
    }

    @Override
    public boolean isDegenerate() {
        return x == y;
    }

    public static class Builder {

        private Layers layers;
        private Coordinate.Builder coordBuild;
        private int x, y;

        private Builder(Layers layers) {
            this.layers = layers;
            coordBuild = new Coordinate.Builder(layers.getDimension());

            //initialize x and y
            boolean xInitialized = false;
            for (int i = 0; i < layers.getDimension(); i++) {
                if (!layers.isFlat(i)) {
                    if (xInitialized) {
                        y = i;
                        break;
                    } else {
                        x = i;
                        y = i;
                    }
                }
            }
        }

        private void checkDimension(int dim) {
            if (dim < 0) {
                throw new IllegalArgumentException("Dimension cannot be negative.");
            } else if (dim >= layers.getDimension()) {
                throw new IllegalArgumentException("Dimension too large.");
            }
        }

        private void checkDimensionOverflow(int dim, int index) {
            checkDimension(dim);
            if (index < 0) {
                throw new IllegalArgumentException("Target value cannot be negative.");
            } else if (index >= layers.getSize(dim)) {
                throw new IllegalArgumentException("Target value exceeds number of layers in given dimension.");
            }
        }

        private void checkDimensionNonFlat(int dim) {
            checkDimension(dim);
            if (layers.isFlat(dim)) {
                throw new IllegalArgumentException("Cannot select a flat dimension.");
            }
        }

        public void setCoordinate(int dim, int target) {
            checkDimensionOverflow(dim, target);
            coordBuild.setCoordinate(dim, target);
        }

        public void setX(int target) {
            checkDimensionNonFlat(target);
            if (x == y) {
                y = target;
            } else if (y == target) {
                throw new IllegalArgumentException("Target dimension already chosen as Y.");
            }
            x = target;
        }

        public void setY(int target) {
            if (x == y) {
                throw new IllegalStateException("The source layers are degenerate -- there is only one nonflat dimension. Cannot set the other.");
            }
            checkDimensionNonFlat(target);
            if (x == target) {
                throw new IllegalArgumentException("Target dimension already chosen as X.");
            }
            y = target;
        }

        public Builder getBuilder(Layers layers) {
            if (layers.getNonFlatDimensionNumber() == 0) {
                throw new IllegalArgumentException("All dimensions of layers are flat.");
            }
            return new Builder(layers);
        }

        public LayerSpecification createSpecification() {
            return new CoordinateLayerSpecification(coordBuild.create(), x, y);
        }
    }
}
