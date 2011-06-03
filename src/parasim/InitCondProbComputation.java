/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim;

import parasim.computation.verification.Property;

/**
 *
 * @author sven
 */
public class InitCondProbComputation implements ComputationInterface
{
    private ODE ode;
    private Property property;
    private SimulationStorage sims;

    private boolean merge_detect;
    private int simulations_active;
    private int distance_checks_active;
    private final float equilibrium_tolerance = 0.0000001f; //TODO
    private boolean ode_loaded;
    private float dc_counter;
    private float sim_counter;

    /** True if computation has finished. */
    private boolean finished;
    /** Holds the overall time spent in this computation.
        Is updated by the compute() method. */
    private long totalTime;
    /** Tells how much work to do during one call to the compute() method. */
    private float speed;
    /** Pauses and resumes computation. */
    private boolean paused;

    public InitCondProbComputation()
    {
        ode = new ODE();
        property = new Property();
        sims = new SimulationStorage();
        merge_detect = false;
        simulations_active = 0;
        distance_checks_active = 0;
        ode_loaded = false;
        finished = true;
        totalTime = 0;
        speed = 1.0f;
        sim_counter = 0.0f;
        dc_counter = 0.0f;
        paused = false;
    }

    public float getSpeed()
    {
        return speed;
    }

    public long getTotalTime()
    {
        return totalTime;
    }

    public void setSpeed(float newSpeed)
    {
        speed = newSpeed;
    }

    public ODE getODE()
    {
        return ode;
    }

    /** Pauses the computation. */
    public void pause()
    {
        paused = true;
    }

    public void resume()
    {
        paused = false;        
    }

    public boolean paused()
    {
        return paused;
    }

    public boolean finished()
    {
        return finished;
    }

    /** TODO Should be deleted in future as a Computation should not exist in an unloaded state */
    public boolean isLoaded()
    {
        return ode_loaded;
    }


    public void set_merge_detect(boolean mode)
    {
      merge_detect = mode;
    }

    public SimulationStorage getSimulationStorage()
    {
        return sims;
    }

    /**
     * Loads an ODE system and the default property.
     *
     * @return on success null, on failure error message
     **/
    public String load_ode_file(String ode_filename)
    {
      String err_str;
      boolean flush_sims = false;

      if (ode_filename != null)
      {
        err_str = ode.load_ode_file(ode_filename);
        if (err_str != null)
        {
          return err_str;
        }
        System.out.println("Model loaded:\n");
        System.out.println(ode);
        flush_sims = true;
        ode_loaded = true;
        System.out.println("Loading default property (G true)\n");
        property.set_default_property();
      }
      else
      {
        return "ODE filename empty";
      }

      if (flush_sims)
      {
        sims = new SimulationStorage();
        distance_checks_active = 1;
        sims.insert(ode.generate_initial_points(ode.ic_division), property, merge_detect);
        simulations_active = sims.count;
        totalTime = 0;
        finished = false;
        paused = true;
        sim_counter = 0.0f;
        dc_counter = 0.0f;
      }

      return null;
    }

    /**
     * Loads an LTL property, an ODE has to be loaded beforehand with adequate
     * state names. On success the simulation storage is emptied and initialized
     * for a new computation.
     *
     * @return on success null, on failure error message
     **/
    public String load_property_file(String property_filename)
    {
      String err_str;
      boolean flush_sims = false;

      /* load property only if ode loaded and filename not null */
      if (ode_loaded && property_filename != null)
      {
        err_str = property.load_ltl_file(property_filename, ode.var_indexes, ode);
        if (err_str != null)
        {
          property.set_default_property();
          return "load_property_file: "+err_str+
                 "\nLoading default property (G true)";
        }
        flush_sims = true;
        System.out.println("Property loaded:\n");
        System.out.println(property);
      }
      else if (!ode_loaded)
      {
        return "ODE not loaded cannot load property";
      }
      else if (property_filename == null)
      {
        return "Empty property filename";
      }

      if (flush_sims)
      {
        sims = new SimulationStorage();
        distance_checks_active = 1;
        sims.insert(ode.generate_initial_points(ode.ic_division), property, merge_detect);
        simulations_active = sims.count;
        totalTime = 0;
        finished = false;
        paused = false; //FIXME
        sim_counter = 0.0f;
        dc_counter = 0.0f;
      }
      return null;
    }

