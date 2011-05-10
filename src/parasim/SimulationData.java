/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim;

import parasim.verification.Property;

/**
 *
 * @author sven
 */
public class SimulationData
{
  int sim_index;                 /* Simulation index (unique through computation
                                    on all nodes).
                                    sim_index % nodes gives the node that hosts
                                    the simulation, sim_index / nodes gives the
                                    position of this simulation in sim_list
                                 */

  int total_dense_length;        /* The total number of points currently in the
                                    dense points array.
                                    Since the dense array is being deleted from
                                    the begining after checking all the
                                    neighbours on the given segment, this
                                    variable may fluctuate.
                                 */
  int total_sparse_length;       /* The total number of points currently in the
                                    sparse points array. Since the sparse array
                                    is needed for a long time (possibly until
                                    all the simulations neighbours have finished
                                    their simulation and checking and no new
                                    simulations are introduced through
                                    densification) it is considered
                                    nondecreasing.
                                 */

  PointsDataBlock first_d_pdb;   /* First dense points data block. */
  PointsDataBlock last_d_pdb;    /* Last dense points data block. */

  /* Linked data blocks of points (each point is a dims-tuple).
     The dense points are used for distance checkging on the local side
     and when all neighbours are checked they are deleted.

     Each two consecutive points in the dense array are at most
     ode.min_check_dist[d] far away from each other on every dimension d.
  */

  PointsDataBlock first_s_pdb;   /* First sparse points data block. */
  PointsDataBlock last_s_pdb;    /* Last sparse points data block. */

  /* A subset of the dense points array such that each two consecutive points
     are at most ode.max_check_dist[d] far away. It is expected to contain
     only about every 100th or 1000th point from the dense points array.
     When other simulations carry out distance checking to this simulation,
     they check their dense arrays against these sparse arrays.

     FUTU: sparse points should also contain all boundary points where the
     set of ltl_states changes.
  */

  /* FUTU AVL point tree for distance checking of simulations from disjoint
     initial condition regions */

  DistanceCheckDataBlock[] dist_check;
  /* Array of DCDB's to carry out distance checking on neighbours */
  CycleDetectionDataBlock cyc_detect;

  int status;         /* One of SIM_STATUS_* constants */
  int end_status;     /* One of SIM_END_STATUS_* constants */

  PointsDataBlock cycle_s_pdb;
  int cycle_offset;
  /* If the simulation ends in a cycle, the last_s_pdb loops back into a
     position specified by the cycle_s_pdb (a pdb somewhere between the
     first_s_pdb and the last_s_pdb) and the cycle_offset inside this pdb.
     If the simulation does not end in a cycle cycle_s_pdb == null and
     cycle_offset == -1;
  */

  PropertyCheckDataBlock property_check;

  /** Constructor
   *
   @param index the unique index of this simulation
   @param point the coordinates of the initial point of the simulation
   @param neighbours array of neighbours, these will be used during distance checking
   @param merge_detect mode of distance checking
   */
  SimulationData(int index, Point p, SimulationData[] neighbours, Property property, boolean merge_detect)
  {
    total_dense_length = 1;
    total_sparse_length = 1;
    sim_index = index;
    status = Utils.SIM_STATUS_COMP;
    end_status = Utils.SIM_END_STATUS_COMP;

    first_d_pdb = new PointsDataBlock(0, null);
    first_d_pdb.insert_point(p);
    last_d_pdb = first_d_pdb;
    first_s_pdb = new PointsDataBlock(0, null);
    first_s_pdb.insert_point(p);
    last_s_pdb = first_s_pdb;

    cyc_detect = new CycleDetectionDataBlock(p, Utils.CYC_DETECT_STACK_LEN);
    cycle_s_pdb = null;
    cycle_offset = -1;

    property_check = new PropertyCheckDataBlock(this, property);

    this.dist_check = new DistanceCheckDataBlock[neighbours.length];
    for (int i = 0; i < neighbours.length; i++)
    {
      //println("Sim("+index+").neighbours = "+neighbours[i].sim_index);
      dist_check[i] = new DistanceCheckDataBlock(neighbours[i], this, merge_detect);
    }
  }

