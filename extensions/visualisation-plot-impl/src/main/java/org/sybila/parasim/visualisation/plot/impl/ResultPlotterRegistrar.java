package org.sybila.parasim.visualisation.plot.impl;

import org.sybila.parasim.core.Event;
import org.sybila.parasim.core.Instance;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.core.event.ManagerStarted;
import org.sybila.parasim.visualisation.plot.api.event.ResultPlotterRegistered;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ResultPlotterRegistrar {
    
    @Inject
    private Instance<Object> plotter; //sem přijde něco jako ResultPlotter místo Object
    
    @Inject
    private Event<ResultPlotterRegistered> event;
    
    public void register(@Observes ManagerStarted startEvent) {
        plotter.set(new Object()); //TODO
        event.fire(new ResultPlotterRegistered());
    }
    
}
