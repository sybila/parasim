/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim;

/**
 * Class representing an initial set of points filling the initial conditions.
 * Contains point coordinates and each point's neighbours.
 *
 * Class used only for ODE.generate_inital_points method as data container.
 * @author sven
 */
public class InitialPointSet {

  private Point[] points;         /* points and their coordinates */
  private int[][] neighbours;     /* indexes of neighbours of each point */

  /**
   * Constructs a set of points
   * @param size number of initial points in set
   */
  InitialPointSet(int size)
  {
    points = new Point[size];
    neighbours = new int[size][];
  }

  /**
   * Sets the point on given index to p. Does not perform any range checking.
   * @param index position to insert point
   * @param p point to insert
   */
  void set_point(int index, Point p)
  {
    points[index] = p;
  }

  /**
   * No range checkign is performed. FIXME
   *
   * @param index
   * @return
   */
  Point get_point(int index)
  {
    return points[index];
  }

  /**
   * For a point with given index and grid coordinates finds it's grid
   * neighbours and saves their indices in the neighbours[index].
   * Neighbours are points with exactly one grid coordinate smaller by 1, their
   * index is computed from the known number of points in subgrid.
   *
   * @param index      position of given point in the points array
   * @param coords     grid coordinates of point
   * @param dimensions number of grid points in each dimension
   */
  void set_point_neighbours(int index, int[] coords, int[] subgrid)
  {
    int ncount = 0;
    int i, j, nindex;
    for (i = 0; i < coords.length; i++)
    {
      if (coords[i] > 0) ncount++; /* computing number of neighbours */
    }
    neighbours[index] = new int[ncount];
    ncount = 0;
    for (i = 0; i < coords.length; i++)
    {
      if (coords[i] > 0) {
        nindex = 0;
        for (j = 0; j < coords.length; j++)
        {
          if (j != i)
          {
            nindex += subgrid[j]*coords[j];
          }
          else
          {
            nindex += subgrid[j]*(coords[j]-1);
          }
        }
        neighbours[index][ncount] = nindex;
        ncount++;
      }
    }
  }

  public int get_point_count()
  {
    return points.length;
  }

  public int get_neighbour_count(int p_index)
  {
    return neighbours[p_index].length;
  }

  /**
   *
   * @param p_index
   * @param n_index
   * @return
   */
  int get_point_neighbour(int p_index, int n_index)
  {
     return neighbours[p_index][n_index];
  }
}
