package org.sybila.parasim.visualisation.plot.impl.render;

import org.sybila.parasim.visualisation.plot.impl.ResultPlotterConfiguration;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ValidityPointRenderer extends ZeroRemover {

    public ValidityPointRenderer(ResultPlotterConfiguration conf) {
        super(new ColorPointRenderer(new CirclePointRenderer(conf.getPointRadius()),
                conf.getPointColorValid(), conf.getPointColorInvalid()), conf);
    }
}
