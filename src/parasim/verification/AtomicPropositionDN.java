/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim.verification;

import parasim.Utils;
import parasim.Point;
import parasim.ODE;

/**
 * Proposition of type Derivative op Number
 * @author sven
 */
class AtomicPropositionDN implements Evaluable
{
  private int var_index;
  private String var_str;
  private int operator;           /* one of AP_ operator constants */
  private float constant;
  private ODE ode;

  /**
   * Constructor
   *
   * Note: to prevent multiple calls of ode.compute_derivatives(p) in each
   *       atomic proposition, the higher level method must ensure that it
   *       has been called at least once before calling the valid() methods.
   *
   * @param var index of the variable derivative the AP speaks about
   * @param var_str variable name
   * @param op operator inside the atomic proposition
   * @param c constant to which to compare
   * @param ode the ODE system, used to obtain the derivatives
   **/
  public AtomicPropositionDN(int var, String var_str, int op, float c, ODE ode)
  {
    var_index = var;
    this.var_str = var_str;
    operator = op;
    constant = c;
    this.ode = ode;
  }

  /* Returns true if the proposition is valid in given state. */
  public boolean valid(Point p)
  {
    float val = ode.get_derivative(var_index);

    switch(operator) {
      case Utils.AP_LESS:
        return (val < constant);
      case Utils.AP_LESS_EQUAL:
        return (val <= constant);
      case Utils.AP_EQUAL:
        return (val == constant);
      case Utils.AP_GREATER_EQUAL:
        return (val >= constant);
      case Utils.AP_GREATER:
        return (val > constant);
      case Utils.AP_NOT_EQUAL:
        return (val != constant);
      default:
        return false;
    }
  }

  @Override
  public String toString()
  {
    return var_str+(Utils.AP_OP_STR[operator-1])+constant;
  }

}
