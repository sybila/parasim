package org.sybila.parasim.computation.simulation;

import org.sybila.parasim.model.ode.OdeSystem;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ImmutableConfiguration implements Configuration {

    public static final int DEFAULT_MAX_NUMBER_OF_ITERATIONS = 100000;
    public static final float DEFAULT_STEP = (float) 0.1;
    public static final float DEFAULT_TIME_STEP = (float) 0.1;
    
    private float[] maxBounds;
    private int maxNumberOfIterations;
    private float[] minBounds;
    private OdeSystem odeSystem;
    private float[] steps;
    private float timeStep;
    private float targetTime;
    
    public ImmutableConfiguration(OdeSystem odeSystem, float[] maxBounds, float[] minBounds, float targetTime) {
        this(
            odeSystem,
            maxBounds,
            minBounds,
            targetTime,
            getConstantArray(odeSystem.getDimension(), DEFAULT_STEP),
            (DEFAULT_TIME_STEP > targetTime ? targetTime : DEFAULT_TIME_STEP),
            DEFAULT_MAX_NUMBER_OF_ITERATIONS
        );
    }
    
    public ImmutableConfiguration(OdeSystem odeSystem, float[] maxBounds, float[] minBounds, float targetTime, float[] steps, float timeStep, int maxNumberOfIterations) {
        if (odeSystem == null) {
            throw new IllegalArgumentException("The paremeter odeSystem is null.");
        }
        if (maxBounds == null) {
            throw new IllegalArgumentException("The paremeter maxBounds is null.");
        }
        if (minBounds == null) {
            throw new IllegalArgumentException("The paremeter minBounds is null.");
        }
        if (steps == null) {
            throw new IllegalArgumentException("The paremeter steps is null.");
        }
        if (odeSystem.getDimension() != maxBounds.length) {
            throw new IllegalArgumentException("The number of dimensions doesn't match with maxBounds size.");
        }
        if (odeSystem.getDimension() != minBounds.length) {
            throw new IllegalArgumentException("The number of dimensions doesn't match with minBounds size.");
        }        
        if (odeSystem.getDimension() != steps.length) {
            throw new IllegalArgumentException("The number of dimensions doesn't match with steps size.");
        }        
        if (timeStep <= 0) {
            throw new IllegalArgumentException("The time step has to be a positive number.");
        }
        if (targetTime <= 0) {
            throw new IllegalArgumentException("The target time has to be a positive number.");
        }
        if (maxNumberOfIterations <= 0) {
            throw new IllegalArgumentException("The max number of iterations has to be a positive number.");
        }        
        if (timeStep > targetTime) {
            throw new IllegalArgumentException("The time step can't be higher than target time");
        }
        this.odeSystem = odeSystem;
        this.maxBounds = maxBounds;
        this.minBounds = minBounds;
        this.targetTime = targetTime;
        this.steps = steps;
        this.timeStep = timeStep;
        this.maxNumberOfIterations = maxNumberOfIterations;
        
    }
    
    
    @Override
    public int getDimension() {
        return odeSystem.getDimension();
    }

    @Override
    public float[] getMaxBounds() {
        return maxBounds;
    }

    @Override
    public int getMaxNumberOfIterations() {
        return maxNumberOfIterations;
    }

    @Override
    public float[] getMinBounds() {
        return minBounds;
    }

    @Override
    public OdeSystem getOdeSystem() {
        return odeSystem;
    }

    @Override
    public float[] getSteps() {
        return steps;
    }

    @Override
    public float getTargetTime() {
        return targetTime;
    }

    @Override
    public float getTimeStep() {
        return timeStep;
    }
    
    
    private static float[] getConstantArray(int size, float number) {
        float[] result = new float[size];
        for(int i=0; i<size; i++) {
            result[i] = number;
        }
        return result;
    }
}
