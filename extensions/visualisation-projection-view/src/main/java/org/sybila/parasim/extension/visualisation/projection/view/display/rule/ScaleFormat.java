package org.sybila.parasim.extension.visualisation.projection.view.display.rule;

import org.sybila.parasim.visualisation.projection.api.scale.Scale;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface ScaleFormat {

    public Iterable<TickModel> formatHorizontalScale(Scale scale, int size, StringMeasure measure);

    public Iterable<TickModel> formatVerticalScale(Scale scale, int size, StringMeasure measure, int maxWidth);
}
