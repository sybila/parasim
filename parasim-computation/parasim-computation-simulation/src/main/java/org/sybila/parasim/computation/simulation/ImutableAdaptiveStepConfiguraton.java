package org.sybila.parasim.computation.simulation;

import org.sybila.parasim.model.ode.OdeSystem;

/*
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ImutableAdaptiveStepConfiguraton implements AdaptiveStepConfiguration {
    
    private Configuration configuration;
    private float[] maxAbsoluteError;
    private float maxRelativeError;
    
    public ImutableAdaptiveStepConfiguraton(Configuration configuration, float[] maxAbsoluteError, float maxRelativeError) {
        if (configuration == null) {
            throw new IllegalArgumentException("The parameter configuration is null.");
        }
        if (maxAbsoluteError == null) {
            throw new IllegalArgumentException("The parameter maxAbsoluteError is null.");
        }
        if (configuration.getDimension() != maxAbsoluteError.length) {
            throw new IllegalArgumentException("The number of dimensions doesn't match with size of maxAbsoluteError array.");
        }
        this.configuration = configuration;
        this.maxAbsoluteError = maxAbsoluteError;
        this.maxRelativeError = maxRelativeError;
    }

    @Override
    public float[] getMaxAbsoluteError() {
        return maxAbsoluteError;
    }

    @Override
    public float getMaxRelativeError() {
        return maxRelativeError;
    }

    @Override
    public int getDimension() {
        return configuration.getDimension();
    }

    @Override
    public float[] getMaxBounds() {
        return configuration.getMaxBounds();
    }

    @Override
    public int getMaxNumberOfIterations() {
        return configuration.getMaxNumberOfIterations();
    }

    @Override
    public float[] getMinBounds() {
        return configuration.getMinBounds();
    }

    @Override
    public OdeSystem getOdeSystem() {
        return configuration.getOdeSystem();
    }

    @Override
    public float[] getSteps() {
        return configuration.getSteps();
    }

    @Override
    public float getTargetTime() {
        return configuration.getTargetTime();
    }

    @Override
    public float getTimeStep() {
        return configuration.getTimeStep();
    }
    
}
