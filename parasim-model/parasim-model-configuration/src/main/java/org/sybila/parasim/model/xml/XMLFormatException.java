package org.sybila.parasim.model.xml;

/**
 * Exception during XML parsing: the XML input was of wrong format and could not be parsed properly.
 * 
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 *
 *	@see XMLRepresentableFactory
 */
public class XMLFormatException extends XMLException {

	public XMLFormatException(String message) {
		super(message);
	}
	
	public XMLFormatException(String message, Throwable cause) {
		super(message, cause);
	}
}
