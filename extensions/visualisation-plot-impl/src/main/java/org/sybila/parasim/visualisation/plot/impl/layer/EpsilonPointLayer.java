package org.sybila.parasim.visualisation.plot.impl.layer;

import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.verification.result.VerificationResult;

/**
 * Groups points into layers according to the same coordinates.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class EpsilonPointLayer extends IndependentPointLayer {

    public EpsilonPointLayer(float epsilon, VerificationResult result, OrthogonalSpace bounds) {
        super(result, bounds, new EpsilonLayering(epsilon));
    }
}
