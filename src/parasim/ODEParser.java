/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim;

import processing.core.PApplet;
import java.util.HashMap;
import java.lang.Math;

/**
 * Reads and parses ODE fiels in the Numeric Simulation Verifier format.
 * @author sven
 */
public class ODEParser
{
  int dims;               /* number of model dimensions + 1 (time is dim 0) */
  float[] ic_min;         /* lower boundary of initial conditions */
  float[] ic_max;         /* upper boundary of initial conditions */
  int[] ic_division;      /* the initial condition division on each dimension
                             this specifies the starting points of all
                             simulations */
  float[] gl_min;         /* global minimum, gl_min[0] is unused since
                             time only increases */
  float[] gl_max;         /* global maximum
                             upper bound on time of simulation is gl_max[0] */
  //String model;           /* model name */
  float[] max_check_dist; /* Maximal dimension-vise distance of two simulations
                             if distance is greater densification takes place.
                             Implicit value is double the length of the
                             initial conditions interval. */
  float[] min_check_dist; /* Minimal dimension-vise distance of two simulations
                             if distance is smaller merging takes place.
                             Implicit value is 1/2000 the distance of
                             simulations at the begining inside the initial
                             conditions interval. */
  String[] var_names;     /* Names of variables, var_names[0] = "Time" */
  HashMap var_indexes;
                          /* var_name => index map */
  TermProduct[][] equations;
                          /* For each variable the equation is the sum of
                             products of which the first is alaways a constant
                             and the rest may be other variables.
                             Since time (dim 0) has no equation, the first
                             equation at index 0 corresponds to the first
                             spatial variable var_name[1].
                             If each variable appears at most once the system
                             is multiaffine. */

  ODEParser()
  {
    var_indexes = new HashMap();
    clear();
  }

  /* Clears all fields. */
  private void clear()
  {
    dims = 0;
    ic_min = null;
    ic_max = null;
    ic_division = null;
    gl_min = null;
    gl_max = null;
    max_check_dist = null;
    min_check_dist = null;
    var_names = null;
    var_indexes.clear();
    equations = null;
  }

