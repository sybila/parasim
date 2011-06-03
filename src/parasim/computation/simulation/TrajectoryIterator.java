
package parasim.computation.simulation;

import java.util.Iterator;

/**
 * Enables iterating over point of a trajectory with arbitrary jumps.
 * @author Sven Dražan <sven@mail.muni.cz>
 */
public interface TrajectoryIterator extends Iterator<Point> {

    boolean hasNext(int jump);

    Point next(int jump);

}
