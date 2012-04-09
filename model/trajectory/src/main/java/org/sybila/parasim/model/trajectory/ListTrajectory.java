package org.sybila.parasim.model.trajectory;

import java.util.List;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ListTrajectory extends AbstractTrajectory {

    private List<Point> points;

    /**
     * Creates a new ListTrajectory with the given points.
     *
     * @param points point sequence
     */
    public ListTrajectory(List<Point> points) {
        super(points.get(0).getDimension(), points.size());
        this.points = points;
    }

    @Override
    public Point getPoint(int index) {
        return points.get(index);
    }
}
