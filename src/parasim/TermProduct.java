/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim;

/**
 * Class representing a single product of a constant and several variables.
 * Used to represent parts of differential equations and to evaluate them.
 * @author sven
 */
public class TermProduct {

  private float constant;
  private int[] var_indexes;
  private String[] var_names;

  /**
   * @param constant an arbitrary floating point number
   * @param indexes identify which variables (point dimensions) are to be
   *        multiplied together with the constant
   * @param var_names variable names for output
   */
  public TermProduct(float constant, int[] indexes, String[] var_names)
  {
    this.constant = constant;
    var_indexes = indexes;
    this.var_names = var_names;
  }

  /**
   * Evaluates the term in point p
   * @param p point in which to evaluate
   * @return value in p
   */
  public float eval(Point p)
  {
    float result = constant;
    for (int i=0; i<var_indexes.length; i++)
    {
      result *= p.get(var_indexes[i]);
    }
    return result;
  }

  /**
   * Concatenates the constant and the variable names for output
   * @return textual representation of term
   */
  @Override
  public String toString()
  {
    StringBuilder s = new StringBuilder();
    if (constant < 0.0)
    {
        s.append("(").append(constant).append(")");
    }
    else s.append(constant);
    if (var_names.length > 0)
    {
      s.append("*");
      s.append(Utils.combine("*",var_names));
    }
    return s.toString();
  }
}