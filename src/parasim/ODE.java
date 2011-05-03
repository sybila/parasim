/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim;

import java.util.HashMap;
import processing.core.PApplet;

/**
 *
 * @author sven
 */
public class ODE {
  int dims;               /* number of model dimensions + 1 (time is dim 0) */
  private float[] dx;     /* temporary variable for dx computation */
  float[] ic_min;         /* lower boundary of initial conditions */
  float[] ic_max;         /* upper boundary of initial conditions */
  int[] ic_division;      /* the initial condition division on each dimension
                             this specifies the starting points of all
                             simulations */
  float[] gl_min;         /* global minimum, gl_min[0] is unused since
                             time only increases */
  float[] gl_max;         /* global maximum
                             upper bound on time of simulation is gl_max[0] */
  String model;           /* model name */
  float[] max_check_dist; /* Maximal dimension-vise distance of two simulations
                             if distance is greater densification takes place.
                             Implicit value is double the distance between the
                             points in the initial conditions interval. */
  float[] min_check_dist; /* Minimal dimension-vise distance of two simulations
                             if distance is smaller merging takes place.
                             Implicit value is 1/2000 of max_check_dist. */
  String[] var_names;     /* Names of variables, var_names[0] = "Time" */
  HashMap var_indexes;    /* var_name => index map */
  TermProduct[][] equations;
                          /* For each variable the equation is the sum of
                             products of which the first is alaways a constant
                             and the rest may be other variables.
                             Since time (dim 0) has no equation, the first
                             equation at index 0 corresponds to the first
                             spatial variable var_name[1].
                             If each variable appears at most once the system
                             is multiaffine. */

  /**
   * Constructor, before any method call the Ode must be loaded by
   * load_ode_file().
   **/
  ODE()
  {
    dims = 0;
    dx = null;
    ic_min = null;
    ic_max = null;
    ic_division = null;
    gl_min = null;
    gl_max = null;
    model = null;
    min_check_dist = null;
    max_check_dist = null;
    var_names = null;
    var_indexes = null;
    equations = null;
  }

  /**
   * Tries to load the ODE from file, on success all class fields are
   * initialized and a null is returned. On failure an error string is returned
   * however the ode itself is unmodified.
   **/
  public String load_ode_file(String filename)
  {
    ODEParser parser = new ODEParser();

    String err_str = parser.parse_ode_file(filename);
    if (err_str != null)
    {
      return "ERROR: parse_ode_file(): "+err_str;
    }
    else
    {
      dims = parser.get_dims();
      dx = new float[dims];
      dx[0] = 1.0f;
      for (int i=1; i<dims; i++)
      {
        dx[i] = 0.0f;
      }

      ic_min = parser.get_ic_min();
      ic_max = parser.get_ic_max();
      ic_division = parser.get_ic_division();
      gl_min = parser.get_gl_min();
      gl_max = parser.get_gl_max();
      min_check_dist = parser.get_min_check_dist();
      max_check_dist = parser.get_max_check_dist();

      model = filename;
      var_names = parser.get_var_names();
      var_indexes = new HashMap(parser.get_var_indexes());
      equations = parser.get_equations();

      /*
      print("ic_min = ");
      print_array(ic_min);
      print("ic_max = ");
      print_array(ic_max);
      print("gl_min = ");
      print_array(gl_min);
      print("gl_max = ");
      print_array(gl_max);
      print("ic_division = ");
      print_array(ic_division);
      print("min_check_dist = ");
      print_array(min_check_dist);
      print("max_check_dist = ");
      print_array(max_check_dist);
      print("var_names = ");
      println(join(var_names," "));
      */

      return null;
    }
  }

  /**
   * Evaluates the differential equations in point x and stores them in dx.
   * If multiple threads work with the same ODE instance it is NOT SAFE.
   **/
  public void compute_derivatives(Point x)
  {
    int i,j;
    for (i=0; i<equations.length; i++)
    {
      dx[i+1] = 0.0f;
      for (j=0; j<equations[i].length; j++)
      {
        dx[i+1] += equations[i][j].eval(x);
      }
    }
  }

  public float get_derivative(int dim)
  {
    return dx[dim]; /* dim is expected to be a valid index, no assertions */
  }

  /* Calculates X + dX*dt */
  Point simulate(Point x, float dt, boolean recompute_derivatives)
  {
    if (recompute_derivatives)
    {
      compute_derivatives(x);
    }
    Point rvect = new Point(dims);
    for (int i = 0; i < dims; i++)
    {
      rvect.c[i] = x.c[i] + dt*dx[i];
    }
    return rvect;
  }

