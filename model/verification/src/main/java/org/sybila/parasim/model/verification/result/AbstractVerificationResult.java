package org.sybila.parasim.model.verification.result;

import org.sybila.parasim.model.trajectory.Point;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Implements some auxiliary methods of {@link VerificationResult}
 * according with use of its interface methods.
 * 
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class AbstractVerificationResult implements VerificationResult {

    @Override
    public Element toXML(Document doc) {
        Element target = doc.createElement(VerificationResultFactory.RESULT_NAME);
        for (int i = 0; i < size(); i++) {
            target.appendChild(pointToXML(doc, i));
        }
        
        return target;
    }
    
    private Element pointToXML(Document doc, int index) {
        Point p = getPoint(index);
        float r = getRobustness(index);
        
        Element target = doc.createElement(VerificationResultFactory.POINT_NAME);
        for (int i = 0; i < p.getDimension(); i++) {
            Element dim = doc.createElement(VerificationResultFactory.DIMENSION_NAME);
            dim.appendChild(doc.createTextNode(Float.toString(p.getValue(i))));
            target.appendChild(dim);
        }
        Element rob = doc.createElement(VerificationResultFactory.ROBUSTNESS_NAME);
        rob.appendChild(doc.createTextNode(Float.toString(r)));
        target.appendChild(rob);
        
        return target;
    }
    
    
}
