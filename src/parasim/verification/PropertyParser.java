/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim.verification;

import java.util.HashMap;
import parasim.ODE;
import parasim.Utils;

/**
 *
 * @author sven
 */
public /**
 * TODO
 **/
class PropertyParser
{
  private int state_count;              /* number of states of automaton */
  private String[] state_names;         /* state's names */
  private HashMap state_indexes;        /* mapping of state_name => index */
  private int[] initial_states;         /* initial states of atomaton */
  private int[] accepting_states;       /* set of accepting states */
  private Transition[][] transitions;   /* transitions of each state */

  private String err_str;       /* passing error messages from methods */
  private ODE ode;              /* during parse_ltl_file() holds ode model
                                   for AtomicProposition initialization */
  private boolean has_derivatives;
                                /* flag to indicate if variable derivatives
                                   are present in atomic propositions and
                                   therefore must be updated before calling
                                   transition.enabled() */

  public PropertyParser()
  {
    state_indexes = new HashMap();
    clear();
  }

  private void clear()
  {
    state_count = 0;
    state_names = null;
    state_indexes.clear();
    initial_states = null;
    accepting_states = null;
    transitions = null;
    ode = null;
    has_derivatives = false;
  }

  private String clear(String s)
  {
    clear();
    return s;
  }

  /**
   * Parses an LTL property file in DVE format as restricted in ltl_format.txt.
   *
   * @param filename file to parse
   * @param var_indexes mapping of variable_names => variable_indexes from
   *        an ODE file loaded before
   **/
  String parse_ltl_file(String filename, HashMap var_indexes, ODE ode)
  {
    err_str = null;
    this.ode = ode;
    
    String lines[] = Utils.loadStrings(filename);
    if (lines == null)
    {
      clear();
      return "File "+filename+" not found or not readable.";
    }
    int line_cntr = 0;
    int i;
    for (i=0; i<lines.length; i++)
    {
      if (lines[i].length() > 0 && lines[i].charAt(0) != '#')
      { /* copy useful lines to front of array */
        lines[line_cntr] = Utils.trim(lines[i]);
        line_cntr++;
      }
    }
    lines = (String[]) Utils.expand(lines, line_cntr);     /* cut away end of array */
    if (lines.length < 7)
    {
      clear();
      return "File contains too few lines.";
    }
    int[] transition_count = null;

    for (i=0; i<lines.length; i++)
    {
      switch (i)
      {
        case 0:  /* process LTL_property { */
          if (!lines[i].substring(0,8).equals("process ") ||
              lines[i].charAt(lines[i].length()-1) != '{')
          {
            clear();
            return "Bad process definition near "+lines[i];
          }
          break;
        case 1:  /* state q1, q2; */
          if (!lines[i].substring(0,6).equals("state ") ||
              !lines[i].endsWith(";"))
          {
            clear();
            return "Bad state definition near "+lines[i];
          }
          state_names =
            Utils.trim(Utils.split(lines[i].substring(6,lines[i].length()-1),','));
          for (int j=0; j<state_names.length; j++)
          {
            String[] reg_match = Utils.match(state_names[j],"[\\w]+");
            {
              if (reg_match.length != 1 || !reg_match[0].equals(state_names[j]))
              {
                return clear("Bad state name '"+state_names[j]+
                             "' must have form [\\w]+");
              }
            }
            if (state_indexes.containsKey(state_names[j]))
            {
              return clear("Duplicate state '"+state_names[j]+"' at '"+
                           lines[i]+"'");
            }
            state_indexes.put(state_names[j],new Integer(j));
          }
          transitions = new Transition[state_names.length][state_names.length];
          transition_count = new int[state_names.length];
          for (int j=0; j<state_names.length; j++)
          {
            transition_count[j] = 0;
          }
          break;
        case 2:  /* init q1, q3; */
          if (!lines[i].substring(0,5).equals("init ") ||
              !lines[i].endsWith(";"))
          {
            clear();
            return "Bad init definition near "+lines[i];
          }
          String[] init_states =
            Utils.trim(Utils.split(lines[i].substring(5,lines[i].length()-1),','));
          initial_states = new int[init_states.length];
          for (int j=0; j<init_states.length; j++)
          {
            if (!state_indexes.containsKey(init_states[j]))
            {
              return clear("Unknown initial state '"+init_states[j]+
                           "' at '"+lines[i]+"'");
            }
            initial_states[j] = ((Integer)state_indexes.get(init_states[j])).intValue();
          }
          break;
        case 3:  /* accept q2; */
          if (!lines[i].substring(0,7).equals("accept ") ||
              lines[i].charAt(lines[i].length()-1) != ';')
          {
            clear();
            return "Bad accept definition near "+lines[i];
          }
          String[] acc_states =
            Utils.trim(Utils.split(lines[i].substring(7,lines[i].length()-1),','));
          accepting_states = new int[acc_states.length];
          int acc_state;
          for (int j=0; j<acc_states.length; j++)
          {
            if (!state_indexes.containsKey(acc_states[j]))
            {
              clear();
              return "Unknown accepting state '"+acc_states[j]+
                     "' at '"+lines[i]+"'";
            }
            acc_state = ((Integer)state_indexes.get(acc_states[j])).intValue();
            for(int k=0; k<j; k++)
            {
              if (accepting_states[k] == acc_state)
              {
                clear();
                return "Duplicate accepting state definition '"+
                       acc_states[j]+"'";
              }
            }
            accepting_states[j] = acc_state;
          }
          break;
        case 4:  /* trans */
          if (!lines[i].equals("trans"))
          {
            clear();
            return "Bad trans definition near "+lines[i];
          }
          break;
        default:
          if (i == lines.length-1)
          {
            if(!lines[i].equals("}"))
            {
              clear();
              return "Bad last line bracket at "+lines[i];
            }
            continue;
          }
          /* q1 -> q2 { guard X>0.001 && Y<3.6; },
             q1 -> q1 {},
             q2 -> q2 {}; */
          err_str = parse_transition(lines[i], var_indexes,
                                     transition_count);
          break;
      }
      if (err_str != null)
      {
        clear();
        return err_str;
      }
    }
    for (i=0; i<transitions.length; i++)
    { /* trim the transition array to real sizes */
      transitions[i] =
        (Transition[]) Utils.expand(transitions[i], transition_count[i]);
    }

    ode = null;
    return null;
  }

