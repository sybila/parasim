package org.sybila.parasim.model.trajectory;

/**
 * Supplements a Point by giving it an unique position number on it's trajectory.
 * A point's position on a trajectory must not change even if the
 * trajectory is prolonged or truncated (from begining or end).
 *
 * This means that as long as the trajectory T contains the given point P
 * calling T.getPoint(P.getPoisitionOnTrajectory()) must return P.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public interface PointExtension extends Point {

    /**
     * Returns the position of this point on it's trajectory as an integer.
     * The first point on a untruncated trajectory has index 0.
     *
     * @return Unique position on trajectory.
     */
    int getPositionOnTrajectory();

    /**
     * Copies the dimension values to specified array.
     * @param dest Destination where to copy dimension values.
     * @param destPos Position inside destination where to copy values.
     */
    void arrayCopy(float[] dest, int destPos);
}