  /**
   * Inserts new points into the dense and sparse points arrays.
   * If memory is sufficient then true is returned else false.
   */
  boolean insert_points(Point[] d_points, int d_points_count,
                        Point[] s_points, int s_points_count, StatusMod err)
  {
    err.set(0);
    PointsDataBlock new_last_d_pdb;

    if (d_points != null)
    {
      new_last_d_pdb = last_d_pdb.insert_points(d_points, 0, d_points_count, err);
      if (err.get() == 0) {
        total_dense_length += d_points_count;
        last_d_pdb = new_last_d_pdb;
      }
      else
      {
        total_dense_length +=
        new_last_d_pdb.first_point_index - last_d_pdb.first_point_index +
        new_last_d_pdb.count - last_d_pdb.count;
        last_d_pdb = new_last_d_pdb;
        return false;
      }
    }

    if (s_points != null)
    {
      last_s_pdb = last_s_pdb.insert_points(s_points, 0, s_points_count, err);
      if (err.get() == 0) {
        total_sparse_length += s_points_count;
      }
      else
      {
        total_sparse_length = last_s_pdb.first_point_index + last_s_pdb.count;
        return false;
      }
    }

    return true;
  }


  /**
   * Returns every n-th point in the given array starting from the first and
   * adding also the last. Mode = 0 for dense array, 1 for sparse.
   */
  Point[] get_points_subset(int n, int mode)
  {
    int base_length;
    int last = 0;
    PointsDataBlock curr_pdb;    

    if (mode == 0) {
      base_length = 1+(total_dense_length-1)/n;
      if ((total_dense_length-1) % n != 0) last = 1;
      curr_pdb = first_d_pdb;
    }
    else {
      base_length = 1+(total_sparse_length-1)/n;
      if ((total_sparse_length-1) % n != 0) last = 1;
      curr_pdb = first_s_pdb;
    }

    Point[] subset = new Point[base_length+last];
    int cnt = 0;
    int i = 0;

    while (cnt < base_length)
    {
      while (i < curr_pdb.count && (cnt+last) < subset.length)
      {
        subset[cnt] = curr_pdb.points[i];
        cnt++;
        i += n;
      }
      i -= curr_pdb.count;
      curr_pdb = curr_pdb.next;
    }
    if (last == 1)
    {
      if (mode == 0)
      {
        subset[cnt] = last_d_pdb.points[last_d_pdb.count-1];
      }
      else
      {
        subset[cnt] = last_s_pdb.points[last_s_pdb.count-1];
      }
    }
    return subset;
  }

  /**
   * Returns the simulation's last point. Mode 0 for last point from the
   * dense array, mode 1 for the last sparse array.
   */
  Point get_last_point(int mode)
  {
    if (mode == 0)
    {
      return last_d_pdb.points[last_d_pdb.count-1];
    }
    else if (mode == 1)
    {
      return last_s_pdb.points[last_s_pdb.count-1];
    }
    return null;
  }

  Point get_first_point()
  {
    return first_s_pdb.points[0];
  }

    public int getPropertyStatus()
    {
        return property_check.get_status();
    }

  /**
   * Checks wheater all the dense points are closer than ode.min_check_dist
   * and all the sparse points are closer than ode.max_check_dist.
   **/
  boolean check_points_arrays_sanity(ODE ode)
  {
    PointsDataBlock curr = first_d_pdb;
    int cnt = 0;
    Point last_point = null;

    if (curr != null && curr.count > 0)
    {
      last_point = curr.points[0];
      cnt++;
      if (curr.count <= cnt)
      {
        if (curr.next != null &&
            curr.next.first_point_index != curr.first_point_index+curr.count)
        {
          return false;
        }
        curr = curr.next;
        cnt = 0;
      }
    }
    while (curr != null && curr.count > cnt)
    {
      if (!ode.point_dist_comp(last_point, curr.points[cnt], 0, false)) return false;
      cnt++;
      if (curr.count <= cnt)
      {
        if (curr.next != null &&
            curr.next.first_point_index != curr.first_point_index+curr.count)
        {
          return false;
        }
        curr = curr.next;
        cnt = 0;
      }
      if (curr != null) last_point = curr.points[cnt];
    }

    curr = first_s_pdb;
    cnt = 0;
    if (curr != null && curr.count > 0)
    {
      last_point = curr.points[0];
      cnt++;
      if (curr.count <= cnt)
      {
        if (curr.next != null &&
            curr.next.first_point_index != curr.first_point_index+curr.count)
        {
          return false;
        }
        curr = curr.next;
        cnt = 0;
      }
    }
    while (curr != null && curr.count > cnt)
    {
      if (!ode.point_dist_comp(last_point, curr.points[cnt], 1, true)) return false;
      cnt++;
      if (curr.count <= cnt)
      {
        if (curr.next != null &&
            curr.next.first_point_index != curr.first_point_index+curr.count)
        {
          return false;
        }
        curr = curr.next;
        cnt = 0;
      }
      if (curr != null) last_point = curr.points[cnt];
    }
    return true;
  }
}

