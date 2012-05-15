package org.sybila.parasim.visualisation.plot.impl.render;

import org.sybila.parasim.visualisation.plot.impl.ResultPlotterConfiguration;

/**
 * Renders points as circles of two colors according to the extension configuration.
 * Points with robustness equal to zero are omitted.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ValidityPointRenderer extends ZeroRemover {

    /**
     * Initialize this renderer according to configuration:
     * <ul>
     *  <li>{@link ResultPlotterConfiguration#getPointRadius()} -- size (radius) of points.</li>
     *  <li>{@link ResultPlotterConfiguration#getPointColorValid()} -- color of points with positive robustness.</li>
     *  <li>{@link ResultPlotterConfiguration#getPointColorInvalid()} -- color of points with negative robustness.</li>
     * </ul>
     * @param conf This extension configuration.
     */
    public ValidityPointRenderer(ResultPlotterConfiguration conf) {
        super(new ColorPointRenderer(new CirclePointRenderer(conf.getPointRadius()),
                conf.getPointColorValid(), conf.getPointColorInvalid()), conf);
    }
}
