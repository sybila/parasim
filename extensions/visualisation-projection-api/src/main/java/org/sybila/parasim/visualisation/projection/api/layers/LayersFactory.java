package org.sybila.parasim.visualisation.projection.api.layers;

import org.sybila.parasim.model.verification.result.VerificationResult;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface LayersFactory {

    Layers getLayers(VerificationResult target);
}
