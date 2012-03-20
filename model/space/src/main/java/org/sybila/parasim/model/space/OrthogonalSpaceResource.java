package org.sybila.parasim.model.space;

import java.io.File;
import java.net.URL;

import org.sybila.parasim.model.xml.FileXMLResource;
import org.sybila.parasim.model.xml.XMLRepresentableFactory;

public class OrthogonalSpaceResource extends FileXMLResource<OrthogonalSpace> {
    
    public OrthogonalSpaceResource(File file) {
        super(file);
    }

    @Override
    protected XMLRepresentableFactory<OrthogonalSpace> getFactory() {
        return new OrthogonalSpaceFactory();
    }

    @Override
    protected URL getXMLSchema() {
        return getClass().getClassLoader().getResource("space.xsd");
    }

    @Override
    protected String getNamespace() {
        return "http://www.sybila.org/parasim/space";
    }

}
