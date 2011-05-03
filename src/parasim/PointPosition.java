/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim;

/**
 *
 * @author sven
 */
public class PointPosition
{
  PointsDataBlock pdb;                /* PDB point is in */
  int offset;                         /* offset of point in PDB */
  int index;
  /* Index of the point in the simulation, for points not on a cycle this is
     the same as pdb.first_point_index + offset, for points on a cycle once
     the cycle's turning point is reached the index still keeps on increasing,
     so if (index > pdb.first_point_index + offset) then point is on cycle.

     FIXME TODO when merging ??? the indexes of all points in the n_intervals
     array are decreased to hold the relationship to the neighbours
     cycle_pdb.first_point_index + cycle_offset.
   */

  PointPosition(PointsDataBlock pdb, int offset, int index)
  {
    this.pdb = pdb;
    this.offset = offset;
    this.index = index;
  }

  /**
   * Given the current position of a point in a PDB of a known simulation sim,
   * will return the next point's position and also the position's status.
   *
   * TODO parameters
   *
   **/
  PointPosition get_next_point_pos(SimulationData sim, StatusMod status)
  {
    if (pdb.count > offset+1)
    {
      status.set(Utils.SIM_END_STATUS_COMP);
      return new PointPosition(pdb, offset+1, index+1);
    }
    else if (pdb.count == offset+1 && pdb.next != null && pdb.next.count > 0)
    {
      status.set(Utils.SIM_END_STATUS_COMP);
      return new PointPosition(pdb.next, 0, index+1);
    }
    else if (pdb.count == offset+1 && pdb.next == null &&
             sim.cycle_s_pdb != null)
    {
      status.set(Utils.SIM_END_STATUS_CYC);
      return new PointPosition(sim.cycle_s_pdb, sim.cycle_offset, index+1);
    }
    else /* pdb.count == offset+1 && pdb.next == null &&
            sim.cycle_s_pdb == null */
    {
      status.set(sim.end_status);
      return new PointPosition(null, -1, -1);
      //FIXME merging ???
    }
  }

  /**
   * Returns true if the PDB and offset are equal.
   **/
  boolean equals(PointPosition pos)
  {
    return (pos.pdb == pdb && pos.offset == offset);
  }
}