    /**
     * TODO
     **/
    public long compute()
    {
        long startTime = System.currentTimeMillis();
        dc_counter += Utils.DC_LOCAL_POINTS_BATCH * speed;
        sim_counter += Utils.SIM_DENSE_POINTS_BATCH * speed;

        int sim_batch = (int)java.lang.Math.floor(sim_counter);
        int dc_batch = (int)java.lang.Math.floor(dc_counter);
      
        int tmp_cnt;
        boolean active_change = false;
        if (simulations_active > 0 || distance_checks_active > 0)
        {
            //println("extend_simulations()");
            if (simulations_active > 0 && sim_batch > 0)
            {
                tmp_cnt = extend_simulations(sims, ode, /*property,*/ sim_batch);
                if (tmp_cnt != simulations_active)
                {
                    simulations_active = tmp_cnt;                    
                    active_change = true;
                }
                //System.out.println("Active simulations = "+tmp_cnt);
            }
            //println("check_simulation_distances()");

            if (distance_checks_active > 0 && dc_batch > 0)
            {
                tmp_cnt = check_simulation_distances(sims, ode, dc_batch);
                //tmp_cnt = 0;
                if (tmp_cnt != distance_checks_active)
                {
                    distance_checks_active = tmp_cnt;
                    active_change = true;
                }
            }
            if (simulations_active == 0 && distance_checks_active == 0 && active_change)
            {
                System.out.println("FINISHED");
                long tempTotalTime = totalTime + (System.currentTimeMillis() - startTime);
                System.out.println("Computation total time: "+(tempTotalTime/1000.0f)+" seconds");
                finished = true;
            }
            else if (active_change)
            {
                /*println("Active Sims: "+simulations_active+
                  " DChecks: "+distance_checks_active);*/
            }
        }
        int del_count = free_processed_dense_pdbs(sims);
        //if (del_count > 0) println("Deleted "+del_count+" PDBs");

        sim_counter -= sim_batch;
        dc_counter -= dc_batch;

        totalTime += System.currentTimeMillis() - startTime;
        return System.currentTimeMillis() - startTime;
    }

    /**
     * FIXME, Returns number of active distance checks.
     **/
    private int check_simulation_distances(SimulationStorage sims, ODE ode, int dc_batch)
    {
      int i,j;
      DistanceCheckDataBlock dc_data;
      int status;
      int active_checks = 0;
      int active_sim_checks;

      boolean dbg = false;
      int sims_count = sims.count;

      for (i = 0; i < sims_count; i++)
      {
        if (sims.simulations[i].status != Utils.SIM_STATUS_FIN)
        {
          active_sim_checks = 0;
          for (j = 0; j<sims.simulations[i].dist_check.length; j++)
          {
            dc_data = sims.simulations[i].dist_check[j];
            if (dc_data.check_status == Utils.DC_STATUS_COMP)
            {
              //print("Sim["+i+"].dist_check["+j+"]("+dc_data.neighbour.sim_index+").check_distance...");
              status = dc_data.check_distance(dc_batch, ode);
              //println("OK");
              //if (false)
              //{
              //  println("Sim["+i+"].dist_check["+j+"].ic_count = "+
              //          dc_data.intervals.length);
              //}

              if (dc_data.check_status != status)
              {

                dc_data.check_status = status;
                if (status == Utils.DC_STATUS_SEP)
                {
                  if (true)
                  {
                    Point local_point = dc_data.local.first_s_pdb.points[0];
                    Point neighbour_point = dc_data.neighbour.first_s_pdb.points[0];

                    Point initial_cond = new Point(local_point.getDims());
                    for (int k = 0; k<local_point.getDims(); k++)
                    {
                      initial_cond.set(k, (local_point.get(k) + neighbour_point.get(k))/2.0f);
                    }
                    SimulationData[] neighbours = new SimulationData[]
                      {dc_data.local, dc_data.neighbour};
                    status = sims.insert(initial_cond, neighbours, property, dc_data.merge_detect);
                    if (status > 0)
                    {
                      simulations_active++;
                      active_sim_checks += 2;
                      //System.out.println("Densification ("+dc_data.local.sim_index+","+
                      //        dc_data.neighbour.sim_index+") => "+status);
                    }
                    else System.out.println("SimulationStorage FULL");
                  }

                  dc_data.check_status = Utils.DC_STATUS_SEP_NEW;
                }

                //System.out.println("Sim["+i+"].dist_check["+j+"]("+
                //        dc_data.neighbour.sim_index+").status = "+
                //        Utils.DC_STATUS_STR[dc_data.check_status]);
              }
              else
              {
                active_sim_checks++;
              }
            }
          }
          if (sims.simulations[i].status == Utils.SIM_STATUS_DC && active_sim_checks == 0)
          {
            sims.simulations[i].status = Utils.SIM_STATUS_FIN;
          }
          active_checks += active_sim_checks;
        }
      }
      return active_checks;
    }