  /**
   * Parses the token into a transition. On success returns null and
   * modifies the transition field and updates trans_count.
   *
   * @param token string to parse
   * @param var_indexes mapping od variable_name => variable_index
   * @param trans_count number of transitions from state i
   **/
  private String parse_transition(String token, HashMap var_indexes,
                                  int[] trans_count)
  {
    if (token.indexOf("->") == -1)
    {
      return "Missing '->' in transition "+token;
    }
    if (token.indexOf('{') == -1)
    {
      return "Missing '{' in transition "+token;
    }
    if (token.indexOf('}') == -1)
    {
      return "Missing '}' in transition "+token;
    }
    String state1 = Utils.trim(token.substring(0,token.indexOf("->")));
    String state2 = Utils.trim(token.substring(token.indexOf("->")+2,
                                         token.indexOf('{')));
    String guard_str = Utils.trim(token.substring(token.indexOf('{')+1,
                                         token.indexOf('}')));
    if (guard_str.length()>0)
    {
      if (guard_str.length()<6 || !guard_str.substring(0,6).equals("guard "))
      {
        return "Missing keyword 'guard' in transition '"+token+"'";
      }
      guard_str = Utils.trim(guard_str.substring(6));
    }
    if (guard_str.length()>0)
    {
      if (guard_str.charAt(guard_str.length()-1) == ';')
      {
        guard_str = Utils.trim(guard_str.substring(0,guard_str.length()-1));
      }
    }

    if (!state_indexes.containsKey(state1))
    {
      return "Unknown state '"+state1+"' in transition "+token;
    }
    if (!state_indexes.containsKey(state2))
    {
      return "Unknown state '"+state2+"' in transition "+token;
    }
    int state_index1 = ((Integer)state_indexes.get(state1)).intValue();
    int state_index2 = ((Integer)state_indexes.get(state2)).intValue();

    for (int i=0; i<trans_count[state_index1]; i++)
    {
      if (transitions[state_index1][i].to == state_index2)
      {
        return "Duplicit transition target '"+state2+"' at '"+token+"'";
      }
    }

    Evaluable guard = null;
    if (guard_str.length() > 0)
    {
      guard = parse_expression(guard_str, var_indexes);
    }
    if (err_str == null)
    {
      transitions[state_index1][trans_count[state_index1]] =
        new Transition(state_index1, state1, state_index2, state2, guard);
      trans_count[state_index1]++;
    }
    else
    {
      if (err_str != null)
      {
        return err_str;
      }
      else
      {
        return "Error during parsing guard '"+guard_str+"' in transition '"+
               token+"'";
      }
    }
    return null;
  }

