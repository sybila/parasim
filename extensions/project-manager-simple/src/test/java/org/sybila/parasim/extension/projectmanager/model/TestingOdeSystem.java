package org.sybila.parasim.extension.projectmanager.model;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.sbml.SBMLOdeSystemFactory;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TestingOdeSystem {

    private TestingOdeSystem() {
    }

    public static OdeSystem get() throws IOException, URISyntaxException {
        return SBMLOdeSystemFactory.fromFile(new File(TestingOdeSystem.class.getClassLoader().getResource("SIR.sbml").toURI()));
    }
}
