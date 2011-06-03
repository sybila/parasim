/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim.computation.simulation;

/**
 *
 * @author Sven Dražan <sven@mail.muni.cz>
 */
public class SimplePoint implements Point {

    private float[] point;
    private int position;

    SimplePoint(float[] point, int position)
    {
        this.point = point;
        this.position = position;
    }

    public int getDimension()
    {
        return point.length;
    }

    public float getTime()
    {
        return point[0];
    }

    public float getValue(int index)
    {
        return point[index];
    }

    public float[] toArray()
    {
        return point;
    }

    public void arrayCopy(float[] dest, int destPos)
    {
        System.arraycopy(point, 0, dest, destPos, point.length);
    }

    public int getPositionOnTrajectory()
    {
        return position;
    }

}