  /**
   * Parses an *.ode file, format description in ode_format.txt.
   * On success a null is returned and all the fields are initialized
   * to correct values and may be obtained by the get_* methods.
   * On failure an error message is returned and the fields are cleared.
   *
   */
  String parse_ode_file(String filename)
  {
    PApplet dummyApplet = new PApplet();
    /* HACK I didn't want to write my own loadStrings method. */
    String lines[] = dummyApplet.loadStrings(filename);
    if (lines == null)
    {
      return "File "+filename+" not found or not readable.";
    }

    HashMap tag_mode = new HashMap();
    for (int i=0; i<Utils.ODEP_MODE_STR.length; i++)
    {
      tag_mode.put(Utils.ODEP_MODE_STR[i], new Integer(i));
    }

    int line_cntr = 0;
    int i;
    for (i=0; i<lines.length; i++)
    {
      if (lines[i].length() > 0 && lines[i].charAt(0) != '#')
      {
        lines[line_cntr] = lines[i];   /* copy useful lines to front of array */
        line_cntr++;
      }
    }
    lines = (String[]) PApplet.expand(lines, line_cntr);     /* cut away end of array */
    /* join all lines, cut apart on whitespace boundaries, trim, join and cut
       apart again on semicolon boundaries */
    String[] tokens = PApplet.splitTokens(Utils.combine("", lines));
    tokens = PApplet.trim(tokens);
    tokens = PApplet.split(PApplet.join(tokens, ""), ";");

    if (tokens.length < 1)
    {
      return "VARS field not defined";
    }

    if (!tokens[0].substring(0,5).equals("VARS:"))
    {
      return "VARS field must be defined first";
    }

    /* load the variable names and set the number of dimensions */
    var_names = PApplet.split("Time,"+tokens[0].substring(5),',');
    dims = var_names.length;
    if (dims == 1)
    {
      clear();
      return "At least one variable must be defined";
    }
    for (i=0; i<dims; i++)
    {
      String[] reg_match = PApplet.match(var_names[i],"[\\w]+");
      {
        if (reg_match.length != 1 || !reg_match[0].equals(var_names[i]))
        {
          String tmp_dup = var_names[i];
          clear();
          return "Bad variable name '"+tmp_dup+"' must have form [\\w]+";
        }
      }
      if (var_indexes.containsKey(var_names[i]))
      {
        String tmp_dup = var_names[i];
        clear();
        return "Duplicit variable name '"+tmp_dup+"'";
      }
      else
      {
        var_indexes.put(var_names[i],new Integer(i));
      }
    }

    /* initialize all the fields */
    ic_min = new float[dims];
    ic_max = new float[dims];
    ic_division = new int[dims-1];
    gl_min = new float[dims];
    gl_max = new float[dims];
    equations = new TermProduct[dims-1][];
    max_check_dist = new float[dims];
    min_check_dist = new float[dims];

    for (i=0; i<dims; i++)
    {
      ic_min[i] = 0.0f;
      ic_max[i] = 0.0f;
      gl_min[i] = 0.0f;
      gl_max[i] = 0.0f;
      max_check_dist[i] = 0.0f;
      min_check_dist[i] = 0.0f;
    }
    max_check_dist[0] = 0.1f;
    min_check_dist[0] = max_check_dist[0];

    for (i=0; i<dims-1; i++)
    {
      ic_division[i] = 2;
      equations[i] = null;
    }

    int[] var_status = new int[dims];
    /* Each variable must first have its BOUNDS defined and then its INIT
       if the order is incorrect or one of them is missing the input has
       bad format. This array checks these conditions for each variable. */
    for (i=0; i<dims; i++)
    {
      var_status[i] = Utils.ODEP_VAR_STATUS_NOBOUNDS;
    }
    var_status[0] = Utils.ODEP_VAR_STATUS_INIT_DEFINED;
    /* Time although a dimension like others should have it's limit defined by
       the TIME_LIMIT tag. */

    int tag_boundary;
    String tag_type;
    String tag_value;
    String token;
    String err_str = null;
    int mode;

    for (i=1; i<tokens.length; i++)
    {
      token = tokens[i];
      if (token.length() == 0)
      {
        continue;
      }
      tag_boundary = token.indexOf(":");
      if (tag_boundary == -1)
      {
        return "bad token: '"+token+"'";
      }
      tag_type = token.substring(0,tag_boundary);
      tag_value = token.substring(tag_boundary+1);

      if (!tag_mode.containsKey(tag_type))
      {
        err_str = "Unknown field type '"+tag_type+"' near '"+tokens[i]+"'";
      }
      else
      {
        mode = ((Integer)tag_mode.get(tag_type)).intValue();
        switch (mode)
        {
          case Utils.ODEP_MODE_VARS:
            err_str = "VARS field can be defined only once";
            break;
          case Utils.ODEP_MODE_EQ:
            err_str = parse_equation(tag_value, var_indexes, var_status);
            break;
          case Utils.ODEP_MODE_BOUNDS:
          case Utils.ODEP_MODE_TIME_LIMIT:
          case Utils.ODEP_MODE_MAX_TIME_STEP:
          case Utils.ODEP_MODE_INIT:
          case Utils.ODEP_MODE_DIV:
          case Utils.ODEP_MODE_MIN_DIST:
          case Utils.ODEP_MODE_MAX_DIST:
            err_str = parse_simple_token(tag_value, var_indexes,
                                         var_status, mode);
            break;
        }
      }

      if (err_str != null)
      {
        clear();
        return err_str;
      }

      //println(tokens[i]);
    }

    /* Set implicit min_check_dist & max_check_dist */
    for (i=1; i<dims; i++)
    {
      if (max_check_dist[i] == 0.0f)
      {
        if (ic_division[i-1] < 3)
        {
          max_check_dist[i] = 2.0f*Math.abs(ic_max[i]-ic_min[i]);
        }
        else
        {
          max_check_dist[i] =
            2.0f*Math.abs(ic_max[i]-ic_min[i])/(ic_division[i-1]-1.0f);
        }
      }
      if (min_check_dist[i] == 0.0f)
      {
        min_check_dist[i] = max_check_dist[i]/2000.0f;
      }
    }

    if (gl_max[0] == 0.0f)
    {
      clear();
      return "TIME_LIMIT not specified";
    }

    if (max_check_dist[0] == 0.0f)
    {
      clear();
      return "MAX_TIME_STEP not specified";
    }

    for (i=1; i<dims; i++)
    {
      if (equations[i-1] == null)
      {
        return "EQ not defined for "+var_names[i];
      }
      if (var_status[i] == Utils.ODEP_VAR_STATUS_NOBOUNDS)
      {
        return "BOUNDS not defined for "+var_names[i];
      }
      else if (var_status[i] == Utils.ODEP_VAR_STATUS_BOUNDS_DEFINED)
      {
        return "INIT not defined for "+var_names[i];
      }
      if (min_check_dist[i] >= max_check_dist[i])
      {
        clear();
        return "MIN_DIST["+var_names[i]+"]("+min_check_dist[i]+
               ") >= MAX_DIST["+var_names[i]+"]("+max_check_dist[i]+")";
      }
    }
    return null;
  }

