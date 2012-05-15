package org.sybila.parasim.visualisation.plot.impl;

import org.sybila.parasim.visualisation.plot.api.Plotter;

/**
 * Plotter containing only one dimension. Prints a message reporting this fact.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class OneDimensionalPlotter implements Plotter {

    @Override
    public void plot() {
        System.out.println("One dimension plotting not supported.");
    }
}
