/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim;

/**
 * Class to hold a stack of points. Mainly for cycle detection.
 * FIXME is not a stack
 * @author sven
 */
public class PositionStack
{
  PointPosition[] positions;
  int count;
  private int bottom;

  PositionStack(int size)
  {
    positions = new PointPosition[size];
    count = 0;
    bottom = 0;
  }

  /* Adds a point to the top of the stack. If full, the oldest point is discarded. */
  void add(PointPosition position)
  {
    if (count < positions.length)
    {
      positions[count] = position;
      count++;
    }
    else
    {
      positions[bottom] = position;
      bottom = (bottom+1) % positions.length;
    }
  }

  /* Returns the i-th position, 0 being the last added and count-1 being the
     first added. */
  PointPosition get(int index)
  {
    if (index < 0 || index >= count)
    {
      return null;
    }
    else if (count <= positions.length)
    {
      return positions[count-index-1];
    }
    else {
      return positions[(positions.length-index-1 + bottom)%positions.length];
    }
  }
}