    /**
     * FIXME, Returns number of extended simulations.
     *
     * @param sims storage where to insert new points
     * @param ode the ODE system to simulate
     * @param sim_batch number of points each simulation will be extended
     **/
    private int extend_simulations(SimulationStorage sims, ODE ode, int sim_batch)
    {
      int i,j,k;

      SimulationData simulation;

      Point[] d_points = new Point[sim_batch];
      /* dense points, each two consecutive are closer than min_check_dist */
      Point[] s_points = new Point[sim_batch+1];
      /* sparse points, each two consecutive are closer than max_check_dist
         although there is usualy 100x less sparse points than dense_points,
         it is safer to allocate the full size because of some dubious cases
         of parameter settings */
      int s_count; /* s_point counter */
      int d_count; /* d_point counter */

      int[] extreme_s_indexes = new int[sim_batch+2];
      /* indexes of points from s_points detected as extreme values */
      int[] extreme_d_indexes = new int[sim_batch+2];
      /* indexes of points from d_points detected as extreme values */
      int[][] extreme_dimensions = new int[sim_batch+2][];
      /* the dimensions in which the extreme values were detected for each
         index of extreme_indexes */
      int ex_count; /* extreme_index counter */
      int[] ex_dims; /* temporary results of detect_extremes */

      Point old_d_point, new_d_point, tmp_point;
      Point last_s_point;
      /* It is expected that the old_d_point (the last dense point) is not
         further than max_check_dist on all dimensions including time. */

      float step = 0.001f;
      float step_faktor = 1.2f;
      boolean close;

      PointPosition d_pos, s_pos, cycle_pos;
      StatusMod status = new StatusMod();
      StatusMod err = new StatusMod();
      /* will hold returned values of different detection processes */
      boolean bounded;

      int extended_counter = 0;
      /* counts how many simulations have been extended */

      for (i = 0; i < sims.count; i++)
      {
        if (sims.simulations[i].status != Utils.SIM_STATUS_COMP)
        {
          if (sims.simulations[i].property_check.get_status() == Utils.PC_STATUS_NONE)
          {
            sims.simulations[i].property_check.check_property();
            if (sims.simulations[i].property_check.get_status() == Utils.PC_STATUS_NONE)
            {
              sims.simulations[i].property_check.set_status(Utils.PC_STATUS_DONTKNOW);
            }
          }
          continue;
        }

        simulation = sims.simulations[i];
        old_d_point = sims.get_last_sim_point(i,0);
        last_s_point = sims.get_last_sim_point(i,1);

        ex_count = 0;
        d_count = 0;
        s_count = 0;

        /* compute new dense and sparse points */
        for (j = 0; j<sim_batch; j++)
        {
          new_d_point = ode.simulate(old_d_point, step, true);
          close = ode.point_dist_comp(old_d_point, new_d_point, 0, true);
          if (close)
          { /* new d_point was closer than min_check_dist so we try to increase
               the step size */
            step *= step_faktor;
            tmp_point = ode.simulate(old_d_point, step, false);
            while (ode.point_dist_comp(old_d_point, tmp_point, 0, true))
            {
              new_d_point = tmp_point;
              step *= step_faktor;
              tmp_point = ode.simulate(old_d_point, step, false);
            }
          }
          else
          { /* new d_point was further than min_check_dist so we try to decrease
               the step size */
            step /= step_faktor;
            new_d_point = ode.simulate(old_d_point, step, false);
            while (!ode.point_dist_comp(old_d_point, new_d_point, 0, true))
            {
              step /= step_faktor;
              new_d_point = ode.simulate(old_d_point, step, false);
            }
          }
          /* in step there is such a value that if increased the next d_point
             would be further then min_check_dist away */
          d_points[j] = new_d_point;
          d_count++;

          /* if the new_d_point is too far away or the old_d_point is an
             equilibrium then we add it to the sparse array */
          if ((!last_s_point.equals(old_d_point) &&
               !ode.point_dist_comp(last_s_point, new_d_point, 1, true)) ||
               ode.equilibrium(equilibrium_tolerance) ||
               ode.points_equal(old_d_point, new_d_point, false)
               )
          {
            /* the new d_point is further than max_check_dist, therefore we
               must add the old_d_point as a new_s_point */
            s_points[s_count] = old_d_point;
            s_count++;
            last_s_point = old_d_point;
          }

          if (ode.equilibrium(equilibrium_tolerance) ||
              ode.points_equal(old_d_point, new_d_point, false))
          {
            simulation.status = Utils.SIM_STATUS_DC;
            simulation.end_status = Utils.SIM_END_STATUS_EQ;
            break;
            /* An equilibrium can not (TODO) be an extreme */
          }

          /* If the new_d_point is an extreme value it is forced into the
             s_points array. */
          ex_dims = simulation.cyc_detect.detect_extremes(new_d_point);
          if (ex_dims != null)
          {
            extreme_s_indexes[ex_count] = s_count;
            extreme_d_indexes[ex_count] = j;
            extreme_dimensions[ex_count] = ex_dims;
            ex_count++;
            s_points[s_count] = new_d_point;
            s_count++;
            last_s_point = new_d_point;
          }

          old_d_point = new_d_point;

          /* check if timeout or global minimum, maximum was not reached */
          if (!ode.in_bounds(new_d_point, status))
          {
            simulation.status = Utils.SIM_STATUS_DC;
            if (status.get() == 0)
            {
              /* time limit reached */
              simulation.end_status = Utils.SIM_END_STATUS_TIMEOUT;
            }
            else if (status.get() < 0)
            {
              /* global minimum exceeded on some dimension */
              simulation.end_status = Utils.SIM_END_STATUS_MIN;
            }
            else if (status.get() > 0)
            {
              /* global maximum exceeded on some dimension */
              simulation.end_status = Utils.SIM_END_STATUS_MAX;
            }
            /* no more points are to be computed, skip the rest of the for cycle */
            break;
          }
        }

        /* old positions of ends of the sparse and dense arrays must be
           saved before extending the simulation point arrays */
        s_pos = new PointPosition(
          simulation.last_s_pdb,
          simulation.last_s_pdb.count-1,
          simulation.last_s_pdb.first_point_index+simulation.last_s_pdb.count-1);
        d_pos = new PointPosition(
          simulation.last_d_pdb,
          simulation.last_d_pdb.count-1,
          simulation.last_d_pdb.first_point_index+simulation.last_d_pdb.count-1);
        sims.extend(i, d_points, d_count, s_points, s_count, err);
        extended_counter++;

        if (simulation.status == Utils.SIM_STATUS_COMP ||
            simulation.end_status != Utils.SIM_END_STATUS_EQ)
        { /* if the simulation is still computing or it is not but the
             end status is not an equilibrium then check for cycles */
          /* after inserting the new dense and sparse points the newly computed
             extreme points are compared to the old extremes for cycle detection */
          if (s_count > 0)
          {
            k = 0;
            d_pos = d_pos.get_next_point_pos(simulation, status);
            s_pos = s_pos.get_next_point_pos(simulation, status);
            /* s_pos now holds the position of the first s_point added
               to the sparse array */
            for (j=0; j<ex_count; j++)
            {
              /* go through the newly added extremes and look for cycles */
              /* find the first extreme's position inside the sparse array */
              while (status.get() == Utils.SIM_END_STATUS_COMP && k < extreme_s_indexes[j])
              {
                s_pos = s_pos.get_next_point_pos(simulation, status);
                k++;
              }
              ex_dims = extreme_dimensions[j];
              cycle_pos = simulation.cyc_detect.detect_cycle(s_pos, ex_dims, ode);
              if (cycle_pos != null)
              {
                /* a cycle was detected, save the begining of the cycle */
                simulation.cycle_s_pdb = cycle_pos.pdb;
                simulation.cycle_offset = cycle_pos.offset;

                /* trim the sparse array */
                s_pos.pdb.count = s_pos.offset+1;
                s_pos.pdb.next = null;
                simulation.last_s_pdb = s_pos.pdb;
                simulation.total_sparse_length = s_pos.pdb.first_point_index + s_pos.pdb.count;

                //println("Sim["+i+"].cycle_length = "+(
                //simulation.last_s_pdb.first_point_index+simulation.last_s_pdb.count
                //- simulation.cycle_s_pdb.first_point_index - simulation.cycle_offset));

                /* find the extreme's position in the dense array */
                k = 0;
                while (status.get() == Utils.SIM_END_STATUS_COMP && k < extreme_d_indexes[j])
                {
                  d_pos = d_pos.get_next_point_pos(simulation, status);
                  k++;
                }

                /* trim the dense array */
                d_pos.pdb.count = d_pos.offset+1;
                d_pos.pdb.next = null;

                /* since the dense array might have been shortened from the begining
                   the new total length must be calculated from the current value
                   and the difference in the old and new counts */
                simulation.total_dense_length -=
                  simulation.last_d_pdb.first_point_index - d_pos.pdb.first_point_index +
                  simulation.last_d_pdb.count - d_pos.pdb.count;
                // simulation.total_dense_length = d_pos.pdb.first_point_index + d_pos.pdb.count;
                simulation.last_d_pdb = d_pos.pdb;

                if (java.lang.Math.abs((s_pos.pdb.first_point_index+s_pos.offset) -
                        (cycle_pos.pdb.first_point_index+cycle_pos.offset))<4)
                {
                  /* if the found cycle is shorter then 4 sparse points it
                     is most likely an equilibrium */
                  // TODO check mutual distance of points on pseudocycle
                  simulation.cycle_s_pdb = null;
                  simulation.cycle_offset = -1;
                  simulation.status = Utils.SIM_STATUS_DC;
                  simulation.end_status = Utils.SIM_END_STATUS_EQ;
                }
                else
                {
                  /* change the simulation's status */
                  simulation.status = Utils.SIM_STATUS_DC;
                  simulation.end_status = Utils.SIM_END_STATUS_CYC;
                }

                /* no more points are to be computed */
                break;
              }
            }
          }
        } /* cycle detection */

        if (simulation.status != Utils.SIM_STATUS_COMP)
        {
          System.out.print("Sim["+i+"]: end_status = "+Utils.SIM_END_STATUS_STR[simulation.end_status]);
          System.out.print(", Points = (D:"+simulation.total_dense_length+
                ", S:"+simulation.total_sparse_length+")");
          System.out.println(", Sanity "+(simulation.check_points_arrays_sanity(ode)?"OK":"Error"));
        }
      }
      return extended_counter;
    }

    /**
     * Iterates over all simulations and if distance_checking has already processed
     * the first dense pdb's and has moved on to the next ones, then all the
     * processed pdbs from the start are deleted.
     * At least one pdb is left so future computation can take place.
     **/
    private int free_processed_dense_pdbs(SimulationStorage sims)
    {
      int smallest_needed_pdb_index;
      SimulationData sim;
      int del_count = 0;
      for (int i=0; i<sims.count; i++)
      {
        sim = sims.simulations[i];
        smallest_needed_pdb_index = sim.last_d_pdb.first_point_index;
        if (sim.dist_check.length > 0 &&
            sim.dist_check[0].merge_detect)
            /* all dist_check_dbs are expected to run in same mode */
        {
          for (int j=0; j<sim.dist_check.length; j++)
          {
            smallest_needed_pdb_index =
              Utils.min(smallest_needed_pdb_index, sim.dist_check[j].local_pos.pdb.first_point_index);
          }
        }
        while (sim.first_d_pdb.first_point_index < smallest_needed_pdb_index)
        {
          sim.total_dense_length -= sim.first_d_pdb.count;
          sim.first_d_pdb = sim.first_d_pdb.delete();
          del_count++;
        }
      }
      return del_count;
    }
}
