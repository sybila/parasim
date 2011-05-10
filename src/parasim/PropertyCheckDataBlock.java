/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim;

import parasim.verification.Property;
import parasim.verification.Transition;
import parasim.verification.GuardValidityBoundary;

/**
 *
 * @author sven
 */
public class PropertyCheckDataBlock
{
  private int status;
  private SimulationData sim;
  private Property prop;
  //int[] curr_states;

  PropertyCheckDataBlock(SimulationData sim, Property prop)
  {
    this.sim = sim;
    status = Utils.PC_STATUS_NONE;
    this.prop = prop;
  }

  boolean accept_on_stability(Point p, int[] states)
  {
    int[] old_states = new int[states.length];
    int[] new_states = null;
    Utils.arrayCopy(states, old_states);
    old_states = Utils.sort(old_states);
    new_states = prop.get_successors(old_states, p, false);
    boolean same;

    int max_counter = 1 << (prop.get_state_count()+1);
    while (max_counter > 0)
    {
      new_states = prop.get_successors(old_states, p, true);
      if (new_states.length == 0)
      {
        return false;
      }
      new_states = Utils.sort(new_states);
      if (new_states.length == old_states.length)
      {
        same = true;
        for(int i=0; i<new_states.length; i++)
        {
          if (new_states[i] != old_states[i])
          {
            same = false;
            break;
          }
        }
        if (same)
        {
          /* The set of states has stabilized */
          new_states = prop.intersect_accepting_states(new_states);
          if (new_states.length > 0)
          {
            return true;
          }
          else
          {
            return false;
          }
        }
      }
      old_states = new_states;
      max_counter--;
    }
    /* The property automaton must have reached a cycle by now. */
    old_states = new int[states.length];
    max_counter = 1 << (prop.get_state_count()+1);
    while (max_counter > 0)
    {
      new_states = prop.get_successors(old_states, p, true);
      if (new_states.length == 0)
      {
        return false;
      }

      int[] intersection = prop.intersect_accepting_states(new_states);
      if (intersection.length > 0)
      {
        /* In there is an accepting state on the cycle the whole cycle is
           accepting. */
        return true;
      }

      old_states = new_states;
      max_counter--;
    }
    /* If no accepting states have been found on the cycle it does not accept.*/
    return false;
  }

  void check_property()
  {
    int[] curr_states = new int[prop.get_initial_states().length];
    Utils.arrayCopy(prop.get_initial_states(), curr_states);
    int[] new_states;

    boolean stop = false;
    PointPosition pos = new PointPosition(sim.first_s_pdb, 0, 0);
    Point curr_point;
    StatusMod pos_stat = new StatusMod();
    Transition[] new_trans;
    Transition[] old_trans = new Transition[0];

    GuardValidityBoundary[] check_points = new GuardValidityBoundary[16];
    int cp_count = 0;

    int cycle_counter = -1;
    boolean on_cycle = false;
    int[] intersection;
    int cycle_guard_boundry_index = -1;
    int[] cycle_start_states = null;
    boolean cycle_contains_accepting_states = false;

    while (!stop)
    {
      curr_point = pos.pdb.points[pos.offset];
      new_states = prop.get_successors(curr_states, curr_point, false);
      if (new_states.length == 0)
      {
        status = Utils.PC_STATUS_INVALID;
        return;
      }

      if (on_cycle && !cycle_contains_accepting_states)
      {
        int[] tmp = prop.intersect_accepting_states(new_states);
        if (tmp.length > 0)
        {
          cycle_contains_accepting_states = true;
        }
      }

      new_trans = prop.get_enabled_transitions(curr_point, true);
      if (new_trans.length == 0)
      {
        // Stop false error
      }

      if (cp_count == 0 ||
          !check_points[cp_count-1].transitions_equal(new_trans) ||
          (on_cycle && cycle_guard_boundry_index == -1)
          )
      {
        if (cp_count == check_points.length)
        {
          check_points = (GuardValidityBoundary[]) Utils.expand(check_points, cp_count*2);
        }
        if (on_cycle && cycle_guard_boundry_index == -1)
        {
          cycle_guard_boundry_index = cp_count;
        }
        check_points[cp_count] =
          new GuardValidityBoundary(pos.pdb, pos.offset, new_trans);
        cp_count++;
      }

      pos = pos.get_next_point_pos(sim, pos_stat);
      //"COMP","EQILIBRIUM","CYCLE","MERGE","TIMEOUT","LTL","MIN","MAX"
      switch (pos_stat.get())
      {
        case Utils.SIM_END_STATUS_COMP:
          break;
        case Utils.SIM_END_STATUS_EQ:
          //intersection = prop.intersect_accepting_states(new_states);
          //if (intersection.length > 0)
          if (accept_on_stability(curr_point, new_states))
          {
            status = Utils.PC_STATUS_VALID;
          }
          else
          {
            status = Utils.PC_STATUS_INVALID;
          }
          return;
        case Utils.SIM_END_STATUS_CYC:
          if (on_cycle)
          {
            new_states = Utils.sort(new_states);
            if (new_states.length != cycle_start_states.length)
            {
              status = Utils.PC_STATUS_DONTKNOW;
              return;
            }
            for (int i=0; i<cycle_start_states.length; i++)
            {
              if (new_states[i] != cycle_start_states[i])
              {
                status = Utils.PC_STATUS_DONTKNOW;
                return;
              }
            }
            if (cycle_contains_accepting_states)
            {
              status = Utils.PC_STATUS_VALID;
              return;
            }
            else
            {
              status = Utils.PC_STATUS_DONTKNOW;
              return;
            }

            // FIXME stabilizace
            /*cycle_states = prop.intersect_accepting_states(cycle_states);
            if (cycle_states.length > 0)
            {
              status = PC_STATUS_VALID;
              return;
            }*/
          }
          else
          {
            on_cycle = true;
            cycle_start_states = Utils.sort(new_states);
          }
          break;
        case Utils.SIM_END_STATUS_MERGE:
          status = Utils.PC_STATUS_DONTKNOW;
          return;
          //exit();
          //break;
        case Utils.SIM_END_STATUS_TIMEOUT:
        case Utils.SIM_END_STATUS_MIN:
        case Utils.SIM_END_STATUS_MAX:
          status = Utils.PC_STATUS_DONTKNOW;
          intersection = prop.intersect_accepting_states(new_states);
          if (prop.tautology_guards(intersection))
          {
            status = Utils.PC_STATUS_VALID;
          }
          return;
          //break;
      }
      curr_states = new_states;
      old_trans = new_trans;
    }
  }

  public int get_status()
  {
    return status;
  }

  public void set_status(int s)
  {
    status = s;
  }

}
