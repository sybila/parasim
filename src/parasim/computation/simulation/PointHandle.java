/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim.computation.simulation;

/**
 *
 * @author sven
 */
public class PointHandle implements Point {

    private int dimension;
    private int pointIndex;
    private float[] points;
    private int positionOnTrajectory;

    PointHandle(float[] points, int pointIndex, int dimension, int positionOnTrajectory)
    {
        if ( pointIndex < 0 || dimension <=0 ||
             pointIndex + dimension > points.length ||
             positionOnTrajectory < 0 )
        {
            throw new ArrayIndexOutOfBoundsException("Bad PointHandle constructor parameters");
        }
        this.dimension = dimension;
        this.pointIndex = pointIndex;
        this.points = points;
        this.positionOnTrajectory = positionOnTrajectory;
    }
    /**
     * @return Number of dimensions of given point.
     */
    public int getDimension()
    {
        return dimension;
    }

    /**
     * @return Value of time dimension 0, is equal to getValue(0)
     */
    public float getTime()
    {
        return points[pointIndex];
    }

    /**
     * @param index The dimension of who's value to return.
     * @return Value of given dimension.
     */
    public float getValue(int dimIndex)
    {
        if (dimIndex < 0 || dimIndex >= dimension)
        {
            throw new ArrayIndexOutOfBoundsException("Bad PointHandle getValue dimension index [ "+dimIndex+" ]");
        }
        return points[pointIndex+dimIndex];
    }

    /**
     * @return Values of all dimensions as an array, time is on dimension 0.
     */
    public float[] toArray()
    {
        float[] tmp = new float[dimension];
        java.lang.System.arraycopy(points, pointIndex, tmp, 0, dimension);
        return tmp;
    }

    /**
     * @param dest Destination where to copy dimension values.
     * @param destPos Position inside destination where to copy values.
     */
    public void arrayCopy(float[] dest, int destPos)
    {
        System.arraycopy(points, pointIndex, dest, destPos, dimension);
    }

    /**
     * Returns the position of this point on it's trajectory.
     * @return position on trajectory
     */
    public int getPositionOnTrajectory()
    {
        return positionOnTrajectory;
    }
}
