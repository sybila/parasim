package org.sybila.parasim.support.computation;

import java.io.PrintStream;
import org.sybila.parasim.computation.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.support.model.trajectory.TrajectoryPrinter;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class DataBlockPrinter {

    public static void printDataBlock(DataBlock<Trajectory> dataBlock, PrintStream out) {
        for (Trajectory trajectory : dataBlock) {
            TrajectoryPrinter.printTrajectory(trajectory, out);
        }
    }
    
}
