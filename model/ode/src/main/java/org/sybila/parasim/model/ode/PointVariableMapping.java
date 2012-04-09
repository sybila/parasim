package org.sybila.parasim.model.ode;

/**
 * Maps variable names to dimensions of {@link Point}.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 *
 */
public interface PointVariableMapping extends VariableMapping<Integer> {

    /**
     * @return Dimension of given {@link Point}.
     */
    public int getDimension();
}