  /**
   * Splits a group of expressions (including possible sub expressions with
   * brackets) with "&&" or "||" in between.
   *
   * @return on success array of subtokens, on failure null and modifies the
   *         err_str with error message.
   **/
  private String[] split_group(String token)
  {
    String[] parts = new String[token.length()];
    int part_count = 0;
    int part_start = 0;
    int level = 0;
    char c;
    int op_mode = 0;  /* 0 = none, 1 = &, 2 = | */
    for (int i=0; i<token.length(); i++)
    {
      c = token.charAt(i);
      if (c == '(')
      {
        level++;
      }
      else if (c == ')')
      {
        level--;
        if (level < 0)
        {
          err_str = "Bad bracet order near "+token;
          return null;
        }
      }
      else if (c == '&' && level == 0)
      {
        if (op_mode == 0)
        {
          op_mode = 1;
          parts[part_count] = Utils.trim(token.substring(part_start, i));
          part_count++;
          part_start = i;
        }
        else if (op_mode == 1)
        {
          parts[part_count] = "&&";
          part_count++;
          part_start = i+1;
          op_mode = 0;
        }
        else
        {
          err_str = "Bad '&' position near "+token;
          return null;
        }
      }
      else if (c == '|' && level == 0)
      {
        if (op_mode == 0)
        {
          op_mode = 2;
          parts[part_count] = Utils.trim(token.substring(part_start, i));
          part_count++;
          part_start = i;
        }
        else if (op_mode == 2)
        {
          parts[part_count] = "||";
          part_count++;
          part_start = i+1;
          op_mode = 0;
        }
        else
        {
          err_str = "Bad '|' position near "+token;
          return null;
        }
      }
      else if (op_mode != 0)
      {
        err_str = "Expected '"+((op_mode == 1)?'&':'|')+
                  "' near '"+c+"' in "+token;
        return null;
      }
    }
    if (level != 0)
    {
      err_str = "Bad bracet order near "+token;
      return null;
    }
    if (part_start < token.length())
    {
      parts[part_count] = Utils.trim(token.substring(part_start));
      part_count++;
    }

    return Utils.expand(parts, part_count);
  }

  /**
   * Expects token in any of these possible forms as described in
   * the file ltl_format.txt:
   * ATOM | not ATOM | (COMPLEX) | not (COMPLEX) | COMPLEX
   *
   * @return an evaluable object on success or null on failure and save
   *         error message to err_str
   **/
  private Evaluable parse_expression(String token, HashMap var_indexes)
  {
    token = Utils.trim(token);

    /* if token does not contain bracets and & or | it must
       be ATOM or not ATOM */
    if (token.indexOf('(') == -1 &&
        token.indexOf(')') == -1 &&
        token.indexOf('&') == -1 &&
        token.indexOf('|') == -1)
    {
      /* detect possible negation */
      if (token.length() > 4 && token.substring(0,4).equals("not "))
      {
        Evaluable inner =
          parse_atomic_proposition(token.substring(4), var_indexes);
        if (err_str == null)
        {
          return new Negation(inner);
        }
        else
        {
          return null;
        }
      }
      else
      {
        return parse_atomic_proposition(token, var_indexes);
      }
    }
    else /* token must be 'COMPLEX', '(COMPLEX)' or 'not (COMPLEX)' */
    {
      String[] parts = split_group(token);

      if (err_str != null || parts == null || parts.length == 0)
      {
        if (err_str == null)
        {
          err_str = "Bad token format at '"+token+"'";
        }
        return null;
      }
      else if (parts.length == 1)
      { /* token must be '(COMPLEX)' or 'not (COMPLEX)' */
        String part = parts[0];
        if (part.length() > 4 && part.substring(0,4).equals("not "))
        {
          Evaluable inner =
            parse_expression(part.substring(4), var_indexes);
          if (err_str == null)
          {
            return new Negation(inner);
          }
          else
          {
            return null;
          }
        }
        else if (part.charAt(0) == '(' &&
                 part.charAt(part.length()-1) == ')')
        {
          /* strip left and right brackets and whitespace */
          part = Utils.trim(part.substring(1,part.length()-1));
          return parse_expression(part, var_indexes);
        }
        else
        {
          err_str = "Bad token format '"+part+
                    "'' near '"+token+"' expected 'not'";
          return null;
        }
      }
      else /* token was 'COMPLEX' */
      {
        /* every second part should be equal to && or || */
        if ((parts.length % 2) != 1)
        {
          err_str = "Bad token format in '"+token+"'";
          return null;
        }
        String op = parts[1];
        if (!op.equals("&&") && !op.equals("||"))
        {
          err_str = "Bad operator '"+op+"' in '"+token+"'";
          return null;
        }
        for (int i=1; i<parts.length; i+=2)
        {
          if (!parts[i].equals(op))
          {
            err_str = "Different operator '"+parts[i]+"' in '"+token+
                      "' expected '"+op+"'";
            return null;
          }
        }
        int op_type = Utils.AP_GROUP_AND;
        if (op.equals("||"))
        {
          op_type = Utils.AP_GROUP_OR;
        }
        Evaluable[] sub_expr = new Evaluable[(parts.length+1)/2];
        for (int i=0; i<parts.length; i+=2)
        {
          sub_expr[i/2] = parse_expression(parts[i], var_indexes);
          if (err_str != null)
          {
            return null;
          }
        }
        return new AtomicPropositionGroup(sub_expr, op_type);
      }
    }
  }


