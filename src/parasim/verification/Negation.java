/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim.verification;

import parasim.Point;

/**
 *
 * @author sven
 */
public class Negation implements Evaluable
{
  private Evaluable target;

  public Negation(Evaluable target)
  {
    this.target = target;
  }

  public boolean valid(Point p)
  {
    return !target.valid(p);
  }

  @Override
  public String toString()
  {
    return "not "+target.toString();
  }
}
