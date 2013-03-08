package org.sybila.parasim.computation.verification.stlstar.cpu;

import org.sybila.parasim.computation.verification.cpu.SimpleVerifier;
import org.sybila.parasim.model.verification.stl.Formula;

/**
 * Verifier which verifies formulae with respect to frozen-time values semantics
 * of STL.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class SimpleSTLStarVerifier extends SimpleVerifier<Formula> {

    /**
     * Create a frozen-time value semantics verifier.
     */
    public SimpleSTLStarVerifier() {
        super(STLStarMonitorFactory.getInstance());
    }
}
