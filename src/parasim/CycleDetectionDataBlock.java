/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim;

/**
 * Cycles are detected by holding the last extreme points in every dimension
 * and comparing them to newly computed extremes.
 *
 * @author sven
 */
public class CycleDetectionDataBlock
{
  Point p1, p2;
    /* Two last points from simulation, if the trend on some dimension D
       changes from increasing to decreasing the local maximum of this
       dimension has been detected and such point is inserted into the
       dim_extremes[D] stack. */
  PositionStack[] dim_extremes;
    /* positions of extremes (max) on each dimension */

  CycleDetectionDataBlock(Point initial_point, int stack_size)
  {
    dim_extremes = new PositionStack[initial_point.getDims()-1];
    for (int i = 0; i<dim_extremes.length; i++)
    {
      dim_extremes[i] = new PositionStack(stack_size);
    }
    p1 = initial_point;
    p2 = initial_point;
  }

  /**
   * Detects extreme (maximum) values on any dimension in a stream of dense
   * points. With each call the values p1 and p2 of the last two points are
   * updated. If for some dimension D holds p1[D] < p2[d] > new_point[D] then
   * p2 is a local maximum.
   *
   * Returns an array with all dimensions where an extreme value was found.
   **/
  int[] detect_extremes(Point new_point)
  {
    int[] ex_dims = new int[new_point.getDims()-1];
    int ex_dims_counter = 0;

    for (int i=1; i<new_point.getDims(); i++) /* i=1 time is not accounted */
    {
      if (p2.get(i) > p1.get(i) && new_point.get(i) < p2.get(i))
      {
        ex_dims[ex_dims_counter] = i;
        ex_dims_counter++;
      }
    }
    p1 = p2;
    p2 = new_point;
    if (ex_dims_counter > 0)
    {
      return ((int[]) Utils.expand(ex_dims, ex_dims_counter));
    }
    return null;
  }

  /**
   * Compares the new extreme point with position new_pos to the ones already
   * on stack. If some point is found to be less then ode.min_check_dist
   * far away, it's position is returned.
   * If not the new extreme point is added to all stacks
   * where it is an extreme as indicated by the ex_dims array.
   *
   @param new_pos position of the new extreme to be compared
   @param ex_dims dimensions in which the new point is an extreme
   @param ode the system used for distance comparison
   **/
  PointPosition detect_cycle(PointPosition new_pos, int[] ex_dims, ODE ode)
  {
    PointPosition pos = null;
    for (int i=0; i<ex_dims.length; i++)
    {
      pos = find_close(new_pos.pdb.points[new_pos.offset], ex_dims[i]-1, ode);
      /* since dim_extremes[0] corresponds to dim[1] => -1 */
      if (pos != null)
      {
        return pos;
      }
      dim_extremes[ex_dims[i]-1].add(new_pos);
    }
    return null;
  }

  /* Checks all the points in dim_extremes[dim_extreme] if any one is closer
     than ode.min_check_dist, returns it's position if found, else null. */
  PointPosition find_close(Point p, int dim_extreme, ODE ode)
  {
    PointPosition pos;
    for (int i = 0; i<dim_extremes[dim_extreme].count; i++)
    {
      pos = dim_extremes[dim_extreme].get(i);
      if (ode.point_dist_comp(p, pos.pdb.points[pos.offset], 0, false))
      {
        return pos;
      }
    }
    return null;
  }
}

