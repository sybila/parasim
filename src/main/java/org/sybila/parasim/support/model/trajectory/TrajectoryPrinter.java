package org.sybila.parasim.support.model.trajectory;

import java.io.PrintStream;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TrajectoryPrinter {
   
    public static void printTrajectory(Trajectory trajectory, PrintStream out) {
        for(Point point : trajectory) {
            out.print(point);
        }
        out.println();
    }
    
}