package org.sybila.parasim.support.computation.simulation;

import java.util.ArrayList;
import java.util.List;
import org.sybila.parasim.computation.ListDataBlock;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.PointTrajectory;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class BayramovInitialDataBlock extends ListDataBlock<Trajectory> {
    
    public BayramovInitialDataBlock() {
        super(getInitialTrajectories());
    }
    
    private static List<Trajectory> getInitialTrajectories() {
        List<Trajectory> trajectories = new ArrayList<Trajectory>();
        for(float x=(float)0.0015; x<0.0040; x+=0.0005) {
            for(float y=(float)0.00095; y<0.00405; y+=0.0005) {
                for(float z=(float)0.00038; z<0.00482; z+=0.0005) {
                    trajectories.add(new PointTrajectory(new ArrayPoint(new float[] {x, y, z}, 0)));
                }
            }
        }
        return trajectories;
    }
    
}
