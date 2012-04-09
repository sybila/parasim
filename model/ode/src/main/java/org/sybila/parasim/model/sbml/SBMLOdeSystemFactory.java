package org.sybila.parasim.model.sbml;

import java.io.File;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLReader;
import org.sybila.parasim.model.ode.OdeSystem;

/**
 * @author <a href="mailto:jpapouse@fi.muni.cz">Jan Papousek</a>
 */
public class SBMLOdeSystemFactory {

    public static OdeSystem fromFile(File file) throws IOException {
        Model model = modelFromFile(file);
        return new SBMLOdeSystem(model);
    }

    protected static Model modelFromFile(File file) throws IOException {
        try {
            return SBMLReader.read(file).getModel();
        } catch(XMLStreamException e) {
            throw new IOException(e);
        }
    }

}
