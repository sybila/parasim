/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim;

/**
 * Class to hold data for neighbour distance checking and methods
 * to carry it out.
 * @author sven
 */
public class DistanceCheckDataBlock
{
  SimulationData neighbour;        /* Neighbour (presumed to be nonlocal in
                                      MPI) */
  SimulationData local;            /* Local simulation */
  boolean merge_detect;            /* If merge_detect == true, merging is also
                                      to be detected in this case the local_pos
                                      iterates through the dense PDBs (X=d),
                                      if mode == false only separation is
                                      detected in which case the local_pos
                                      interates through the sparse PDBs (X=s).*/

  PointPosition local_pos;         /* The position of the point in the local
                                      simulation being checked. */
  Interval[] intervals;            /* Intervals of neighbour points closer than
                                      ode.max_check_dist to the current local
                                      point specified by local_pos.
                                    */

  int check_status;
  /* Status of distance checking process, one of CHECK_STATUS_* constants */
  /* FUTU separation detection data */

  int local_cycle_counter;
  int intervals_cycle_counter;

  //boolean local_on_cycle;
  /* True if the local simulation has a cycle and the local_pos has ever reached
     the turn around point (local.last_X_pdb's last point). */
  int local_cycle_length;
  /* If the local simulation has a cycle, then it's length (in the X PDBs)
     will be stored here once the cycle is interated through for the second
     time. */
  boolean intervals_on_cycle;
  /* True if neighbour has cycle and if any intervals have ever reached to span
     the cycle's turn around point (last point in neighbour.last_s_pdb). */
  int first_interval_left_border_index;
  /* If (intervals_on_cycle == true) then the interval[0].first.index is
     kept here to detect stability of the first interval. */
  int first_interval_stability_counter;
  /* If (local_cycle_counter > 0 && intervals_on_cycle == true) this variable
     is increased each time a new local point is processed but the
     first_interval_left_border_index remains to stay the same.

     If this counter reaches the length of the local simulation's cycle
     (local_cycle_length) this indicates that the whole local simulation's
     cycle is close enough to a fixed interval on the neighbour simulation
     and DC_STATUS_CYCLE_LOCAL is returned.
     */

  int ctap_index;
  /* If the local simulation has a cycle then each time the local_pos reaches
     the turn around point (last_s_pdb -> cycle_s_pdb) the local_cycle_counter
     is increased.

     Similarly for the neighbour simulation, when an interval overlaps the
     the cycle turn around point (last_s_pdb -> cycle_s_pdb) this is signaled by
     setting the ctap_index to current position's index. Then when
     the first point of the first interval gets behind this position
     the neighbour_cycle_counter is increased and ctap_index is set to -1 again.

     When both counters are greater then 1 this indicates that all the points
     on both cycles have a close enough point of the other cycle and distance
     checking ends with a DC_STATUS_CYCLE_BOTH result.
  */

  /**
   * Constructor
   *
   @param merge_detect if true merge detection will take place during distance
          checking
   **/
  DistanceCheckDataBlock(SimulationData neighbour, SimulationData local,
                         boolean merge_detect)
  {
    this.neighbour = neighbour;
    this.local = local;
    this.merge_detect = merge_detect;

    local_cycle_counter = 0;
    local_cycle_length = 0;
    intervals_cycle_counter = 0;
    ctap_index = -1;
    intervals_on_cycle = false;
    first_interval_stability_counter = 0;
    first_interval_left_border_index = -1;

    if (merge_detect == true)
    {
      local_pos = new PointPosition(local.first_d_pdb, 0,
                                    local.first_d_pdb.first_point_index);
    }
    else
    {
      local_pos = new PointPosition(local.first_s_pdb, 0,
                                    local.first_s_pdb.first_point_index);
    }

    check_status = Utils.DC_STATUS_COMP;

    intervals = new Interval[1];
    intervals[0] = new Interval(
      new PointPosition(neighbour.first_s_pdb, 0, neighbour.first_s_pdb.first_point_index),
      new PointPosition(neighbour.first_s_pdb, 0, neighbour.first_s_pdb.first_point_index));
  }

