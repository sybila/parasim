package org.sybila.parasim.visualisation.plot.api;

/**
 * Object capable of repeatedly plotting something, generally a result. Plotted
 * object should not be mutable.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface Plotter {

    /**
     * Plots included object. Expected to be called repeatedly,
     * but not simultaneously.
     */
    public void plot();
}
