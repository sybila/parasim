package org.sybila.parasim.extension.projectmanager.model;

import org.sybila.parasim.model.ode.OdeSystem;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class OdeInsideFactory {

    private OdeSystem system;

    protected OdeInsideFactory(OdeSystem odeSystem) {
        if (odeSystem == null) {
            throw new IllegalArgumentException("Argumetn (ode system) is null.");
        }

        system = OdeUtils.substituteAll(odeSystem);
    }

    protected OdeSystem getOdeSystem() {
        return system;
    }
}