  /**
   *
   *
   **/
  int check_distance(int points_count, ODE ode)
  {
    Point l_point; /* local point */
    Point n_point; /* neighbour point */

    Interval[] new_intervals = new Interval[intervals.length * 2];
    int ni_count = 0; // number of new inserted intervals in new_intervals

    PointPosition new_local_pos;
    PointPosition curr_pos = null;
    PointPosition last_pos = null;
    PointPosition tmp_pos = null;
    PointPosition old_curr_pos = null;

    StatusMod status = new StatusMod();

    for (int point_counter = 0; point_counter<points_count; point_counter++)
    {
      new_local_pos = local_pos.get_next_point_pos(local, status);

      if (new_local_pos.pdb == null)
      {
        if (status.get() == Utils.SIM_END_STATUS_COMP)
        {
          return Utils.DC_STATUS_COMP;
        }
        else
        {
          return Utils.DC_STATUS_END_LOCAL;
        }
      }
      else
      {
        if (status.get() == Utils.SIM_END_STATUS_CYC)
        {
          local_cycle_counter++;
          if (local_cycle_counter > 1 && intervals_cycle_counter > 1)
          {
            return Utils.DC_STATUS_CYCLE_BOTH;
          }
        }
        if (local_cycle_counter == 1)
        {
          local_cycle_length++;
        }
        l_point = new_local_pos.pdb.points[new_local_pos.offset];
      }

      for (int i = 0; i<intervals.length; i++)
      {
        if (i == 0)
        {
          curr_pos = intervals[0].first;
          if (local_cycle_counter > 0)
          {
            /* If the first interval does not change for a long time
               computation got stuck in local cycle. */
            if (first_interval_left_border_index != curr_pos.index)
            {
              first_interval_left_border_index = curr_pos.index;
              first_interval_stability_counter = 0;
            }
            else
            {
              first_interval_stability_counter++;
              if (first_interval_stability_counter > local_cycle_length)
              {
                return Utils.DC_STATUS_CYCLE_LOCAL;
              }
            }
          }

          if (ctap_index != -1 && curr_pos.index >= ctap_index)
          {
            ctap_index = -1;
            intervals_cycle_counter++;
            if (local_cycle_counter > 1 && intervals_cycle_counter > 1)
            {
              return Utils.DC_STATUS_CYCLE_BOTH;
            }
          }
        }

        if (i > 0 && last_pos.index >= intervals[i].last.index )
        {
          /* If the last checked position is after the whole old interval
             skip the rest of the for block and continue with next old interval
          */
          continue;
        }

        if (i > 0 && last_pos.index < intervals[i].first.index)
               /* && last_pos.index < intervals[i].last.index */
        {
          curr_pos = intervals[i].first;
        }

        old_curr_pos = curr_pos;
        while (true)
        {
          /* go through all the points of the old interval and check if
             they are close enough, if yes then expand the interval
             with next points as long as the points are
             close enough to l_point, stop only when:
             1) the point is too far and the end of the old interval has
                been reached
             2) the end of neighbour's points has been reached
             3)  */
          n_point = curr_pos.pdb.points[curr_pos.offset];
          if (ode.point_dist_comp(l_point, n_point, 1, false))
          {

            if (merge_detect) {
              // FIXME merge detection
            }
            if (ni_count > 0)
            {
              /* if a new interval already exists, then get the position of
                 the point next to his right (last) boundary and check if it is
                 equal to curr_pos */
              tmp_pos = new_intervals[ni_count-1].last.get_next_point_pos(neighbour, status);
            }
            if (ni_count > 0 && tmp_pos.index == curr_pos.index)
            {
              /* if it equal then expand the interval */
              new_intervals[ni_count-1].extend(tmp_pos);
            }
            else
            {
              /* if not equal then introduce a new interval */
              if (ni_count == new_intervals.length)
              {
                new_intervals = (Interval[]) Utils.expand(new_intervals);
              }
              new_intervals[ni_count] = new Interval(
                new PointPosition(curr_pos.pdb, curr_pos.offset, curr_pos.index),
                new PointPosition(curr_pos.pdb, curr_pos.offset, curr_pos.index));
              ni_count++;
            }
          }
          else
          {
            if (curr_pos.index >= intervals[i].last.index)
            {
              break;
            }
          }

          curr_pos = curr_pos.get_next_point_pos(neighbour, status);

          if (curr_pos.pdb == null)
          {
            if (status.get() == Utils.SIM_END_STATUS_COMP)
            {
              return Utils.DC_STATUS_COMP;
            }
            else
            {
              return Utils.DC_STATUS_END_NEIGHBOUR;
            }
          }
          else
          {
            /*
            if (local.sim_index == 26 && neighbour.sim_index == 25 && point_counter == 0)
            {
              //println("Sim["+local.sim_index+"].curr_pos.index = "+curr_pos.index+" "+local_cycle_counter+" "+intervals_cycle_counter);
              if (status.val == SIM_END_STATUS_CYC)
              println("ON Cycle");
            } */

            if (status.get() == Utils.SIM_END_STATUS_CYC)
            {
              if (!intervals_on_cycle)
              {
                intervals_on_cycle = true;
              }
              if (ctap_index == -1)
              {
                ctap_index = curr_pos.index;
              }
            }
            if (old_curr_pos.equals(curr_pos))
            {
              //println("curr_pos == old_curr_pos");
              //println("Sim["+local.sim_index+"].curr_pos.index = "+curr_pos.index+" "+local_cycle_counter+" "+intervals_cycle_counter);
              //println(intervals.length);
              //println(intervals[0].first.index);
              /* if (curr_pos == old_curr_pos) then a cycle has been reached and
                 the interval does not have to be expanded any more */
              break;
            }
            else if (status.get() == Utils.SIM_END_STATUS_CYC)
            {
              /* in case there is a cycle on the neighbour simulation that is
                 closer than max_check_dist on all it's points and the
                 old_curr_pos is before the cycle's start the old_curr_pos is
                 moved to the begining of the cycle to prevent indefinite
                 extension of the current interval  */
              old_curr_pos = curr_pos;
            }
          }
        } /* while (true) end */

        last_pos = curr_pos;
      } /* for i<intervals.length end */

      intervals = (Interval[]) Utils.expand(new_intervals, ni_count);
      local_pos = new_local_pos;

      if (ni_count == 0)
      {
        return Utils.DC_STATUS_SEP;
      }

      ni_count = 0;
    } /* for point_counter < points_count end */
    return Utils.DC_STATUS_COMP;
  }
}

