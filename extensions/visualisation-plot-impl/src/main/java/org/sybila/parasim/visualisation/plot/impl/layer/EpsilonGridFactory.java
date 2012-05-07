package org.sybila.parasim.visualisation.plot.impl.layer;

import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.visualisation.plot.impl.ResultPlotterConfiguration;
import org.sybila.parasim.util.Block;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class EpsilonGridFactory implements GridPointLayer.GridFactory {

    private EpsilonLayering layering;

    public EpsilonGridFactory(float epsilon) {
        layering = new EpsilonLayering(epsilon);
    }

    @Override
    public LayeredGrid<Float> getGrid(VerificationResult result, OrthogonalSpace bounds) {
        Block.Builder<Layer> layers = new Block.Builder<Layer>(layering.computeLayers(result, bounds));
        LayeredGrid<Float> target = new LayeredGrid<Float>(layers.create());

        for (int i = 0; i < result.size(); i++) {
            target.put(target.getCoordinate(result.getPoint(i)), result.getRobustness(i).getValue());

        }

        return target;
    }

    public static EpsilonGridFactory getCoordinateFactory(ResultPlotterConfiguration conf) {
        return new EpsilonGridFactory(conf.getMinimumDifference());
    }

}
