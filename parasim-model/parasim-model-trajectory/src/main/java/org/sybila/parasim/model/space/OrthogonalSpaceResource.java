package org.sybila.parasim.model.space;

import java.net.URL;

import org.sybila.parasim.model.xml.FileXMLResource;
import org.sybila.parasim.model.xml.XMLRepresentableFactory;

public class OrthogonalSpaceResource extends FileXMLResource<OrthogonalSpace> {

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