  boolean equilibrium(Point x, float tolerance)
  {
    compute_derivatives(x);
    return equilibrium(tolerance);
  }

  boolean equilibrium(float tolerance)
  {
    for (int i=1; i<dims; i++)
    {
      if (Math.abs(dx[i]) > tolerance) return false;
    }
    return true;
  }

  /**
   * This method simulates the trajectory over time interval dt with relative
   * error rerror for all dimensions.
   * FIXME, should be a better integration method such as RKF45.
   **/
  Point[] simulate(Point x, float dt, float rerror)
  {
    // FIXME
    return null;
  }

  /**
   * Divides the initial interval into n points on each dimension
   * (n >= 1) and returns the grid points as an array.
   *
   * To divide each dimension i into dimensions[i] points a number of for cycles
   * would be needed, this is simulated by an array of for-cycle control
   * variables (fcv) which are incremented and decremented as needed.
   */
  InitialPointSet generate_initial_points(int[] dimensions)
  {
    if (dims < 2)
    {
      return null;
    }
    int i;
    int cnt = 0;                        /* number of points already processed */
    int point_count = 1;
    for (i = 0; i < dims-1; i++)
    {
      point_count *= dimensions[i];     /* computing number of points in grid */
    }
    int[] subgrid = new int[dims-1];
    subgrid[dims-2] = 1;
    if (dims > 2)
    {
      for (i = dims-3; i>=0; i--)
      {
        subgrid[i] = dimensions[i+1] * subgrid[i+1];
                                /* precomputing numbers of points in subgrids */
      }
    }
    InitialPointSet point_set = new InitialPointSet(point_count);

    int[] fcv = new int[dims-1];
    for (i = 0; i < dims-1; i++) fcv[i] = 0;

    for (i = 1; i < dims; i++)
    {
      if (dimensions[i-1]>1)
      {
        dx[i] = (ic_max[i] - ic_min[i])/(dimensions[i-1]-1.0f);
      }
      else
      {
        dx[i] = 0.0f;
      }
    }
    dx[0] = 1.0f;

    int index = dims-2;

    while (index >= 0)
    {
      if (index == dims-2)
      {
        /* adding a point */
        for (fcv[index] = 0; fcv[index] < dimensions[index]; fcv[index]++)
        {
          Point p = new Point(dims);
          p.c[0] = ic_min[0];
          for (i = 1; i < dims; i++)
          {
            p.c[i] = ic_min[i]+dx[i]*fcv[i-1];
          }
          point_set.set_point(cnt, p);
          point_set.set_point_neighbours(cnt, fcv, subgrid);
          cnt++;
        }
        index--;
        fcv[index]++;
      }
      else if (fcv[index] == dimensions[index])
      {
        fcv[index] = 0;
        index--;                                              /* backtracking */
        if (index >= 0) fcv[index]++;
      }
      else
      {
        index++;                                              /* recursing up */
      }
    }
    return point_set;
  }

  void set_max_check_dist(float[] dist)
  {
    if (dist.length == max_check_dist.length)
    {
      PApplet.arrayCopy(dist, max_check_dist);
    }
  }

  void set_min_check_dist(float[] dist)
  {
    if (dist.length == min_check_dist.length)
    {
      PApplet.arrayCopy(dist, min_check_dist);
    }
  }

  /**
   * Sets the implicit maximum checking distances as the double of initial
   * points distance and 1/2000 of the distance as the minimum.
   * The implicit maximal time step between two consecutive sparse points is
   * set to max_time_step.
   **/
  void set_implicit_check_dist(int[] division, float max_time_step)
  {
    for (int i = 1; i < dims; i++)
    {
      if (division[i-1]>2) {
        max_check_dist[i] = 2.0f*Math.abs(ic_max[i] - ic_min[i])/(division[i-1]-1.0f);
        min_check_dist[i] = max_check_dist[i]/2000;
      }
      else
      {
        max_check_dist[i] = 2.0f*Math.abs(ic_max[i] - ic_min[i]);
        min_check_dist[i] = max_check_dist[i]/2000;
      }
    }
    max_check_dist[0] = max_time_step;
  }

  void set_global_minimum(float[] gl_min)
  {
    if (gl_min.length == this.gl_min.length)
    {
      PApplet.arrayCopy(gl_min, this.gl_min);
    }
  }

