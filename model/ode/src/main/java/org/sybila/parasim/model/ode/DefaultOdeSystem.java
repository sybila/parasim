package org.sybila.parasim.model.ode;

import java.util.List;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class DefaultOdeSystem extends AbstractOdeSystem {

    private OdeSystemEncoding encoding;
    private List<Variable> variables;

    public DefaultOdeSystem(OdeSystemEncoding encoding, List<Variable> variables) {
        if (encoding == null) {
            throw new IllegalArgumentException("The parameter encoding is null.");
        }
        this.encoding = encoding;
        this.variables = variables;
    }

    public DefaultOdeSystem(OdeSystemEncoding encoding) {
        this(encoding, null);
    }

    public int dimension() {
        return encoding.countVariables();
    }

    public OdeSystemEncoding encoding() {
        return encoding;
    }

    public Variable getVariable(int dimension) {
        if (variables == null) {
            throw new UnsupportedOperationException("Not supported yet.");
        } else {
            return variables.get(dimension);
        }
    }
}
