package org.sybila.parasim.model.verification.buchi;

import java.util.Set;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.ArrayList;
import org.sybila.parasim.model.ode.OdeSystem;

/**
 * Enables parsing of an Buchi automaton with guards containing linear
 * inequalities over variables of an OdeSystem. After an successful parsing
 * of a file all the componens can be obtained by the appropriate getter methods.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>

 Buchi file format
 =================
 Restricted version of the DVE syntax for propery processes, restrictions:

 One property process per file.
 One transition per line.
 Transitions may only have guards (synchronization and effects forbidden).
 Guard expressions must follow restricted grammar of DVE Expr:

 guard EXPR; | guard COMPLEX;
 EXPR     := ATOM | not ATOM | (COMPLEX) | not (COMPLEX)
 ATOM     := VAR OP NUM | VAR OP VAR | dVAR OP NUM | dVAR OP dVAR
 VAR      := variable name (case sensitive)
 NUM      := number (floating point or int)
 OP       := < | >
 COMPLEX  := ANDGROUP | ORGROUP
 ANDGROUP := EXPR | EXPR && ANDGROUP
 ORGROUP  := EXPR | EXPR || ORGROUP
 
 Note: The grammar above generates expressions with explicit braceting of
       all subexpressions with only exceptions of multiple conjuctions
       and disjunctions.

 Exactly one initial state, at least one accepting state or more,
 at most one transition from qx -> qy.

 Example

    process LTL_property {
    state q1, q2;
    init q1;
    accept q2;
    trans
    q1 -> q2 { guard X>0.001 && Y<3.6; },
    q1 -> q1 {},
    q2 -> q2 {};
    }
 */

class BuchiParser {

    private int stateCount;               /* number of states of automaton */
    private String[] stateNames;          /* state's names */
    private HashMap stateIndexes;         /* mapping of state_name => index */
    private Set<Integer> initialStates;   /* initial states of atomaton */
    private Set<Integer> acceptingStates; /* set of accepting states */
    private Transition[][] transitions;   /* transitions of each state */

    private String error;       /* passing error messages from methods */
    private boolean hasDerivatives;
                                /* flag to indicate if variable derivatives
                                   are present in atomic propositions and
                                   therefore must be updated before calling
                                   transition.enabled() */
    private boolean hasTautologyGuards;

    BuchiParser()
    {
        stateIndexes = new HashMap();
        initialStates = new TreeSet<Integer>();
        acceptingStates = new TreeSet<Integer>();
        clear(null);
    }

    private boolean clear(String error)
    {
        stateCount = 0;
        stateNames = null;
        stateIndexes.clear();
        initialStates.clear();
        acceptingStates.clear();
        transitions = null;        
        hasDerivatives = false;
        hasTautologyGuards = false;
        this.error = error;
        return false;
    }

    String getError()
    {
        return error;
    }


