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
public class LotkaVolteraInitialDataBlock extends ListDataBlock<Trajectory> {
    
    public LotkaVolteraInitialDataBlock() {
        super(getInitialTrajectories());
    }
    
    
    private static List<Trajectory> getInitialTrajectories() {
        List<Trajectory> trajectories = new ArrayList<Trajectory>();
        for(float x=1; x<=10; x+=3) {
            for(float y=1; y<=10; y+=3) {
                trajectories.add(new PointTrajectory(new ArrayPoint(new float[] {x, y}, 0)));
            }
        }
        return trajectories;
    }
}
