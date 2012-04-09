package org.sybila.parasim.model.trajectory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A mutable list of trajectories. If T is a MutableTrajectory then append
 * is implemented otherwise it throws an exception.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public class ListMutableDataBlock implements MutableDataBlock {

    private List<MutableTrajectory> trajectories;

    public ListMutableDataBlock(List<Trajectory> trajectories) {
        if (trajectories == null) {
            throw new IllegalArgumentException("The parameter trajectories is null.");
        }
        if (trajectories.isEmpty()) {
            throw new IllegalArgumentException("The number of trajectories has to be a positive number.");
        }
        this.trajectories = new ArrayList<MutableTrajectory>(trajectories.size());
        for (Trajectory trajectory : trajectories) {
            this.trajectories.add(new LinkedTrajectory(trajectory));
        }
    }

    public ListMutableDataBlock(DataBlock<Trajectory> trajectories) {
        if (trajectories == null) {
            throw new IllegalArgumentException("The parameter trajectories is null.");
        }
        this.trajectories = new ArrayList<MutableTrajectory>(trajectories.size());
        for (Trajectory trajectory : trajectories) {
            if (trajectory instanceof MutableTrajectory) {
                this.trajectories.add((MutableTrajectory) trajectory);
            } else {
                this.trajectories.add(new LinkedTrajectory(trajectory));
            }
        }
    }

    @Override
    public void append(DataBlock<Trajectory> trajectories) {
        if (size() != trajectories.size()) {
            throw new IllegalArgumentException("The number of given trajectories has to match with size of the block.");
        }
        Iterator<MutableTrajectory> originIt = this.trajectories.iterator();
        Iterator<Trajectory> appendIt = trajectories.iterator();
        while (originIt.hasNext() && appendIt.hasNext()) {
            originIt.next().append(appendIt.next());
        }
    }

    @Override
    public Trajectory getTrajectory(int index) {
        return trajectories.get(index);
    }

    @Override
    public Iterator<Trajectory> iterator() {
        return new Iterator<Trajectory>() {

            private Iterator<MutableTrajectory> iterator = trajectories.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Trajectory next() {
                return iterator.next();
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }

    @Override
    public void merge(DataBlock<Trajectory> trajectories) {
        for (Trajectory trajectory : trajectories) {
            if (trajectory instanceof MutableTrajectory) {
                this.trajectories.add((MutableTrajectory) trajectory);
            } else {
                this.trajectories.add(new LinkedTrajectory(trajectory));
            }
        }
    }

    @Override
    public int size() {
        return trajectories.size();
    }
}
