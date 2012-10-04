package org.sybila.parasim.visualisation.projection.api;

import javax.swing.JComponent;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface ProjectionPlotter {

    public JComponent getPointPlotter();

    public JComponent getTrajectoryPlotter();

    public ResultModel getResultModel();

    public void setResultModel(ResultModel target);
}
