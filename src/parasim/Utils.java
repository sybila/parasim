/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim;

//import java.io.InputStream;
import processing.core.PApplet;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.StringTokenizer;


/**
 *
 * @author sven
 */
public class Utils {

    static public String combine(String glue, String... s)
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

    static public String nf(float val, int left, int right)
    {
        return PApplet.nf(val, left, right);
    }

    static public String join(String[] pieces, String glue)
    {
        return PApplet.join(pieces, glue);
    }

    static public String[] match(String what, String regexp) {
      return PApplet.match(what, regexp);
    }

    static public String[] loadStrings(String filename){
        PApplet dummyApplet = new PApplet();
        return dummyApplet.loadStrings(filename);
    }

    /* Taken from processing.core.PApplet.java *************/

    static final String WHITESPACE = " \t\n\r\f\u00A0";

    static public boolean[] expand(boolean list[]) {
        return expand(list, list.length << 1);
    }

    static public boolean[] expand(boolean list[], int newSize) {
        boolean temp[] = new boolean[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
        return temp;
    }

    static public byte[] expand(byte list[]) {
        return expand(list, list.length << 1);
    }

    static public byte[] expand(byte list[], int newSize) {
        byte temp[] = new byte[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
        return temp;
    }

    static public char[] expand(char list[]) {
        return expand(list, list.length << 1);
    }

  static public char[] expand(char list[], int newSize) {
    char temp[] = new char[newSize];
    System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
    return temp;
  }


  static public int[] expand(int list[]) {
    return expand(list, list.length << 1);
  }

  static public int[] expand(int list[], int newSize) {
    int temp[] = new int[newSize];
    System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
    return temp;
  }


  static public float[] expand(float list[]) {
    return expand(list, list.length << 1);
  }

  static public float[] expand(float list[], int newSize) {
    float temp[] = new float[newSize];
    System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
    return temp;
  }


  static public String[] expand(String list[]) {
    return expand(list, list.length << 1);
  }

  static public String[] expand(String list[], int newSize) {
    String temp[] = new String[newSize];
    // in case the new size is smaller than list.length
    System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
    return temp;
  }

  static public Object expand(Object array) {
    return expand(array, Array.getLength(array) << 1);
  }

  static public Object expand(Object list, int newSize) {
    Class<?> type = list.getClass().getComponentType();
    Object temp = Array.newInstance(type, newSize);
    System.arraycopy(list, 0, temp, 0,
                     Math.min(Array.getLength(list), newSize));
    return temp;
  }

    static public int[] sort(int what[]) {
    return sort(what, what.length);
  }


  static public int[] sort(int[] what, int count) {
    int[] outgoing = new int[what.length];
    System.arraycopy(what, 0, outgoing, 0, what.length);
    Arrays.sort(outgoing, 0, count);
    return outgoing;
  }

  static public String[] split(String what, char delim)
  {
    return PApplet.split(what, delim);
  }

  static public String[] subset(String list[], int start) {
    return subset(list, start, list.length - start);
  }

  static public String[] subset(String list[], int start, int count) {
    String output[] = new String[count];
    System.arraycopy(list, start, output, 0, count);
    return output;
  }

  /**
   * Remove whitespace characters from the beginning and ending
   * of a String. Works like String.trim() but includes the
   * unicode nbsp character as well.
   */
  static public String trim(String str) {
    return str.replace('\u00A0', ' ').trim();
  }

  /**
   * Trim the whitespace from a String array. This returns a new
   * array and does not affect the passed-in array.
   */
  static public String[] trim(String[] array) {
    String[] outgoing = new String[array.length];
    for (int i = 0; i < array.length; i++) {
      if (array[i] != null) {
        outgoing[i] = array[i].replace('\u00A0', ' ').trim();
      }
    }
    return outgoing;
  }

    /**
   * Split the provided String at wherever whitespace occurs.
   * Multiple whitespace (extra spaces or tabs or whatever)
   * between items will count as a single break.
   * <P>
   * The whitespace characters are "\t\n\r\f", which are the defaults
   * for java.util.StringTokenizer, plus the unicode non-breaking space
   * character, which is found commonly on files created by or used
   * in conjunction with Mac OS X (character 160, or 0x00A0 in hex).
   * <PRE>
   * i.e. splitTokens("a b") -> { "a", "b" }
   *      splitTokens("a    b") -> { "a", "b" }
   *      splitTokens("a\tb") -> { "a", "b" }
   *      splitTokens("a \t  b  ") -> { "a", "b" }</PRE>
   */
  static public String[] splitTokens(String what) {
    return splitTokens(what, WHITESPACE);
  }


  /**
   * Splits a string into pieces, using any of the chars in the
   * String 'delim' as separator characters. For instance,
   * in addition to white space, you might want to treat commas
   * as a separator. The delimeter characters won't appear in
   * the returned String array.
   * <PRE>
   * i.e. splitTokens("a, b", " ,") -> { "a", "b" }
   * </PRE>
   * To include all the whitespace possibilities, use the variable
   * WHITESPACE, found in PConstants:
   * <PRE>
   * i.e. splitTokens("a   | b", WHITESPACE + "|");  ->  { "a", "b" }</PRE>
   */
  static public String[] splitTokens(String what, String delim) {
    StringTokenizer toker = new StringTokenizer(what, delim);
    String pieces[] = new String[toker.countTokens()];

    int index = 0;
    while (toker.hasMoreTokens()) {
      pieces[index++] = toker.nextToken();
    }
    return pieces;
  }

  static public final int min(int a, int b) {
    return (a < b) ? a : b;
  }
  /*
  static public final float min(float a, float b) {
    return (a < b) ? a : b;
  }


  static public final int min(int a, int b, int c) {
    return (a < b) ? ((a < c) ? a : c) : ((b < c) ? b : c);
  }

  static public final float min(float a, float b, float c) {
    return (a < b) ? ((a < c) ? a : c) : ((b < c) ? b : c);
  }
  */

  /**
   * Find the minimum value in an array.
   * Throws an ArrayIndexOutOfBoundsException if the array is length 0.
   * @param list the source array
   * @return The minimum value
   */
  /*static public final int min(int[] list) {
    if (list.length == 0) {
      throw new ArrayIndexOutOfBoundsException(ERROR_MIN_MAX);
    }
    int min = list[0];
    for (int i = 1; i < list.length; i++) {
      if (list[i] < min) min = list[i];
    }
    return min;
  }*/
  /**
   * Find the minimum value in an array.
   * Throws an ArrayIndexOutOfBoundsException if the array is length 0.
   * @param list the source array
   * @return The minimum value
   */
  /*static public final float min(float[] list) {
    if (list.length == 0) {
      throw new ArrayIndexOutOfBoundsException(ERROR_MIN_MAX);
    }
    float min = list[0];
    for (int i = 1; i < list.length; i++) {
      if (list[i] < min) min = list[i];
    }
    return min;
  }*/

  /**
   * Calls System.arraycopy(), included here so that we can
   * avoid people needing to learn about the System object
   * before they can just copy an array.
   */
  static public void arrayCopy(Object src, int srcPosition,
                               Object dst, int dstPosition,
                               int length) {
    System.arraycopy(src, srcPosition, dst, dstPosition, length);
  }


  /**
   * Convenience method for arraycopy().
   * Identical to <CODE>arraycopy(src, 0, dst, 0, length);</CODE>
   */
  static public void arrayCopy(Object src, Object dst, int length) {
    System.arraycopy(src, 0, dst, 0, length);
  }


  /**
   * Shortcut to copy the entire contents of
   * the source into the destination array.
   * Identical to <CODE>arraycopy(src, 0, dst, 0, src.length);</CODE>
   */
  static public void arrayCopy(Object src, Object dst) {
    System.arraycopy(src, 0, dst, 0, Array.getLength(src));
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

    public static void print_array(Point p)
    {
      if (p == null || p.getDims() == 0) return;
      System.out.print("Point[");
      for (int i=0; i<p.getDims(); i++)
      {
        if (i>0) System.out.print("; ");        
        System.out.print(nf(p.get(i),1,8));
      }
      System.out.println(")");
    }

    public static void print_array(float[] arr)
    {
      if (arr == null || arr.length == 0) return;
      System.out.print("array(");
      for (int i=0; i<arr.length; i++)
      {
        if (i>0) System.out.print("; ");
        System.out.print(nf(arr[i],1,8));
      }
      System.out.println(")");
    }

    public static void print_array(float[][] arr)
    {
      if (arr == null || arr.length == 0) return;
      for (int i=0; i<arr.length; i++)
      {
        if (arr[i] != null)
        {
          System.out.print("  ["+i+"] => ");
          print_array(arr[i]);
        }
        else
        {
          //print("  ["+i+"] => ");
          //println("null");
        }
      }
    }

    public static void print_array(Point[] arr)
    {
      if (arr == null || arr.length == 0) return;
      for (int i=0; i<arr.length; i++)
      {
        if (arr[i] != null)
        {
          System.out.print("  ["+i+"] => ");
          print_array(arr[i]);
        }
        else
        {
          //print("  ["+i+"] => ");
          //println("null");
        }
      }
    }

    public static void print_array(int[] arr)
    {
      if (arr == null || arr.length == 0) return;
      System.out.print("array(");
      for (int i=0; i<arr.length; i++)
      {
        if (i>0) System.out.print("; ");
        System.out.print(arr[i]);
      }
      System.out.println(")");
    }

    public static void print_array(int[][] arr)
    {
      if (arr == null || arr.length == 0) return;
      for (int i=0; i<arr.length; i++)
      {
        if (arr[i] != null)
        {
          System.out.print("  ["+i+"] => ");
          print_array(arr[i]);
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
