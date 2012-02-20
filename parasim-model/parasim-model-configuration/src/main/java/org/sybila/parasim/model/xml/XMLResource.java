package org.sybila.parasim.model.xml;

/**
 * 
 * External resource of objects in XML format. Contains one object of a designated type.
 * 
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 *
 * @param <T> Type of object being stored.
 */
public interface XMLResource<T extends XMLRepresentable> {

	/**
	 * @return Contained object.
	 */
	public T getRoot();
	
	/**
	 * Sets new contained object.
	 * @param target new contained object.
	 */
	public void setRoot(T target);
	
	/**
	 * Stores contained object to XML.
	 * @throws XMLException when error during storing occurs.
	 */
	public void store() throws XMLException;
	
	/**
	 * Loads an object from XML and sets it as the new contained object.
	 * @throws XMLException when error during loading occurs.
	 */
	public void load() throws XMLException;
}
