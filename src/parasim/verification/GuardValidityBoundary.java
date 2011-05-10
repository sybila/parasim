/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim.verification;

import parasim.PointsDataBlock;

/**
 *
 * @author sven
 */
public class GuardValidityBoundary
{
  private PointsDataBlock pdb;
  private int offset;
  private Transition[] possible_transitions;

  public GuardValidityBoundary(PointsDataBlock pdb, int offset, Transition[] trans)
  {
    this.pdb = pdb;
    this.offset = offset;
    possible_transitions = trans;
  }

  public boolean transitions_equal(Transition[] trans)
  {
    if (trans == null && possible_transitions == null)
    {
      return true;
    }
    else if (trans == null)
    {
      return false;
    }
    else if (trans.length != possible_transitions.length)
    {
      return false;
    }
    else
    {
      for (int i=0; i<possible_transitions.length; i++)
      {
        if (trans[i].from != possible_transitions[i].from ||
            trans[i].to != possible_transitions[i].to)
        {
          return false;
        }
      }
      return true;
    }
  }
}
