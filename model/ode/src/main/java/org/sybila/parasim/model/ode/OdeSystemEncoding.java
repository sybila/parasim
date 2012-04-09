package org.sybila.parasim.model.ode;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface OdeSystemEncoding {

    float coefficient(int variableIndex, int coefficientIndex);

    int countCoefficients();

    int countCoefficients(int variableIndex);

    int countFactors();

    int countFactors(int variableIndex, int coefficientIndex);

    int countVariables();

    int factor(int variableIndex, int coefficientIndex, int factorIndex);
}
