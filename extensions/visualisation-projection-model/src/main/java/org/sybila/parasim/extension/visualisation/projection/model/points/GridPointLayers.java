package org.sybila.parasim.extension.visualisation.projection.model.points;

import java.util.HashMap;
import java.util.Map;
import org.sybila.parasim.extension.visualisation.projection.model.layers.EpsilonLayersFactory;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.util.Coordinate;
import org.sybila.parasim.util.Grid;
import org.sybila.parasim.util.Pair;
import org.sybila.parasim.visualisation.projection.api.layers.Layers;
import org.sybila.parasim.visualisation.projection.api.points.LayerSpecification;
import org.sybila.parasim.visualisation.projection.api.points.Point2D;
import org.sybila.parasim.visualisation.projection.api.points.PointLayers;
import org.sybila.parasim.visualisation.projection.api.points.PointLayersFactory;
import org.sybila.parasim.visualisation.projection.api.points.SingleLayer;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class GridPointLayers extends LayersAdapter implements PointLayers {

    private static final float PRECISION = 0.000001f;
    //
    private Grid<Pair<Point, Robustness>> points;

    public GridPointLayers(Layers layers, VerificationResult result) {
        super(layers);
        for (int i = 0; i < result.size(); i++) {
            Point p = result.getPoint(i);
            points.put(getCoordinate(p), new Pair<>(p, result.getRobustness(i)));
        }
    }

    private Point2D getPoint(Pair<Point, Robustness> pair, LayerSpecification specs) {
        float y;
        if (specs.isDegenerate()) {
            y = 0;
        } else {
            y = pair.first().getValue(specs.getYDimension());
        }
        return new Point2D(pair.first().getValue(specs.getXDimension()), y);
    }

    @Override
    public SingleLayer getSingleLayer(LayerSpecification specification) {
        if (specification.getDimension() != getDimension()) {
            throw new IllegalArgumentException("Dimension mismatch: expected " + getDimension() + "; got " + specification.getDimension());
        }
        Coordinate.Builder target = new Coordinate.Builder(specification.getDimension());
        for (int i = 0; i < getDimension(); i++) {
            target.setCoordinate(i, specification.getLayer(i));
        }

        Map<Point2D, Pair<Point, Robustness>> map = new HashMap<>();
        for (int x = 0; x < getSize(specification.getXDimension()); x++) {
            target.setCoordinate(specification.getXDimension(), x);

            if (specification.isDegenerate()) {
                Pair<Point, Robustness> pair = points.get(target.create());
                map.put(getPoint(pair, specification), pair);
            } else {
                for (int y = 0; y < getSize(specification.getYDimension()); y++) {
                    target.setCoordinate(specification.getYDimension(), y);
                    Pair<Point, Robustness> pair = points.get(target.create());
                    map.put(getPoint(pair, specification), pair);
                }
            }
        }
        float minX = get(specification.getXDimension(), 0).getValue() - PRECISION;
        float maxX = get(specification.getXDimension(), getSize(specification.getXDimension())).getValue() + PRECISION;
        float minY, maxY;
        if (specification.isDegenerate()) {
            minY = -PRECISION;
            maxY = PRECISION;
        } else {
            minY = get(specification.getYDimension(), 0).getValue() - PRECISION;
            maxY = get(specification.getYDimension(), getSize(specification.getYDimension())).getValue() + PRECISION;
        }
        return new MapSingleLayer(map, new Point2D(minX, minY), new Point2D(maxX, maxY));
    }

    private static enum Factory implements PointLayersFactory {

        INSTANCE;

        @Override
        public PointLayers createPointLayers(VerificationResult result) {
            return new GridPointLayers(EpsilonLayersFactory.get(PRECISION).getLayers(result), result);
        }
    }

    public static PointLayersFactory getFactory() {
        return Factory.INSTANCE;
    }
}
