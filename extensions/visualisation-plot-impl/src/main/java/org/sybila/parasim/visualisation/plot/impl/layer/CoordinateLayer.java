package org.sybila.parasim.visualisation.plot.impl.layer;

import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.visualisation.plot.impl.ResultPlotterConfiguration;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class CoordinateLayer extends EpsilonLayer {

    public CoordinateLayer(ResultPlotterConfiguration conf, VerificationResult result, OrthogonalSpace bounds) {
        super(conf.getMinimumDifference(), result, bounds);
    }
}
