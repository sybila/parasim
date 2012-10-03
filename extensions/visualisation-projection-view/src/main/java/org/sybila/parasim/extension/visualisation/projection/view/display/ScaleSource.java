package org.sybila.parasim.extension.visualisation.projection.view.display;

import org.sybila.parasim.extension.visualisation.projection.view.util.Orientation;
import org.sybila.parasim.visualisation.projection.api.scale.Scale;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface ScaleSource {

    public Scale getScale(Orientation orientation);
}