    /**
     * Parses an Buchi file in DVE format as restricted in ltl_format.txt.
     *
     * @param fileName file to parse
     * @param ode OdeSystem to get variable names and indexes
     */
    boolean parseFile(String fileName, OdeSystem ode)
    {        
        clear(null);

        HashMap varIndexes = new HashMap();
        for (int i=0; i<ode.getDimension(); i++)
        {
            varIndexes.put(ode.getVariableName(i), new Integer(i));
        }

        ArrayList<String> lines;
        LineReader reader = new LineReader();
        if (reader.readLines(fileName))
        {
            lines = reader.getLinesList();
        }
        else
        {
            error = reader.getError();            
            return false;
        }

        for (Iterator<String> it = lines.listIterator(); it.hasNext(); )
        {
            String line = it.next();
            if (line.length() > 0 && line.charAt(0) == '#')
            {
                it.remove();
            }
        }
        
        if (lines.size() < 7)
        {
            return clear("File contains too few lines.");
        }
        
        int[] transitionCount = null;

        for (int i=0; i<lines.size(); i++)
        {
            String line = lines.get(i);
            switch (i)
            {
                case 0:  /* process LTL_property { */
                    if (!line.substring(0,8).equals("process ") ||
                         line.charAt(line.length()-1) != '{')
                    {
                        return clear("Bad process definition near "+line);
                    }
                    break;

                case 1:  /* state q1, q2; */
                    if (!line.substring(0,6).equals("state ") ||
                        !line.endsWith(";"))
                    {
                        return clear("Bad state definition near "+line);
                    }
                    stateNames =
                    line.substring(6,line.length()-1).split(",");
                    for (int j = 0; j<stateNames.length; j++)
                    {
                        stateNames[j] = stateNames[j].trim();
                        if (!stateNames[j].matches("[\\w]+"))
                        {                            
                            return clear("Bad state name '"+stateNames[j]+
                                         "' must have form [\\w]+");
                        }

                        if (stateIndexes.containsKey(stateNames[j]))
                        {
                            return clear("Duplicate state '"+stateNames[j]+
                                         "' at '"+line+"'");
                        }
                        stateIndexes.put(stateNames[j],new Integer(j));
                    }                                                                                                            
          
                    transitions = new Transition[stateNames.length][stateNames.length];
                    transitionCount = new int[stateNames.length];
                    break;
                case 2:  /* init q1, q3; */
                    if (!line.substring(0,5).equals("init ") ||
                        !line.endsWith(";"))
                    {
                        return clear("Bad init definition near "+line);
                    }
                    String[] initStates = line.substring(5,line.length()-1).split(",");
                    for (int j=0; j<initStates.length; j++)
                    {
                        initStates[j] = initStates[j].trim();
                        if (!stateIndexes.containsKey(initStates[j]))
                        {                            
                            return clear("Unknown initial state '"+initStates[j]+
                                         "' at '"+line+"'");
                        }
                        Integer stateIndex = (Integer)stateIndexes.get(initStates[j]);
                        if (initialStates.contains(stateIndex))
                        {
                            return clear("Duplicite initial state '"+initStates[j]+
                                    "' at '"+line+"'");
                        }
                        initialStates.add((Integer)stateIndexes.get(initStates[j]));
                    }                                         
                    break;
                case 3:  /* accept q2; */
                    if (!line.substring(0,7).equals("accept ") ||
                        line.charAt(line.length()-1) != ';')
                    {
                        return clear("Bad accept definition near "+line);
                    }
                    String[] accStates = line.substring(7,line.length()-1).split(",");                    
                    for (int j=0; j<accStates.length; j++)
                    {
                        accStates[j] = accStates[j].trim();
                        if (!stateIndexes.containsKey(accStates[j]))
                        {
                            return clear("Unknown accepting state '"+accStates[j]+
                                         "' at '"+line+"'");
                        }
                        Integer stateIndex = (Integer)stateIndexes.get(accStates[j]);
                        if (acceptingStates.contains(stateIndex))
                        {
                            return clear("Duplicite accepting state '"+accStates[j]+
                                         "' at '"+line+"'");
                        }
                        acceptingStates.add((Integer)stateIndexes.get(accStates[j]));
                        break;
                    }
                case 4:  /* trans */
                    if (!line.equals("trans"))
                    {
                        return clear("Bad trans definition near "+line);
                    }
                    break;
                default:
                    if (i == lines.size()-1)
                    {
                        if(!line.equals("}"))
                        {
                            return clear("Bad last line bracket at "+line);
                        }                       
                    }
                    else
                    {   /* q1 -> q2 { guard X>0.001 && Y<3.6; },
                           q1 -> q1 {},
                           q2 -> q2 {}; */
                        String tmp = parseTransition(line, varIndexes, transitionCount, ode);
                        if (tmp != null)
                        {
                            return clear(tmp);
                        }
                    }
                    break;
            }            
        }
        for (int i=0; i<transitions.length; i++)
        { /* trim the transition array to real sizes */
            Transition[] tmp = new Transition[transitionCount[i]];
            System.arraycopy(transitions[i], 0, tmp, 0, transitionCount[i]);
            transitions[i] = tmp;
        }
        return true;
    }