  void set_global_maximum(float[] gl_max)
  {
    if (gl_max.length == this.gl_max.length)
    {
      PApplet.arrayCopy(gl_max, this.gl_max);
    }
  }

  void set_initial_conditions(float[] ic_min, float[] ic_max)
  {
    if (ic_min.length == this.ic_min.length &&
        ic_max.length == this.ic_max.length )
    {
      PApplet.arrayCopy(ic_min, this.ic_min);
      PApplet.arrayCopy(ic_max, this.ic_max);
    }
  }

  /**
   * Computes the dimensionwise distances between two points A and B and compares
   * them either to the min_check_dist (min_max 0) or max_check_dist (min_max 1).
   * If |a-b|[i] < dist[i] for 0+(time) <= i < dims true returned, false othervise.
   *
   * If time == true the dimensions are checked from 0 (being the time) else
   * from 1 (the first spatial dimension).
   *
   **/
  boolean point_dist_comp(Point a, Point b, int min_max, boolean time)
  {
    int start = 0;
    if (!time) start = 1;

    if (min_max == 0)
    {
      for (int i=start; i<min_check_dist.length; i++)
      {
        if (Math.abs(a.c[i]-b.c[i]) > min_check_dist[i]) return false;
      }
    }
    else if (min_max == 1)
    {
      for (int i=start; i<max_check_dist.length; i++)
      {
        if (Math.abs(a.c[i]-b.c[i]) > max_check_dist[i]) return false;
      }
    }
    return true;
  }

  /**
   * Computes the dimensionwise distances between two points A and B and compares
   * them to dist array. If dist has a different length than the number of
   * dimensions of A, the vectors are aligned from the back.
   * If |a-b|[] < dist[] on all compared dimensions true is returned.
   **/
  boolean point_dist_comp(Point a, Point b, float[] dist)
  {
    int dim = a.c.length-dist.length;
    for (int i=0; i<dist.length; i++)
    {
      if (Math.abs(a.c[dim]-b.c[dim]) > dist[i]) return false;
      dim++;
    }
    return true;
  }

  /**
   * Compares the two points on all spatial dimensions (if time == true also
   * on the time dimension) and returns true if they are the same.
   * Points are expected to have same length.
   **/
  boolean points_equal(Point a, Point b, boolean time)
  {
    for (int i=(time?0:1); i<a.c.length; i++)
    {
      if (a.c[i] != b.c[i]) return false;
    }
    return true;
  }

  /**
   * Checks if all dimensions gl_min[i] <= a[i] <= gl_max[i].
   * Returns true if this holds for all i, else sets the status to the
   * dimension index that broke then bound (-index if lower +index if upper)
   * and returns false.
   **/
  boolean in_bounds(Point a, StatusMod status)
  {
    for (int i=0; i<gl_max.length; i++)
    {
      if (a.c[i] > gl_max[i])
      {
        status.set(i);
        return false;
      }
      else if (a.c[i] < gl_min[i])
      {
        status.set(-i);
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString()
  {
    String s = "Model name: "+model+"\n";
    s += "Variables: "+PApplet.join(var_names,", ")+";\n";
    s += "Equations: \n";
    for (int i=0; i<equations.length; i++)
    {
      s += "d"+var_names[i+1]+" = "+equations[i][0].toString();
      for (int j=1; j<equations[i].length; j++)
      {
        s += " + "+equations[i][j].toString();
      }
      s += ";\n";
    }
    s += "Global bounds:\n";
    for (int i=1; i<dims; i++)
    {
      s += gl_min[i]+" <= "+var_names[i]+" <= "+gl_max[i]+";\n";
    }
    s += "Initial conditions:\n";
    for (int i=1; i<dims; i++)
    {
      s += ic_min[i]+" <= "+var_names[i]+" <= "+ic_max[i]+
           " (division "+ic_division[i-1]+");\n";
    }

    s += "Minimal checking distance:\n";
    for (int i=1; i<dims; i++)
    {
      s += var_names[i]+": "+min_check_dist[i]+"\n";
    }
    s += "Maximal checking distance:\n";
    for (int i=1; i<dims; i++)
    {
      s += var_names[i]+": "+max_check_dist[i]+"\n";
    }
    s += "Maximal time step: "+max_check_dist[0]+"\n";
    s += "Time limit: "+gl_max[0]+"\n";
    return s;
  }
}
