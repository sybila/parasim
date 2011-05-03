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
public class Transition
{
  int from, to;         /* origin and target state indexes */
  String from_s, to_s;  /* origin and target state names */
  Evaluable guard;      /* condition guarding the transition */

  Transition(int from, String from_s, int to, String to_s, Evaluable guard)
  {
    this.from = from;
    this.from_s = from_s;
    this.to = to;
    this.to_s = to_s;
    this.guard = guard;
  }

  boolean enabled(Point p)
  {
    if (guard == null)
    {
      return true;
      /* Some transitions have empty guards which are always true */
    }
    else
    {
      return guard.valid(p);
    }
  }
  @Override
  public String toString()
  {
    return from_s+" -> "+to_s+" {"+
           ((guard == null)?"":" guard "+guard.toString()+" ")+"}";
  }

  boolean no_guard()
  {
    return (guard == null);
  }
}