    /**
     * Parses the token into a transition. On success returns null and
     * modifies the transition field and updates transCount.
     *
     * @param token string to parse
     * @param varIndexes mapping od variable_name => variable_index
     * @param transCount number of transitions from state i
     * @param ode OdeSystem for variable names and indexes
     * @return null on success, error message on failure
     */
    private String parseTransition(String token, HashMap varIndexes, int[] transCount, OdeSystem ode)
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
        String state1 = token.substring(0,token.indexOf("->")).trim();
        String state2 = token.substring(token.indexOf("->")+2, token.indexOf('{')).trim();
        String guardStr = token.substring(token.indexOf('{')+1, token.indexOf('}')).trim();
        if (guardStr.length()>0)
        {
            if (guardStr.length()<6 || !guardStr.substring(0,6).equals("guard "))
            {
                return "Missing keyword 'guard' in transition '"+token+"'";
            }
            guardStr = guardStr.substring(6).trim();
        }
        if (guardStr.length()>0)
        {
            if (guardStr.charAt(guardStr.length()-1) == ';')
            {
                guardStr = guardStr.substring(0,guardStr.length()-1).trim();
            }
        }

        if (!stateIndexes.containsKey(state1))
        {
            return "Unknown state '"+state1+"' in transition "+token;
        }
        if (!stateIndexes.containsKey(state2))
        {
            return "Unknown state '"+state2+"' in transition "+token;
        }
        int stateIndex1 = ((Integer)stateIndexes.get(state1)).intValue();
        int stateIndex2 = ((Integer)stateIndexes.get(state2)).intValue();

        for (int i=0; i<transCount[stateIndex1]; i++)
        {
            if (transitions[stateIndex1][i].getTo() == stateIndex2)
            {
                return "Duplicit transition target '"+state2+"' at '"+token+"'";
            }
        }

        Evaluable guard = null;
        if (guardStr.length() > 0)
        {
            guard = parseExpression(guardStr, varIndexes, ode);
            if (guard == null)
            {
                return error;
            }
        }

        if (guard == null)
        {
            hasTautologyGuards = true;
        }

        transitions[stateIndex1][transCount[stateIndex1]] =
          new Transition(stateIndex1, stateIndex2, guard);
        transCount[stateIndex1]++;
        
