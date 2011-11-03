package org.sybila.parasim.model.verification.buchi;

import org.sybila.parasim.model.trajectory.Point;

/**
 * Represents a conjunction or disjunction of a group of evaluable subexpressions.
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class EvaluableGroup<P extends Point> implements Evaluable<P>
{
    private EvaluableGroupType type;
    private Evaluable[] subExpressions;

    public EvaluableGroup(Evaluable[] subExpressions, EvaluableGroupType type)
    {
        this.type = type;
        this.subExpressions = subExpressions;
    }

    /**
     * Evaluates the degree of satisfaction on the group.
     * If it is a conjunction the minimum value over all values is returned.
     * If it is a disjunction the maximum is returned.
     * @param point Point in which to evaluate subexpressions.
     * @return Overall value.
     */
    public float evaluate(P point)
    {
        if (subExpressions == null || subExpressions.length == 0)
        {
            return 0;
        }
        
        float result = subExpressions[0].evaluate(point);
        float tmp;
        if (type == EvaluableGroupType.AP_GROUP_AND) /* Minimum */
        {
            for (int i=1; i<subExpressions.length; i++)
            {
                tmp = subExpressions[i].evaluate(point);
                if (tmp < result) 
                {
                    result = tmp;
                }
            }                    
        }
        else /* type == AP_GROUP_OR => Maximum */
        {
            for (int i=1; i<subExpressions.length; i++)
            {
                tmp = subExpressions[i].evaluate(point);
                if (tmp > result)                  
                {
                    result = tmp;
                }
            }            
        }
        return result;
    }

    /**
     * Evaluates the valididty of the group.
     * @param point Point in which to compute validity of subexpressions.
     * @return Validity of the whole expression.
     */
    public boolean valid(P point)
    {
        if (type == EvaluableGroupType.AP_GROUP_AND)
        {
          for (int i=0; i<subExpressions.length; i++)
          {
            if (!subExpressions[i].valid(point))
            {
              return false;
            }
          }
          return true;
        }
        else /* type == AP_GROUP_OR */
        {
          for (int i=0; i<subExpressions.length; i++)
          {
            if (!subExpressions[i].valid(point))
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
        String out = new String();
        for (int i=0; i<subExpressions.length; i++)
        {
          if (i>0) out += type.toString();
          out += subExpressions[i].toString();
        }
        return '(' + out + ')';
    }
}
