package org.sybila.parasim.visualisation.projection.api.points;

import org.sybila.parasim.model.verification.result.VerificationResult;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface PointLayersFactory {

    public PointLayers createPointLayers(VerificationResult result);
}
