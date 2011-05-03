/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim;

import processing.core.PApplet;

/**
 * Structure to hold simulation points
 * @author sven
 */
public class PointsDataBlock
{
  int first_point_index;      /* Index of the first point in this data block */
  int count;                  /* Number of points in data block, if block is
                                 not the last one of the simulation then
                                 point_count == SIMUL_DATA_BLOCK, else it is
                                 smaller. */
  PointsDataBlock next;       /* Pointers to the next and previous data block */
  PointsDataBlock prev;

  Point[] points;           /* Array of points, each point is a dims-tuple. */

  /* FUTU LTL states
     ltl_state[] ltl_states; */

  PointsDataBlock(int fpi, PointsDataBlock previous)
  {
    first_point_index = fpi;
    count = 0;
    next = null;
    prev = previous;
    points = new Point[Utils.POINTS_DATA_BLOCK_LENGTH];
  }

  void insert_point(Point p)
  {
    points[0] = p;
    count = 1;
  }

  /*
  Inserts the given points into the PDB and returns the pointer of the PDB
  where the last point was inserted into.
  If there is not enough memory to allocate new points
  an Error is returned in err.
  */
  PointsDataBlock insert_points(Point[] pts, int offset, int length, StatusMod err)
  {
    if (length <= points.length-count)
    {
      PApplet.arrayCopy(pts, offset, points, count, length);
      count += length;
      return this;
    }
    else
    {
      PApplet.arrayCopy(pts, offset, points, count, points.length-count);
      length -= points.length-count;
      offset += points.length-count;
      count = points.length;
      try {
        next = new PointsDataBlock(first_point_index + points.length, this);
        return next.insert_points(pts, offset, length, err);
      } catch (OutOfMemoryError e) {
        next = null;
        err.set(Utils.ERR_OUT_OF_MEMORY);
        return this;
      }
    }
  }

  /**
   * Deletes all points in the points field.
   *
   @return the next PointsDataBlock
   **/
  PointsDataBlock delete()
  {
    points = null;
    prev = null;
    if (next != null)
    {
      next.prev = null;
    }
    return next;
  }

  /**
   * Expected to be called once it is known that no more points will ever be
   * added to the structure. Trims of the unused part of the points array.
   **/
  void conserve_points()
  {
    points = (Point[]) PApplet.expand(points, count);
  }
}
