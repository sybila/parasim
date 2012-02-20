package org.sybila.parasim.model.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * Object which can be transformed into XML.
 * 
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 *
 */
public interface XMLRepresentable {
	
	/**
	 * Transforms this instance into XML.
	 * @param doc Root document of the new XML element.
	 * @return XML element representing this instance.
	 */
	public Element toXML(Document doc);
}
