package org.sybila.parasim.model.trajectory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class MutableDataBlock implements DataBlock<Trajectory> {

    private List<LinkedTrajectory> trajectories;
    
    public MutableDataBlock(List<Trajectory> trajectories) {
        if (trajectories == null) {
            throw new IllegalArgumentException("The parameter trajectories is null.");
        }
        if (trajectories.isEmpty()) {
            throw new IllegalArgumentException("The number of trajectories has to be a positive number.");
        }
        this.trajectories = new ArrayList<LinkedTrajectory>(trajectories.size());
        for(Trajectory trajectory: trajectories) {
            this.trajectories.add(new LinkedTrajectory(trajectory));
        }
    }
    
    public MutableDataBlock(DataBlock<Trajectory> trajectories) {
        if (trajectories == null) {
            throw new IllegalArgumentException("The parameter trajectories is null.");
        }
        this.trajectories = new ArrayList<LinkedTrajectory>(trajectories.size());
        for(Trajectory trajectory: trajectories) {
            this.trajectories.add(new LinkedTrajectory(trajectory));
        }        
    }
    
    public void append(DataBlock<Trajectory> trajectories) {
        if (size() != trajectories.size()) {
            throw new IllegalArgumentException("The number of given trajectories has to match with size of the block.");
        }
        for(int t=0;t<trajectories.size(); t++) {
            this.trajectories.get(t).append(trajectories.getTrajectory(t));
        }
    }
    
    public Trajectory getTrajectory(int index) {
        return trajectories.get(index);
    }

    public Iterator<Trajectory> iterator() {
        return new Iterator<Trajectory>() {

            private Iterator<LinkedTrajectory> iterator = trajectories.iterator();
            
            public boolean hasNext() {
                return iterator.hasNext();
            }

            public Trajectory next() {
                return (Trajectory) (iterator.next());
            }

            public void remove() {
                iterator.remove();
            }
        };
    }
    
    public void merge(DataBlock<Trajectory> trajectories) {
        for(Trajectory trajectory: trajectories) {
            this.trajectories.add(new LinkedTrajectory(trajectory));
        }
    }
    
    public int size() {
        return trajectories.size();
    }
}
