package org.sybila.parasim.computation.lifecycle;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class Resources {
 
    private long time;
    
    public Resources(long time) {
        if (time <= 0) {
            throw new IllegalArgumentException("The parameter [time] has to be a positive number.");
        }
        this.time = time;
    }
    
    public Resources() {
        this(Long.MAX_VALUE);
    }
    
    /**
     * @return time in miliseconds
     */
    public long getTime() {
        return time;
    }
    
    /**
     * @param time in miliseconds
     */
    public void setTime(long time) {
        if (time <= 0) {
            throw new IllegalArgumentException("The parameter [time] has to be a positive number.");
        }
        this.time = time;
    }
    
}
