/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim;

/**
 * Class to hold intervals of points and operate on them during distance
 * checking. The time order of points if from the first to the last point
 * (last point has time dimension bigger then the first point).
 *
 * @author sven
 */
public class Interval
{
  PointPosition first, last;

  Interval(PointPosition f, PointPosition l)
  {
    first = f;
    last = l;
  }

  void extend(PointPosition l)
  {
    last = l;
  }
}
