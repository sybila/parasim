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
public interface Evaluable
{
  public boolean valid(Point p);
  
  @Override
  public String toString();
}