  private Evaluable parse_atomic_proposition(String token, HashMap var_indexes)
  {
    token = Utils.trim(token);
    if (token.length() == 0)
    {
      err_str = "Empty proposition";
      return null;
    }
    int op = -1;
    int op_start = -1;
    int op_len = 2;
    if (token.indexOf("!=") != -1)
    {
      op = Utils.AP_NOT_EQUAL;
      op_start = token.indexOf("!=");
    }
    else if (token.indexOf("==") != -1)
    {
      op = Utils.AP_EQUAL;
      op_start = token.indexOf("==");
    }
    else if (token.indexOf("<=") != -1)
    {
      op = Utils.AP_LESS_EQUAL;
      op_start = token.indexOf("<=");
    }
    else if (token.indexOf(">=") != -1)
    {
      op = Utils.AP_GREATER_EQUAL;
      op_start = token.indexOf(">=");
    }
    else if (token.indexOf("<") != -1)
    {
      op = Utils.AP_LESS;
      op_start = token.indexOf("<");
      op_len = 1;
    }
    else if (token.indexOf(">") != -1)
    {
      op = Utils.AP_GREATER;
      op_start = token.indexOf(">");
      op_len = 1;
    }
    else
    {
      err_str = "Unknown operation in atomic proposition '"+token+"'";
      return null;
    }

    String str1 = Utils.trim(token.substring(0,op_start));
    String str2 = Utils.trim(token.substring(op_start+op_len));

    int var1_index;
    int var1_type = -1; /* type of first operand */

    if (var_indexes.containsKey(str1))
    {
      var1_index = ((Integer)var_indexes.get(str1)).intValue();
      var1_type = Utils.AP_OPERAND_TYPE_VAR;
    }
    else if (str1.length() > 1 && str1.charAt(0) == 'd' &&
             var_indexes.containsKey(str1.substring(1)))
    {
      var1_index = ((Integer)var_indexes.get(str1.substring(1))).intValue();
      var1_type = Utils.AP_OPERAND_TYPE_DVAR;
    }
    else
    {
      err_str = "Bad type of first operand '"+str1+"' in '"+token+
                "' expected VAR or dVAR";
      return null;
    }

    int var2_index = -1;
    float var2_val = 0.0f;
    int var2_type = -1; /* type of second operand */

    if (var_indexes.containsKey(str2))
    {
      var2_index = ((Integer)var_indexes.get(str2)).intValue();
      var2_type = Utils.AP_OPERAND_TYPE_VAR;
    }
    else if (str2.length() > 1 && str2.charAt(0) == 'd' &&
             var_indexes.containsKey(str2.substring(1)))
    {
      var2_index = ((Integer)var_indexes.get(str2.substring(1))).intValue();
      var2_type = Utils.AP_OPERAND_TYPE_DVAR;
    }
    else if (!Float.isNaN(Float.valueOf(str2).floatValue()))
    {
      var2_val = Float.valueOf(str2).floatValue();
      var2_type = Utils.AP_OPERAND_TYPE_NUM;
    }
    else
    {
      err_str = "Bad type of second operand '"+str2+"' in '"+token+
                "' expected "+
                ((var1_type == Utils.AP_OPERAND_TYPE_VAR)?"NUM or VAR":"NUM or DVAR");
      return null;
    }

    if (var1_type == Utils.AP_OPERAND_TYPE_VAR &&
        var2_type == Utils.AP_OPERAND_TYPE_NUM)
    {
      return new AtomicPropositionVN(var1_index, str1, op, var2_val);
    }
    else if (var1_type == Utils.AP_OPERAND_TYPE_VAR &&
             var2_type == Utils.AP_OPERAND_TYPE_VAR)
    {
      return new AtomicPropositionVV(var1_index, str1, op, var2_index, str2);
    }
    else if (var1_type == Utils.AP_OPERAND_TYPE_DVAR &&
             var2_type == Utils.AP_OPERAND_TYPE_DVAR &&
             var2_type == Utils.AP_OPERAND_TYPE_DVAR)
    {
      return new AtomicPropositionDD(var1_index, str1, op,
                                     var2_index, str2, ode);
    }
    else
    {
      err_str = "Combination of operands not permited in '"+token+"'";
      return null;
    }
  }

  int get_state_count()
  {
    return state_count;
  }

  String[] get_state_names()
  {
    return state_names;
  }

  HashMap get_state_indexes()
  {
    return state_indexes;
  }

  int[] get_initial_states()
  {
    return initial_states;
  }

  int[] get_accepting_states()
  {
    return accepting_states;
  }

  Transition[][] get_transitions()
  {
    return transitions;
  }

  boolean get_has_derivatives()
  {
    return has_derivatives;
  }
}
