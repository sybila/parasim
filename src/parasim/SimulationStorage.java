/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim;

import parasim.verification.Property;

/**
 * Object for storing simulations.
 *
 * @author sven
 */
public class SimulationStorage
{
  int count;
  SimulationData[] simulations;

  SimulationStorage()
  {
    count = 0;
    simulations = new SimulationData[Utils.MAX_SIMULATIONS];
  }

  /**
   * Inserts a new simulation with it's initial point.
   * Returns index on success or -1 on failure (array full).
   * initial_conditions is inserted as is without copying.
   *
   @param initial_conditions coordinates of initial point
   @param neighbours array of neighbours for distance checking
   @param merge_detect mode of distance checking
  */
  int insert(Point initial_conditions, SimulationData[] neighbours,
             Property property, boolean merge_detect)
  {
    if (count < Utils.MAX_SIMULATIONS)
    {
      simulations[count] = new SimulationData(count, initial_conditions,
                                              neighbours, property, merge_detect);
      count++;
      return count-1;
    }
    else return -1;
  }

  /**
   * Inserts new simulations with their initial points.
   *
   @param  point_set set of point coordinates and their neighbours,
           neighbours must only be among point in currently inserted set
   @param  merge_detect the mode of distance checking of inserted simulations
   @return number of successfuly inserted simulations
  */
  int insert(InitialPointSet point_set, Property property, boolean merge_detect)
  {
    int sim_offset = count;
    int curr_point = 0;
    int i, tmp;
    SimulationData[] neighbours = new SimulationData[0];

    while (curr_point < point_set.get_point_count() && count < Utils.MAX_SIMULATIONS)
    {
      neighbours = (SimulationData[])
                   Utils.expand(neighbours, point_set.get_neighbour_count(curr_point));
      for (i = 0; i<neighbours.length; i++)
      {
        neighbours[i] = simulations[sim_offset+point_set.get_point_neighbour(curr_point,i)];
      }
      tmp = insert(point_set.get_point(curr_point), neighbours, property, merge_detect);
      if (tmp == -1) return (count - sim_offset);

      curr_point++;
    }
    return (count - sim_offset);
  }

  /**
   * Extends a given simulation by inserting dense and/or sparse points to the end.
   * Returns number of points added (result[0] = dense, result[1] = sparse).
   * Since variables are passed by reference, the given data is
   * incorporated as is without creating copies.
   */
  int[] extend(int sim_index, Point[] d_points, int d_points_count,
                              Point[] s_points, int s_points_count, StatusMod err)
  {
    if (sim_index < 0 || sim_index >= Utils.MAX_SIMULATIONS)
    {
      return (new int[]{-2});
    }
      /* bad simulation index */
    if (sim_index > count)
    {
      /* simulation with such index is not yet initialised */
      return (new int[]{-1});
    }
    int old_dense = simulations[sim_index].total_dense_length;
    int old_sparse = simulations[sim_index].total_sparse_length;
    simulations[sim_index].insert_points(d_points, d_points_count,
                                         s_points, s_points_count, err);
    return (new int[]{simulations[sim_index].total_dense_length - old_dense,
                      simulations[sim_index].total_sparse_length - old_sparse});
  }

  /**
   * Returns the simulation's last point. Mode 0 for last point from the
   * dense array, mode 1 for the last sparse array.
   */
  Point get_last_sim_point(int sim_index, int mode)
  {
    return simulations[sim_index].get_last_point(mode);
  }
}

