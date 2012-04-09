package org.sybila.parasim.model.ode;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class OctaveOdeSystem {

    private OdeSystemEncoding encoding;
    private String octaveString;

    public OctaveOdeSystem(OdeSystemEncoding encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("The parameter encoding is null.");
        }
        this.encoding = encoding;
    }

    public String octaveName() {
        return "f";
    }

    public String octaveString() {
        if (octaveString == null) {
            StringBuilder builder = new StringBuilder();
            builder.append("function xdot = f(x, t) ");
            builder.append("xdot = zeros(").append(encoding.countVariables()).append(", 1);");
            for (int v = 0; v < encoding.countVariables(); v++) {
                builder.append("xdot(").append(v + 1).append(") = ");
                for (int c = 0; c < encoding.countCoefficients(v); c++) {
                    if (c != 0) {
                        builder.append(" + ");
                    }
                    builder.append(encoding.coefficient(v, c) >= 0 ? encoding.coefficient(v, c) : "(" + encoding.coefficient(v, c) + ")");
                    for (int f = 0; f < encoding.countFactors(v, c); f++) {
                        builder.append("*").append("x(").append(encoding.factor(v, c, f) + 1).append(")");
                    }
                }
                builder.append("; ");
            }
            builder.append("endfunction");
            octaveString = builder.toString();
        }
        return octaveString;
    }
}
