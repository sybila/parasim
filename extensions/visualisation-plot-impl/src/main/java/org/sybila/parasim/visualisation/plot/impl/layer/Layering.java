package org.sybila.parasim.visualisation.plot.impl.layer;

import java.util.List;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.verification.result.VerificationResult;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface Layering {

    public List<Layer>[] computeLayers(VerificationResult result, OrthogonalSpace bounds);
}
