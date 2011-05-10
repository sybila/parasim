/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim;

/**
 *
 * @author sven
 */
public class Point {
    private float[] coords; /* coordinates */

    public Point(int dims)
    {
        coords = new float[dims];
    }

    public Point (float[] c)
    {
        coords = c;
    }

    public void set(int index, float val)
    {
        coords[index] = val;
    }

    public float get(int index)
    {
        return coords[index];
    }

    public int getDims()
    {
        return coords.length;
    }
}
