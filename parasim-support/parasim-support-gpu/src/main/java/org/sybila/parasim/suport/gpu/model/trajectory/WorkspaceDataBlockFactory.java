package org.sybila.parasim.suport.gpu.model.trajectory;

import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;

/*
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface WorkspaceDataBlockFactory {
    
    DataBlock<Trajectory> createDataBlock(float[] points, float[] times, int[] lengths, int dimension);
    
}
