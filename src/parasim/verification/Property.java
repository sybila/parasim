/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim.verification;

import java.util.HashMap;
import parasim.ODE;
import parasim.Point;
import parasim.Utils;

/**
 * Represents an LTL property automaton to express properties of numerical
 * simulations with discreet states.
 *
 * Gives methods to compute a run starting at the initial_state
 * and taking further transitions to successor states with states of the
 * numerical simulation provided as points in time.
 * @author sven
 */
public class Property
{
  private int state_count;              /* number of states of automaton */
  private String[] state_names;         /* state's names */
  private HashMap state_indexes;        /* mapping of state_name => index */
  private int[] initial_states;         /* initial states of atomaton */
  private int[] accepting_states;       /* set of accepting states */
  private Transition[][] transitions;   /* transitions of each state */
  private int transition_count;
  private boolean has_derivatives;
                                /* flag to indicate if variable derivatives
                                   are present in atomic propositions and
                                   therefore must be updated before calling
                                   transition.enabled() */
  private ODE ode;

  public Property()
  {
    state_count = 0;
    state_names = null;
    state_indexes = null;
    initial_states = null;
    accepting_states = null;
    transitions = null;
    transition_count = 0;
    has_derivatives = false;
    ode = null;
  }

  /**
   * Tries to load an LTL property file. On success all fields are filled
   * with correct data and null is returned, on failure an error message
   * is returned and fields are unmodified.
   *
   * @param filename file with property
   * @param var_indexes mapping of variable_names => indexes of ODE system
   * @param ode ODE system used in case of variable derivatives in atomic
   *        propositions of transition guard formulae
   **/
  public String load_ltl_file(String filename, HashMap var_indexes, ODE ode)
  {
    PropertyParser parser = new PropertyParser();

    String err_str = parser.parse_ltl_file(filename, var_indexes, ode);
    if (err_str != null)
    {
      return "ERROR: parse_ltl_file(): "+err_str;
    }
    else
    {
      state_count = parser.get_state_count();
      state_names = parser.get_state_names();
      state_indexes = new HashMap(parser.get_state_indexes());
      initial_states = parser.get_initial_states();
      accepting_states = parser.get_accepting_states();
      transitions = parser.get_transitions();
      transition_count = 0;
      for (int i=0; i<transitions.length; i++)
      {
        transition_count += transitions[i].length;
      }
      has_derivatives = parser.get_has_derivatives();
      this.ode = ode;
    }

    return null;
  }

  /**
   * Evaluates guards on all transitions of the given state and returns a set
   * of all possible successors.
   *
   * @param ode_updated flag indicating if ODE has computed derivatives for p
   **/
  public int[] get_successors(int state, Point p, boolean ode_updated)
  {
    if (has_derivatives && !ode_updated)
    {
      ode.compute_derivatives(p);
    }

    int[] succs = new int[transitions[state].length];
    int succs_count = 0;

    for (int i=0; i<transitions[state].length; i++)
    {
      if (transitions[state][i].enabled(p))
      {
        succs[succs_count] = transitions[state][i].to;
        succs_count++;
      }
    }
    return Utils.expand(succs, succs_count);
  }

  public int get_state_count()
  {
    return state_count;
  }

  public int[] get_successors(int[] states, Point p, boolean ode_updated)
  {
    if (has_derivatives && !ode_updated)
    {
      ode.compute_derivatives(p);
    }

    int[] succs = new int[transition_count];
    int succs_count = 0;
    boolean found;

    for (int i=0; i<states.length; i++)
    {
      int[] tmp_succs = get_successors(states[i], p, true);
      for (int j=0; j<tmp_succs.length; j++)
      {
        found = false;
        for (int k=0; k<succs_count; k++)
        {
          if (succs[k] == tmp_succs[j])
          {
            found = true;
            break;
          }
        }
        if (!found)
        {
          succs[succs_count] = tmp_succs[j];
          succs_count++;
        }
      }
    }
    succs = (int[]) Utils.expand(succs, succs_count);
    return succs;
  }


  /**
   * Returns the set of enabled transitions in point p.
   */
  public Transition[] get_enabled_transitions(Point p, boolean ode_updated)
  {
    Transition[] trans = new Transition[transition_count];
    int enabled_count = 0;
    for (int i=0; i<transitions.length; i++)
    {
      for (int j=0; j<transitions[i].length; j++)
      {
        if (transitions[i][j].enabled(p))
        {
          trans[enabled_count] = transitions[i][j];
          enabled_count++;
        }
      }
    }
    trans = (Transition[]) Utils.expand(trans, enabled_count);
    return trans;
  }

  /**
   * Returns true if there are any "always true" null guards coming out of
   * the states.
   */
  public boolean tautology_guards(int[] states)
  {
    for (int i=0; i<states.length; i++)
    {
      for (int j=0; j<transitions[states[i]].length; j++)
      {
        if (transitions[i][j].no_guard())
        {
          return true;
        }
      }
    }
    return false;
  }

  public int[] get_initial_states()
  {
    return initial_states;
  }

  public int[] get_accepting_states()
  {
    return accepting_states;
  }

  /**
   * Returns the intersection with the accepting_states.
   */
  public int[] intersect_accepting_states(int[] states)
  {
    int[] inter = new int[accepting_states.length];
    int inter_count = 0;
    for (int i=0; i<accepting_states.length; i++)
    {
      for (int j=0; j<states.length; j++)
      {
        if (accepting_states[i] == states[j])
        {
          inter[inter_count] = states[j];
          inter_count++;
          break;
        }
      }
    }
    inter = (int[]) Utils.expand(inter, inter_count);
    return inter;
  }

  /**
   * Loads a default property G true.
   **/
  public void set_default_property()
  {
    state_count = 1;
    state_names = new String[]{"q"};
    state_indexes = new HashMap();
    state_indexes.put("q",new Integer(0));
    initial_states = new int[]{0};
    accepting_states = new int[]{0};
    transitions = new Transition[1][1];
    transitions[0][0] = new Transition(0,"q",0,"q",null);
    transition_count = 1;
    has_derivatives = false;
    ode = null;
  }

  @Override
  public String toString()
  {
    String s = "property LTL {\n";
    s += "state "+Utils.join(state_names, ", ")+";\n";
    s += "init "+state_names[initial_states[0]];
    for (int i=1; i<initial_states.length; i++)
    {
      s += ", "+state_names[initial_states[i]];
    }
    s += ";\n";
    s += "accept "+state_names[accepting_states[0]];
    for (int i=1; i<accepting_states.length; i++)
    {
      s += ", "+state_names[accepting_states[i]];
    }
    s += ";\n";
    for (int i=0; i<transitions.length; i++)
    {
      for (int j=0; j<transitions[i].length; j++)
      {
        s += transitions[i][j].toString() + "\n";
      }
    }
    return s+"}\n";
  }
}
