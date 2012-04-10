/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
