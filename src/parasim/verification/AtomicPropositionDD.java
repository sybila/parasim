/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim.verification;

import parasim.Utils;
import parasim.Point;
import parasim.ODE;

/**
 * Proposition of type Derivative op Derivative
 * @author sven
 */
class AtomicPropositionDD implements Evaluable
{
  private int var1_index;
  private String var1_str;
  private int operator;           /* one of AP_ operator constants */
  private int var2_index;
  private String var2_str;
  private ODE ode;

  /**
   * Constructor
   *
   * Note: to prevent multiple calls of ode.compute_derivatives(p) in each
   *       atomic proposition, the higher level method must ensure that it
   *       has been called at least once before calling the valid() methods.
   *
   * @param var1 index of the variable derivative the AP speaks about
   * @param var1_str name of variable1
   * @param op operator inside the atomic proposition
   * @param var2 index of the variable derivative the AP speaks about
   * @param var2_str name of variable2
   * @param ode the ODE system, used to obtain the derivatives
   **/
  AtomicPropositionDD(int var1, String var1_str, int op,
                      int var2, String var2_str, ODE ode)
  {
    var1_index = var1;
    this.var1_str = var1_str;
    operator = op;
    var2_index = var2;
    this.var2_str = var2_str;
    this.ode = ode;
  }

  /* Returns true if the proposition is valid in given state. */
  public boolean valid(Point p)
  {
    float val1 = ode.get_derivative(var1_index);
    float val2 = ode.get_derivative(var2_index);

    switch(operator) {
      case Utils.AP_LESS:
        return (val1 < val2);
      case Utils.AP_LESS_EQUAL:
        return (val1 <= val2);
      case Utils.AP_EQUAL:
        return (val1 == val2);
      case Utils.AP_GREATER_EQUAL:
        return (val1 >= val2);
      case Utils.AP_GREATER:
        return (val1 > val2);
      case Utils.AP_NOT_EQUAL:
        return (val1 != val2);
      default:
        return false;
    }
  }

  @Override
  public String toString()
  {
    return var1_str+(Utils.AP_OP_STR[operator-1])+var2_str;
  }
}