  /**
   * Parses the equation token with following grammar into the TermProduct[]
   * of the left-hand side variable of the equations array.
   * d[varname] = S
   * S = S+S | N | N*V
   * V = [varname] | V*V
   * N = [number] | (-[number])
   **/
  String parse_equation(String token, HashMap var_indexes, int[] status)
  {
    if (token.length() < 4 ||
        token.charAt(0) != 'd' ||
        token.indexOf('=') == -1)
    {
      return "Bad EQ format at "+token;
    }
    String left_var = token.substring(1,token.indexOf('='));
    if (!var_indexes.containsKey(left_var))
    {
      return "Unknown variable "+left_var+" near "+token;
    }
    int left_var_index = ((Integer)var_indexes.get(left_var)).intValue();
    String[] products = PApplet.split(token.substring(token.indexOf('=')+1),"+");

    equations[left_var_index-1] = new TermProduct[products.length];

    for (int i=0; i<products.length; i++)
    {
      String[] terms = PApplet.split(products[i],"*");
      if (terms.length == 0)
      {
        return "Bad product term at "+token;
      }
      if (terms[0].length() > 2 &&
          (terms[0].charAt(0) == '(' ||
           terms[0].charAt(terms[0].length()-1) == ')'))
      {
        if (terms[0].charAt(0) != '(' ||
            terms[0].charAt(terms[0].length()-1) != ')')
        {
          return "Missing pair bracet at "+terms[0]+" near "+token;
        }
        terms[0] = terms[0].substring(1,terms[0].length()-1);
      }
      float constant = Float.valueOf(terms[0]).floatValue();
      if (Float.isNaN(constant))
      {
        return "Bad number constant format of "+terms[0]+" near "+token;
      }
      int[] term_var_indexes = new int[terms.length-1];

      for (int j=1; j<terms.length; j++)
      {
        if (!var_indexes.containsKey(terms[j]))
        {
          return "Unknown variable "+terms[j]+" near "+token;
        }
        term_var_indexes[j-1] = ((Integer)var_indexes.get(terms[j])).intValue();
      }
      equations[left_var_index-1][i] =
        new TermProduct(constant, term_var_indexes, PApplet.subset(terms,1));
      //print("equations["+(left_var_index-1)+"]["+i+"] = "+constant+" * ");
      //print_array(term_var_indexes);
      //println();
    }
    return null;
  }