        return null;
    }

    /**
     * Expects token in any of these possible forms as described in
     * the file ltl_format.txt:
     * ATOM | not ATOM | (COMPLEX) | not (COMPLEX) | COMPLEX
     *
     * @param token string to parse expression from
     * @param varIndexes HashMap of variable_name => variable_index pairs
     * @param ode OdeSystem to get variable names and indexes
     * @return an evaluable object on success or null on failure and save
     *         error message to error
     */
    private Evaluable parseExpression(String token, HashMap varIndexes, OdeSystem ode)
    {
        token = token.trim();
        /* if token does not contain bracets and & or | it must
           be ATOM or not ATOM */
        if (token.indexOf('(') == -1 &&
            token.indexOf(')') == -1 &&
            token.indexOf('&') == -1 &&
            token.indexOf('|') == -1)
        { /* detect possible negation */
            if (token.length() > 4 && token.substring(0,4).equals("not "))
            {
                Evaluable inner = parseAtomicProposition(token.substring(4), varIndexes, ode);
                if (error == null)
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
                return parseAtomicProposition(token, varIndexes, ode);
            }
        }
        else /* token must be 'COMPLEX', '(COMPLEX)' or 'not (COMPLEX)' */
        {
            String[] parts = splitGroup(token);
            if (error != null || parts == null || parts.length == 0)
            {
                if (error == null)
                {
                    error = "Bad token format at '"+token+"'";
                }
                return null;
            }
            else if (parts.length == 1)
            { /* token must be '(COMPLEX)' or 'not (COMPLEX)' */
                String part = parts[0];
                if (part.length() > 4 && part.substring(0,4).equals("not "))
                {
                    Evaluable inner = parseExpression(part.substring(4), varIndexes, ode);
                    if (error == null)
                    {
                        return new Negation(inner);
                    }
                    else
                    {
                        return null;
                    }
                }
                else if (part.charAt(0) == '(' && part.charAt(part.length()-1) == ')')
                {   /* strip left and right brackets and whitespace */
                    part = part.substring(1,part.length()-1).trim();
                    return parseExpression(part, varIndexes, ode);
                }
                else
                {
                    error = "Bad token format '"+part+
                            "'' near '"+token+"' expected 'not'";
                    return null;
                }
            }
            else /* token was 'COMPLEX' */
            {
                /* every second part should be equal to && or || */
                if ((parts.length % 2) != 1)
                {
                    error = "Bad token format in '"+token+"'";
                    return null;
                }
                String op = parts[1];
                if (!op.equals("&&") && !op.equals("||"))
                {
                    error = "Bad operator '"+op+"' in '"+token+"'";
                    return null;
                }
                for (int i=1; i<parts.length; i+=2)
                {
                    if (!parts[i].equals(op))
                    {
                        error = "Different operator '"+parts[i]+"' in '"+token+
                                "' expected '"+op+"'";
                        return null;
                    }
                }
                EvaluableGroupType groupType = EvaluableGroupType.AP_GROUP_AND;
                if (op.equals("||"))
                {
                    groupType = EvaluableGroupType.AP_GROUP_OR;
                }
                Evaluable[] subExpr = new Evaluable[(parts.length+1)/2];
                for (int i=0; i<parts.length; i+=2)
                {
                    subExpr[i/2] = parseExpression(parts[i], varIndexes, ode);
                    if (error != null)
                    {
                        return null;
                    }
                }
                return new EvaluableGroup(subExpr, groupType);
            }
        }
    }

    /**
     * Splits a group of expressions (including possible sub expressions with
     * brackets) with "&&" or "||" in between.
     *
     * @param token string to split and parse
     * @return on success array of subtokens, on failure null and modifies the
     *         error with error message.
     */
    private String[] splitGroup(String token)
    {
        String[] parts = new String[token.length()];
        int partCount = 0;
        int partStart = 0;
        int level = 0;
        char c;
        int opMode = 0;  /* 0 = none, 1 = &, 2 = | */
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
                    error = "Bad bracet order near "+token;
                    return null;
                }
            }
            else if (c == '&' && level == 0)
            {
                if (opMode == 0)
                {
                    opMode = 1;
                    parts[partCount] = token.substring(partStart, i).trim();
                    partCount++;
                    partStart = i;
                }
                else if (opMode == 1)
                {
                    parts[partCount] = "&&";
                    partCount++;
                    partStart = i+1;
                    opMode = 0;
                }
                else
                {
                    error = "Bad '&' position near "+token;
                    return null;
                }
            }
            else if (c == '|' && level == 0)
            {
                if (opMode == 0)
                {
                    opMode = 2;
                    parts[partCount] = token.substring(partStart, i).trim();
                    partCount++;
                    partStart = i;
                }
                else if (opMode == 2)
                {
                    parts[partCount] = "||";
                    partCount++;
                    partStart = i+1;
                    opMode = 0;
                }
                else
                {
                    error = "Bad '|' position near "+token;
                    return null;
                }
            }
            else if (opMode != 0)
            {
                error = "Expected '"+((opMode == 1)?'&':'|')+
                        "' near '"+c+"' in "+token;
                return null;
            }
        }
        if (level != 0)
        {
            error = "Bad bracet order near "+token;
            return null;
        }
        if (partStart < token.length())
        {
            parts[partCount] = token.substring(partStart).trim();
            partCount++;
        }

        String[] tmp = new String[partCount];
        System.arraycopy(parts, 0, tmp, 0, partCount);
        return tmp;
    }

    private Evaluable parseAtomicProposition(String token, HashMap varIndexes, OdeSystem ode)
    {
        token.trim();
        if (token.length() == 0)
        {
            error = "Empty proposition";
            return null;
        }
        AtomicPropOperator operator;
        int operatorStart = -1;
        int operatorLength = 1;
        if (token.indexOf("<") != -1)
        {
            operator = AtomicPropOperator.AP_LESS;
            operatorStart = token.indexOf("<");
            operatorLength = 1;
        }
        else if (token.indexOf(">") != -1)
        {
            operator = AtomicPropOperator.AP_GREATER;
            operatorStart = token.indexOf(">");
            operatorLength = 1;
        }
        else
        {
            error = "Unknown operation in atomic proposition '"+token+"'";
            return null;
        }

        String str1 = token.substring(0, operatorStart).trim();
        String str2 = token.substring(operatorStart + operatorLength).trim();
        
        AtomicPropOperand operand1; /* first operand */

        if (varIndexes.containsKey(str1))
        {
            int varIndex = ((Integer)varIndexes.get(str1)).intValue();
            operand1 = new Variable(ode, varIndex);
        }
        else if (str1.length() > 1 && str1.charAt(0) == 'd' &&
                 varIndexes.containsKey(str1.substring(1)))
        {
            int varIndex = ((Integer)varIndexes.get(str1.substring(1))).intValue();
            operand1 = new VariableDerivative(ode, varIndex);
            hasDerivatives = true;
        }
        else
        {
            error = "Bad type of first operand '"+str1+"' in '"+token+
                    "' expected VAR or dVAR";
            return null;
        }
  
        AtomicPropOperand operand2; /* second operand */

        if (varIndexes.containsKey(str2))
        {
            int varIndex = ((Integer)varIndexes.get(str2)).intValue();
            operand2 = new Variable(ode, varIndex);
        }
        else if (str2.length() > 1 && str2.charAt(0) == 'd' &&
                 varIndexes.containsKey(str2.substring(1)))
        {
            int varIndex = ((Integer)varIndexes.get(str2.substring(1))).intValue();
            operand2 = new VariableDerivative(ode, varIndex);
            hasDerivatives = true;
        }
        else if (!Float.isNaN(Float.valueOf(str2).floatValue()))
        {
            float value = Float.valueOf(str2).floatValue();
            operand2 = new Constant(value);
        }
        else
        {
            error = "Bad type of second operand '"+str2+"' in '"+token+
                    "' expected "+
                ((operand1.getClass() == Variable.class)?"NUM or VAR":"NUM or DVAR");
            return null;
        }


        if ( ( operand1.getClass() == Variable.class &&
               (operand2.getClass() == Variable.class ||
                operand2.getClass() == Constant.class)
             ) ||
             ( operand1.getClass() == VariableDerivative.class &&
               (operand2.getClass() == VariableDerivative.class || 
                operand2.getClass() == Constant.class)
             ) 
           )
        {
            return new AtomicProposition(operand1, operator, operand2);
        }
        else
        {
            error = "Combination of operands not permited in '"+token+"'";
            return null;
        }
    }

  int getStateCount()
  {
    return stateCount;
  }

  String[] getStateNames()
  {
    return stateNames;
  }

  HashMap getStateIndexes()
  {
    return stateIndexes;
  }

  Set<Integer> getInitialStates()
  {
    return initialStates;
  }

  Set<Integer> getAcceptingStates()
  {
    return acceptingStates;
  }

  Transition[][] getTransitions()
  {
    return transitions;
  }

  boolean getHasDerivatives()
  {
    return hasDerivatives;
  }

  boolean getHasTautologyGuards()
  {
    return hasTautologyGuards;
  }
}
