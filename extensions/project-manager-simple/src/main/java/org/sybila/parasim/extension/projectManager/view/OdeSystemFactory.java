package org.sybila.parasim.extension.projectManager.view;

import java.io.File;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.sbml.SBMLOdeSystemFactory;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
@Deprecated
public enum OdeSystemFactory {

    INSTANCE;
    private OdeSystem system = null;

    @SuppressWarnings("UseSpecificCatch")
    private OdeSystemFactory() {
        try {
            system = SBMLOdeSystemFactory.fromFile(new File(getClass().getClassLoader().getResource("SIR.sbml").toURI()));
        } catch (Exception e) {
            System.out.println("Could not load testing ode system.");
            System.exit(1);
        }
    }

    public OdeSystem getTestingOdeSystem() {
        return system;
    }

    public static OdeSystemFactory getInstance() {
        return INSTANCE;
    }
}
