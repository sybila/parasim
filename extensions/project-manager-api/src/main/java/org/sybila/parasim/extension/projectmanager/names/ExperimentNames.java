/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.extension.projectmanager.names;

import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.sybila.parasim.extension.projectmanager.project.ResourceException;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ExperimentNames {

    private String formulaName, initialSpaceName, simulationSpaceName, precisionConfigurationName, verificationResultName, modelName, annotation;
    private int iterationLimit = 0;
    private long timeoutAmount = 0;
    private TimeUnit timeUnit = TimeUnit.MINUTES;

    public ExperimentNames() {
        formulaName = null;
        initialSpaceName = null;
        simulationSpaceName = null;
        precisionConfigurationName = null;
        verificationResultName = null;
        modelName = null;
        annotation = null;
    }

    public ExperimentNames(ExperimentNames source) {
        formulaName = source.getFormulaName();
        initialSpaceName = source.getInitialSpaceName();
        simulationSpaceName = source.getSimulationSpaceName();
        precisionConfigurationName = source.getPrecisionConfigurationName();
        verificationResultName = source.getVerificationResultName();
        modelName = source.getModelName();
        annotation = source.getAnnotation();
        iterationLimit = source.getIterationLimit();
        timeoutAmount = source.getTimeoutAmount();
        timeUnit = source.getTimeoutUnit();
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getFormulaName() {
        return formulaName;
    }

    public void setFormulaName(String formulaName) {
        this.formulaName = formulaName;
    }

    public String getInitialSpaceName() {
        return initialSpaceName;
    }

    public void setInitialSpaceName(String initialSpaceName) {
        this.initialSpaceName = initialSpaceName;
    }

    public int getIterationLimit() {
        return iterationLimit;
    }

    public void setIterationLimit(int iterationLimit) {
        this.iterationLimit = iterationLimit;
    }

    public String getPrecisionConfigurationName() {
        return precisionConfigurationName;
    }

    public void setPrecisionConfigurationName(String precisionConfigurationName) {
        this.precisionConfigurationName = precisionConfigurationName;
    }

    public String getSimulationSpaceName() {
        return simulationSpaceName;
    }

    public void setSimulationSpaceName(String simulationSpaceName) {
        this.simulationSpaceName = simulationSpaceName;
    }

    public long getTimeoutAmount() {
        return timeoutAmount;
    }

    public void setTimeoutAmount(long timeoutAmount) {
        this.timeoutAmount = timeoutAmount;
    }

    public TimeUnit getTimeoutUnit() {
        return timeUnit;
    }

    public void setTimeoutUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public String getVerificationResultName() {
        return verificationResultName;
    }

    public void setVerificationResultName(String verificationResultName) {
        this.verificationResultName = verificationResultName;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public boolean isFilled() {
        return ((formulaName != null) && (initialSpaceName != null)
                && (simulationSpaceName != null) && (precisionConfigurationName != null)
                && (modelName != null) && (iterationLimit > 0) && (timeoutAmount > 0));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ExperimentNames)) {
            return false;
        }
        ExperimentNames target = (ExperimentNames) obj;
        if (!getModelName().equals(target.getModelName())) {
            return false;
        }
        if (!getFormulaName().equals(target.getFormulaName())) {
            return false;
        }
        if (!getInitialSpaceName().equals(target.getInitialSpaceName())) {
            return false;
        }
        if (!getSimulationSpaceName().equals(target.getSimulationSpaceName())) {
            return false;
        }
        if (!getPrecisionConfigurationName().equals(target.getPrecisionConfigurationName())) {
            return false;
        }
        if (!getVerificationResultName().equals(target.getVerificationResultName())) {
            return false;
        }
        if (getTimeoutAmount() != target.getTimeoutAmount()) {
            return false;
        }
        if (getIterationLimit() != target.getIterationLimit()) {
            return false;
        }
        if (!getAnnotation().equals(target.getAnnotation())) {
            return false;
        }
        if (!getTimeoutUnit().equals(target.getTimeoutUnit())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 83;
        int result = getModelName().hashCode();
        result = result * prime + getFormulaName().hashCode();
        result = result * prime + getInitialSpaceName().hashCode();
        result = result * prime + getSimulationSpaceName().hashCode();
        result = result * prime + getPrecisionConfigurationName().hashCode();
        result = result * prime + getVerificationResultName().hashCode();
        result = result * prime + Long.valueOf(getTimeoutAmount()).hashCode();
        result = result * prime + getIterationLimit();
        result = result * prime + getAnnotation().hashCode();
        result = result * prime + getTimeoutUnit().hashCode();
        return result;
    }

    public static enum Factory {

        INSTANCE;

        private static String getAndValidate(Properties src, String property) throws ResourceException {
            String result = src.getProperty(property);
            if (result == null) {
                throw new ResourceException("`" + property + "' property missing.");
            }
            return result;
        }

        private static String getAndRemoveSuffix(Properties src, String property, ExperimentSuffixes suff) throws ResourceException {
            String fileName = src.getProperty(property);
            if (fileName == null) {
                return null;
            }
            String result = suff.remove(fileName);
            if (result == null) {
                throw new ResourceException("Wrong file name format: " + fileName + " (missing `" + suff.getSuffix() + "' suffix)");
            }
            return result;
        }

        public ExperimentNames getExperimentNames(Properties properties) throws ResourceException {
            ExperimentNames result = new ExperimentNames();
            result.setModelName(getAndRemoveSuffix(properties, MODEL_PRP, ExperimentSuffixes.MODEL));
            result.setFormulaName(getAndRemoveSuffix(properties, FORMULA_PRP, ExperimentSuffixes.FORMULA));
            result.setInitialSpaceName(getAndRemoveSuffix(properties, INITSPACE_PRP, ExperimentSuffixes.INITIAL_SPACE));
            result.setSimulationSpaceName(getAndRemoveSuffix(properties, SIMSPACE_PRP, ExperimentSuffixes.SIMULATION_SPACE));
            result.setPrecisionConfigurationName(getAndRemoveSuffix(properties, PRECISION_PRP, ExperimentSuffixes.PRECISION_CONFIGURATION));
            result.setVerificationResultName(getAndRemoveSuffix(properties, RESULT_PRP, ExperimentSuffixes.VERIFICATION_RESULT));
            String property = null;
            try {
                property = getAndValidate(properties, TIMEOUT_AMOUNT_PRP);
                result.setTimeoutAmount(Long.valueOf(property));
            } catch (NumberFormatException nfe) {
                throw new ResourceException("Wrong number format of timeout: " + property, nfe);
            }
            try {
                property = getAndValidate(properties, TIMEOUT_UNIT_PRP);
                result.setTimeoutUnit(TimeUnit.valueOf(property.toUpperCase()));
            } catch (RuntimeException e) {
                throw new ResourceException("Wrong timeout unit: " + property, e);
            }
            try {
                property = getAndValidate(properties, ITERATION_PRP);
                result.setIterationLimit(Integer.valueOf(property));
            } catch (NumberFormatException nfe) {
                throw new ResourceException("Wrong number format of iteration limit: " + property, nfe);
            }
            result.setAnnotation(properties.getProperty(ANNOTATION_PRP));
            return result;
        }

        private static void setIfNotNull(Properties properties, String name, String value, ExperimentSuffixes suffix) {
            if (value != null) {
                properties.setProperty(name, suffix.add(value));
            }
        }

        public Properties getProperties(ExperimentNames names) {
            Properties result = new Properties();
            setIfNotNull(result, MODEL_PRP, names.getModelName(), ExperimentSuffixes.MODEL);
            setIfNotNull(result, FORMULA_PRP, names.getFormulaName(), ExperimentSuffixes.FORMULA);
            setIfNotNull(result, INITSPACE_PRP, names.getInitialSpaceName(), ExperimentSuffixes.INITIAL_SPACE);
            setIfNotNull(result, SIMSPACE_PRP, names.getSimulationSpaceName(), ExperimentSuffixes.SIMULATION_SPACE);
            setIfNotNull(result, PRECISION_PRP, names.getPrecisionConfigurationName(), ExperimentSuffixes.PRECISION_CONFIGURATION);
            setIfNotNull(result, RESULT_PRP, names.getVerificationResultName(), ExperimentSuffixes.VERIFICATION_RESULT);
            result.setProperty(TIMEOUT_AMOUNT_PRP, Long.toString(names.getTimeoutAmount()));
            result.setProperty(TIMEOUT_UNIT_PRP, names.getTimeoutUnit().toString());
            result.setProperty(ITERATION_PRP, Integer.toString(names.getIterationLimit()));
            if (names.getAnnotation() != null && !names.getAnnotation().isEmpty()) {
                result.setProperty(ANNOTATION_PRP, names.getAnnotation());
            }
            return result;
        }

        public static Factory getInstance() {
            return INSTANCE;
        }
    }
    private static final String MODEL_PRP = "sbml.file";
    private static final String FORMULA_PRP = "stl.file";
    private static final String INITSPACE_PRP = "space.initial.file";
    private static final String SIMSPACE_PRP = "space.simulation.file";
    private static final String PRECISION_PRP = "simulation.precision.file";
    private static final String RESULT_PRP = "result.output.file";
    private static final String TIMEOUT_AMOUNT_PRP = "timeout.amount";
    private static final String TIMEOUT_UNIT_PRP = "timeout.unit";
    private static final String ITERATION_PRP = "iteration.limit";
    private static final String ANNOTATION_PRP = "annotation";
}
