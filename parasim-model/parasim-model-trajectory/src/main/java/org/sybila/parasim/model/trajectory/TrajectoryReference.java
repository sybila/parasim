package org.sybila.parasim.model.trajectory;

/**
 * This interfaces provides reference to the trajectory. It can be
 * used when one needs to memorize the trajectory and the trajectory instance
 * can be replaced meanwhile.
 * 
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface TrajectoryReference {
    
    /**
     * Returns a trajectory which is pointed by this reference
     * 
     * @return pointed trajectory 
     */
    Trajectory getTrajectory();
    
    /**
     * Sets a trajectory which is pointed by this reference
     * 
     * @param trajectory 
     */
    void setTrajectory(Trajectory trajectory);
    
    
}