  /**
   * Parses a token in the form of
   * VAR_NAME: NUMBER, NUMBER,...;
   * where the presence of VAR_NAME and the number of NUMBERS and their
   * type (FLOAT || INT) depends on the selected mode of operation.
   *
   * On success the appropriate parser fields are filled with the parsed
   * values and the status array is updated, null is returned.
   * On failure the parser is cleared and an error is returned.
   *
   * @param token string to be parsed
   * @param var_indexes mapping of variable_name => dimension_index
   * @param var_status variable parsing status for order checking
   * @return null on succes, error string on failure
   **/
  String parse_simple_token(String token, HashMap var_indexes, int[] var_status,
                            int mode)
  {
    boolean var_present = false;    /* should VAR_NAME be present */
    int number_count = 0;           /* number of NUMBERs expected */
    int number_type = 0;            /* 0 - float, 1 - int */
    int req_var_status = -1;        /* required variable status in given mode */

    switch (mode)
    {
      case Utils.ODEP_MODE_BOUNDS:
        req_var_status = Utils.ODEP_VAR_STATUS_NOBOUNDS;
        var_present = true;
        number_count = 2;
        number_type = 0;
        break;
      case Utils.ODEP_MODE_INIT:
        req_var_status = Utils.ODEP_VAR_STATUS_BOUNDS_DEFINED;
        var_present = true;
        number_count = 2;
        number_type = 0;
        break;
      case Utils.ODEP_MODE_TIME_LIMIT:
      case Utils.ODEP_MODE_MAX_TIME_STEP:
        var_present = false;
        number_count = 1;
        number_type = 0;
        break;
      case Utils.ODEP_MODE_DIV:
        var_present = true;
        number_count = 1;
        number_type = 1;
        break;
      case Utils.ODEP_MODE_MIN_DIST:
      case Utils.ODEP_MODE_MAX_DIST:
        req_var_status = Utils.ODEP_VAR_STATUS_INIT_DEFINED;
        var_present = true;
        number_count = 1;
        number_type = 0;
        break;
      default:
        return "Unknow mode "+mode;
    }

    String mode_str = Utils.ODEP_MODE_STR[mode];
    String[] token_parts = PApplet.split(token, ":");
    int var_index = -1;
    String number_str[] = null;

    if (var_present)
    {
      if (token_parts.length != 2)
      {
        return "Bad "+mode_str+" format near "+token;
      }
      if (!var_indexes.containsKey(token_parts[0]))
      {
        return "Unknown "+mode_str+" variable "+token_parts[0];
      }
      var_index = ((Integer)var_indexes.get(token_parts[0])).intValue();
      if (var_index == 0)
      {
        return "Time is a reserved variable unmodifiable with "+mode_str;
      }
      if (req_var_status != -1 && var_status[var_index] != req_var_status)
      {
        return "Bad definition order or duplicity near "+mode_str+":"+
               token+" check format definition";
      }
      number_str = PApplet.split(token_parts[1],",");
    }
    else
    {
      if (token_parts.length != 1)
      {
        return "Bad "+mode_str+" format near "+token;
      }
      number_str = new String[]{token_parts[0]};
    }

    if (number_str.length != number_count)
    {
      return "Bad "+mode_str+" number count, expected "+
             number_count+" near "+token;
    }

    float[] float_numbers = null;
    int[] int_numbers = null;

    if (number_type == 0)
    {
      float_numbers = new float[number_str.length];
      for (int i=0; i<number_str.length; i++)
      {
        float_numbers[i] = Float.valueOf(number_str[i]).floatValue();
        if (Float.isNaN(float_numbers[i]))
        {
          return "Bad number format ("+number_str[i]+") in "+
                 mode_str+" near "+token;
        }
      }
    }
    else
    {
      int_numbers = new int[number_str.length];
      StatusMod err = new StatusMod();
      for (int i=0; i<int_numbers.length; i++)
      {
        int_numbers[i] = Integer.valueOf(number_str[i]).intValue();
        if (int_numbers[i] == 0)
        {
          Utils.parse_int(number_str[i], err);
          if (err.get() == -1)
          {
            return "Bad number format ("+number_str[i]+") in "+
                   mode_str+" near "+token;
          }
        }
      }
    }

    switch (mode)
    {
      case Utils.ODEP_MODE_BOUNDS:
        if (float_numbers[0] >= float_numbers[1])
        {
          return "BOUNDS lower bound not less than upper bound in "+token;
        }
        gl_min[var_index] = float_numbers[0];
        gl_max[var_index] = float_numbers[1];
        var_status[var_index] = Utils.ODEP_VAR_STATUS_BOUNDS_DEFINED;
        break;
      case Utils.ODEP_MODE_INIT:
        if (gl_min[var_index] > float_numbers[0] ||
            float_numbers[0] >= float_numbers[1] ||
            float_numbers[1] > gl_max[var_index])
        {
          return "INIT initial conditions do not hold "+
                 "gl_min <= ic_min < ic_max <= gl_max in "+token;
        }
        ic_min[var_index] = float_numbers[0];
        ic_max[var_index] = float_numbers[1];
        var_status[var_index] = Utils.ODEP_VAR_STATUS_INIT_DEFINED;
        break;
      case Utils.ODEP_MODE_TIME_LIMIT:
        if (float_numbers[0] <= 0.0)
        {
          return "TIME_LIMIT not positive at "+token;
        }
        gl_max[0] = float_numbers[0];
        break;
      case Utils.ODEP_MODE_MAX_TIME_STEP:
        if (float_numbers[0] <= 0.0)
        {
          return "MAX_TIME_STEP not positive at "+token;
        }
        max_check_dist[0] = float_numbers[0];
        min_check_dist[0] = max_check_dist[0];
        break;
      case Utils.ODEP_MODE_DIV:
        if (int_numbers[0] < 1)
        {
          return "DIVISION not positive at "+token;
        }
        ic_division[var_index-1] = int_numbers[0];
        break;
      case Utils.ODEP_MODE_MIN_DIST:
        if (float_numbers[0] <= 0.0)
        {
          return "MIN_DIST not positive at "+token;
        }
        min_check_dist[var_index] = float_numbers[0];
        break;
      case Utils.ODEP_MODE_MAX_DIST:
        if (float_numbers[0] <= 0.0)
        {
          return "MAX_DIST not positive at "+token;
        }
        max_check_dist[var_index] = float_numbers[0];
        break;
    }
    return null;
  }

  int get_dims()
  {
    return dims;
  }

  float[] get_ic_min()
  {
    return ic_min;
  }

  float[] get_ic_max()
  {
    return ic_max;
  }

  int[] get_ic_division()
  {
    return ic_division;
  }

  float[] get_gl_min()
  {
    return gl_min;
  }

  float[] get_gl_max()
  {
    return gl_max;
  }

  float[] get_min_check_dist()
  {
    return min_check_dist;
  }

  float[] get_max_check_dist()
  {
    return max_check_dist;
  }

  String[] get_var_names()
  {
    return var_names;
  }

  HashMap get_var_indexes()
  {
    return var_indexes;
  }

  TermProduct[][] get_equations()
  {
    return equations;
  }
}
