package org.sybila.parasim.suport.gpu.model.trajectory;

import org.sybila.parasim.model.trajectory.AbstractDataBlock;
import org.sybila.parasim.model.trajectory.ArrayPointLocator;
import org.sybila.parasim.model.trajectory.ArrayTrajectory;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;

/*
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class DefaultWorkspaceDataBlockFactory implements WorkspaceDataBlockFactory {

    public DataBlock<Trajectory> createDataBlock(float[] points, float[] times, int[] lengths, int dimension) {
        return new WorkspaceDataBlock(points, times, lengths, dimension);
    }
    
    private class WorkspaceDataBlock extends AbstractDataBlock<Trajectory> {

        private int dimension;
        private int[] lengths;
        private ArrayPointLocator pointLocator;
        private float[] points;
        private float[] times;
        
        public WorkspaceDataBlock(float[] points, float[] times, int[] lengths, final int dimension) {
            super(lengths.length);
            if (points == null) {
                throw new IllegalArgumentException("The parameter points is null");
            }
            if (times == null) {
                throw new IllegalArgumentException("The parameter times is null");
            }
            if (dimension <= 0) {
                throw new IllegalArgumentException("The dimension has to be a positive number.");
            }
            this.points = points;
            this.times = times;
            this.lengths = lengths;
            this.dimension = dimension;
            int maxLength = 0;
            for(int i=0; i<lengths.length; i++) {
                if (lengths[i] > maxLength) {
                    maxLength = lengths[i];
                }
            }
            pointLocator = new ArrayPointLocator() {

                public int getPointPosition(int trajectoryIndex, int pointIndex) {
                    return pointIndex * getSize() * dimension;
                }

                public int getTimePosition(int trajectoryIndex, int pointIndex) {
                    return pointIndex * getSize();
                }
            };
        }
        
        public Trajectory getTrajectory(int index) {
            if (index < 0 || index >= getSize()) {
                throw new IndexOutOfBoundsException("The index is out of the range [" + 0 + ", " + (getSize() - 1) + "].");
            }
            return new ArrayTrajectory(points, times, dimension, lengths[index], pointLocator);
        }

        public int size() {
            return lengths.length;
        }
        
    }
    
}
