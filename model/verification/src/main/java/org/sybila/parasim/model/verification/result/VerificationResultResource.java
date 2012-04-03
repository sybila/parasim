package org.sybila.parasim.model.verification.result;

import java.io.File;
import java.net.URL;
import org.sybila.parasim.model.xml.FileXMLResource;
import org.sybila.parasim.model.xml.XMLRepresentableFactory;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class VerificationResultResource extends FileXMLResource<VerificationResult> {

    public VerificationResultResource(File file) {
        super(file);
    }
    
    @Override
    protected URL getXMLSchema() {
        return getClass().getClassLoader().getResource("verification_result.xsd");
    }
    
    @Override
    protected String getNamespace() {
        return "http://www.sybila.org/parasim/verification-result";
    }

    @Override
    protected XMLRepresentableFactory<VerificationResult> getFactory() {
        return new VerificationResultFactory();
    }
    
}
