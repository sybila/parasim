/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim;

import java.io.InputStream;
import processing.core.PApplet;

/**
 *
 * @author sven
 */
public class Utils {

    static String combine(String glue, String... s)
    {
      if (s.length == 0) return "";
      StringBuilder out = new StringBuilder();
      out.append(s[0]);
      for (int i=1; i<s.length; i++)
      {
        out.append(glue).append(s[i]);
      }
      return out.toString();
    }

    /****************************** Status Constants ******************************/

    public static final int ERR_OUT_OF_MEMORY = 1;

    public static final int SIM_STATUS_COMP = 0;
          /* Computing points (and distance checking) */
    public static final int SIM_STATUS_DC   = 1;
          /* Point computation finished, distance checking still in progress */
    public static final int SIM_STATUS_FIN  = 2;
          /* Point computation and distance checking finished */
    public static final String[] SIM_STATUS_STR = new String[]
          {"COMP","DISTANCE_CHECKING","FINISHED"};

    public static final int SIM_END_STATUS_COMP = 0;
          /* Simulation still being computed, end not reached */
    public static final int SIM_END_STATUS_EQ   = 1;
          /* Simulation ended in equilibrium (dx = 0 or |dx| < EQ_CONST) */
    public static final int SIM_END_STATUS_CYC  = 2;
          /* Simulation ended in cycle FIXME cycle info */
    public static final int SIM_END_STATUS_MERGE = 3;
          /* Simulation merged into another simulation FIXME merge info */
    public static final int SIM_END_STATUS_TIMEOUT = 4;
          /* Simulation timeout reached */
    public static final int SIM_END_STATUS_LTL = 5;
          /* The LTL property prohibited further computation */
    public static final int SIM_END_STATUS_MIN = 6;
          /* Simulation reached global minimum at some dimension */
    public static final int SIM_END_STATUS_MAX = 7;
          /* Simulation reached global maximum at some dimension */
    public static final String[] SIM_END_STATUS_STR = new String[]
          {"COMP","EQILIBRIUM","CYCLE","MERGE","TIMEOUT","LTL","MIN","MAX"};

    public static final int DC_STATUS_NONE = -1;
          /* Distance checking has not yet started */
    public static final int DC_STATUS_COMP = 0;
          /* Distance checking in progres, no result yet obtained */
    public static final int DC_STATUS_MERGE = 1;
          /* Simulation has merged with neighbour */
    public static final int DC_STATUS_SEP = 2;
          /* Simulations have been separated since they converge to
             different points. No new simulation was been introduced.
             FIXME separation detection */
    public static final int DC_STATUS_SEP_NEW = 3;
          /* Simulations have been separated since they converge to
             different points. A new simulation has been introduced inside the
             original simulations' initial condition interval.
             FIXME separation detection */
    public static final int DC_STATUS_END_LOCAL = 4;
          /* Finished, local end reached */
    public static final int DC_STATUS_END_NEIGHBOUR = 5;
          /* Finished, neighbour's end reached */
    public static final int DC_STATUS_CYCLE_LOCAL = 6;
          /* Finished, local simulation ends in cycle while staying
             in checking distance */
    public static final int DC_STATUS_CYCLE_NEIGHBOUR = 7;
          /* Finished, neighbour simulation ends in cycle while staying in
             checking distance */
    public static final int DC_STATUS_CYCLE_BOTH = 8;
          /* Finished, both simulations have ended in a cycle while staying in
             checking distance */
    public static final String[] DC_STATUS_STR = new String[]
          {"COMP","MERGE","SEP","SEP_NEW","END_LOCAL","END_NEIGHBOUR","CYCLE_LOCAL",
           "CYCLE_NEIGHBOUR","CYCLE_BOTH"};

    /************************** Atomic proposition constants **********************/

    public static final int AP_LESS = 1;
    public static final int AP_LESS_EQUAL = 2;
    public static final int AP_EQUAL = 3;
    public static final int AP_GREATER_EQUAL = 4;
    public static final int AP_GREATER = 5;
    public static final int AP_NOT_EQUAL = 6;
    public static final String[] AP_OP_STR = new String[]
          {"<","<=","==",">=",">","!="};

    public static final int AP_GROUP_AND = 1;
    public static final int AP_GROUP_OR = 2;

    public static final int AP_OPERAND_TYPE_VAR = 0;
    public static final int AP_OPERAND_TYPE_DVAR = 1;
    public static final int AP_OPERAND_TYPE_NUM = 2;

    /************************** ODE Parser mode constants *************************/

