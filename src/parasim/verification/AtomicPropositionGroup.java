/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim.verification;

import parasim.Utils;
import parasim.Point;

/**
 *
 * @author sven
 */
public class AtomicPropositionGroup implements Evaluable
{
  private int type; /* one of AP_GROUP_ constants */
  private Evaluable[] sub_aps;

  public AtomicPropositionGroup(Evaluable[] sub_aps, int type)
  {
    this.type = type;
    this.sub_aps = sub_aps;
  }

  public boolean valid(Point p)
  {
    if (type == Utils.AP_GROUP_AND)
    {
      for (int i=0; i<sub_aps.length; i++)
      {
        if (!sub_aps[i].valid(p))
        {
          return false;
        }
      }
      return true;
    }
    else /* type == AP_GROUP_OR */
    {
      for (int i=0; i<sub_aps.length; i++)
      {
        if (!sub_aps[i].valid(p))
        {
          return true;
        }
      }
      return false;
    }
  }

  @Override
  public String toString()
  {
    String[] sub_str = new String[sub_aps.length];
    for (int i=0; i<sub_aps.length; i++)
    {
      sub_str[i] = sub_aps[i].toString();
    }
    return '('+Utils.join(sub_str,(type == Utils.AP_GROUP_AND)?" && ":" || ")+')';
  }
}
