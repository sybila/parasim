package org.sybila.parasim.model.ode;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ArrayOdeSystemEncoding implements OdeSystemEncoding {

    private int[] coefficientIndexes;
    private float[] coefficients;
    private int[] factorIndexes;
    private int[] factors;
    
    public ArrayOdeSystemEncoding(int[] coefficientIndexes, float[] coefficients, int[] factorIndexes, int[] factors) {
        if (coefficientIndexes == null) {
            throw new IllegalArgumentException("The parameter coefficientIndexes is null.");
        }
        if (coefficients == null) {
            throw new IllegalArgumentException("The parameter coefficients is null.");
        }
        if (factorIndexes == null) {
            throw new IllegalArgumentException("The parameter factorIndexes is null.");
        }
        if (factors == null) {
            throw new IllegalArgumentException("The parameter factors is null.");
        }
        this.coefficientIndexes = coefficientIndexes;
        this.coefficients = coefficients;
        this.factorIndexes = factorIndexes;
        this.factors = factors;
    }
    
    public float coefficient(int variableIndex, int coefficientIndex) {
        if (coefficientIndex < 0 || coefficientIndex >= countCoefficients(variableIndex)) {
            throw new IllegalArgumentException("The parameter coefficientIndex is out of the range [0," + (countCoefficients(variableIndex) - 1) + "].");
        }
        return coefficients[coefficientIndexes[variableIndex] + coefficientIndex];
    }

    public int countCoefficients() {
        return coefficients.length;
    }
    
    public int countCoefficients(int variableIndex) {
        if (variableIndex < 0 || variableIndex >= countVariables()) {
            throw new IllegalArgumentException("The parameter variableIndex is out of the range [0," + (countVariables() - 1) + "].");
        }
        return coefficientIndexes[variableIndex + 1] - coefficientIndexes[variableIndex];
    }

    public int countFactors() {
        return factors.length;
    }
    
    public int countFactors(int variableIndex, int coefficientIndex) {
        if (coefficientIndex < 0 || coefficientIndex >= countCoefficients(variableIndex)) {
            throw new IllegalArgumentException("The parameter coefficientIndex is out of the range [0," + (countCoefficients(variableIndex) - 1) + "].");
        }
        return factorIndexes[coefficientIndexes[variableIndex] + coefficientIndex + 1] - factorIndexes[coefficientIndexes[variableIndex] + coefficientIndex];
    }

    public int countVariables() {
        return coefficientIndexes.length - 1;
    }

    public int factor(int variableIndex, int coefficientIndex, int factorIndex) {
        if (factorIndex < 0 || factorIndex >= countFactors(variableIndex, coefficientIndex)) {
            throw new IllegalArgumentException("The parameter factorIndex is out of the range [0," + (countFactors(variableIndex, coefficientIndex) - 1) + "].");
        }
        return factors[factorIndexes[coefficientIndexes[variableIndex] + coefficientIndex] + factorIndex];
    }
    
}