    public static final int ODEP_VAR_STATUS_NOBOUNDS = 0;
      /* variable bounds not defined */
    public static final int ODEP_VAR_STATUS_BOUNDS_DEFINED = 1;
      /* variable bounds defined */
    public static final int ODEP_VAR_STATUS_INIT_DEFINED = 2;
      /* variable bounds and initial interval both defined */
    public static final int ODEP_MODE_VARS = 0;
    public static final int ODEP_MODE_EQ = 1;
    public static final int ODEP_MODE_BOUNDS = 2;
    public static final int ODEP_MODE_TIME_LIMIT = 3;
    public static final int ODEP_MODE_MAX_TIME_STEP = 4;
    public static final int ODEP_MODE_INIT = 5;
    public static final int ODEP_MODE_DIV = 6;
    public static final int ODEP_MODE_MIN_DIST = 7;
    public static final int ODEP_MODE_MAX_DIST = 8;
    public static final String[] ODEP_MODE_STR = new String[]
          {"VARS","EQ","BOUNDS","TIME_LIMIT","MAX_TIME_STEP","INIT","DIV",
           "MIN_DIST","MAX_DIST"};

    /******************************** GUI constants *******************************/

    public static final int GUI_DIALOG_TYPE_NONE = 0;
    public static final int GUI_DIALOG_TYPE_ODE = 1;
    public static final int GUI_DIALOG_TYPE_PROPERTY = 2;

    public static final int GUI_DIALOG_STATUS_NONE = 0;
    public static final int GUI_DIALOG_STATUS_WAIT = 1;
    public static final int GUI_DIALOG_STATUS_DONE = 2;

    /************************** Property checking constants ***********************/

    public static final int PC_STATUS_NONE = 0;
    public static final int PC_STATUS_COMP = 1;
    public static final int PC_STATUS_VALID = 2;
    public static final int PC_STATUS_INVALID = 3;
    public static final int PC_STATUS_DONTKNOW = 4;

    /************************** ViewPort constants ********************************/



    /************************** Computation tuning constants **********************/

    public static final int POINTS_DATA_BLOCK_LENGTH = 10000;
          /* Number of points in a single PDB */
    public static final int MAX_SIMULATIONS = 5000;
          /* Maximum number of simulations in the simulation storage */
    public static final int SIM_DENSE_POINTS_BATCH = 1000;
          /* number of dense points of one simulation computed in a single call to
             extend_simulations() */
    public static final int DC_LOCAL_POINTS_BATCH = 20;
          /* number of local points processed during distance checking of a single
             simulation's neighbour */
    public static final int CYC_DETECT_STACK_LEN = 20;
          /* size of the cycle detection stack */
    public static final float REL_ERROR = 0.001f;
          /* FIXME relative error of a single simulation batch */

    /******************************************************************************/


    /**
     * Parses string s into int, if s contains a number its value is returned
     * and err.val == 0, else 0 is returned and err.val == -1.
     *
     * Unlike the processing int() conversion method the feedback about the
     * result can not be obtained by comparison to NaN.
     **/
    public static int parse_int(String s, StatusMod err)
    {
      int result;
      err.set(0);
      try
      {
        result = Integer.parseInt(s);
      }
      catch (NumberFormatException e)
      {
        result = 0;
        err.set(-1);
      }
      return result;
    }

    /* Just a simple floating point number for the axes */
    public static String pf(int num, int decexp)
    {
      if (decexp != 0) return num+".0E"+decexp;
      return num+".0";
    }

    void print_array(float[] arr)
    {
      if (arr == null || arr.length == 0) return;
      PApplet.print("array(");
      for (int i=0; i<arr.length; i++)
      {
        if (i>0) PApplet.print("; ");
        //print(arr[i]);
        PApplet.print(PApplet.nf(arr[i],1,8));
      }
      PApplet.println(")");
    }

    void print_array(float[][] arr)
    {
      if (arr == null || arr.length == 0) return;
      for (int i=0; i<arr.length; i++)
      {
        if (arr[i] != null)
        {
          PApplet.print("  ["+i+"] => ");
          this.print_array(arr[i]);
        }
        else
        {
          //print("  ["+i+"] => ");
          //println("null");
        }
      }
    }

    void print_array(Point[] arr)
    {
      if (arr == null || arr.length == 0) return;
      for (int i=0; i<arr.length; i++)
      {
        if (arr[i] != null)
        {
          PApplet.print("  ["+i+"] => ");
          this.print_array(arr[i].c);
        }
        else
        {
          //print("  ["+i+"] => ");
          //println("null");
        }
      }
    }

    void print_array(int[] arr)
    {
      if (arr == null || arr.length == 0) return;
      PApplet.print("array(");
      for (int i=0; i<arr.length; i++)
      {
        if (i>0) PApplet.print("; ");
        PApplet.print(arr[i]);
      }
      PApplet.println(")");
    }

    void print_array(int[][] arr)
    {
      if (arr == null || arr.length == 0) return;
      for (int i=0; i<arr.length; i++)
      {
        if (arr[i] != null)
        {
          PApplet.print("  ["+i+"] => ");
          this.print_array(arr[i]);
        }
        else
        {
          //print("  ["+i+"] => ");
          //println("null");
        }
      }
    }

    public static String getCWD(){
      String cwd = null;
      try
      {
        cwd = new java.io.File( "." ).getCanonicalPath();
      } catch (java.io.IOException e) {}
      return cwd;
    }
}
