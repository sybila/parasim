package org.sybila.parasim.model.variables;

/**
 * Maps variable names to associated objects and vice versa.
 * 
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 * 
 * @param <K>
 *            Type of objects associated with variables.
 * 
 */
public interface VariableMapping<K> {

    /**
     * Returns objects for names.
     * 
     * @param variableName
     *            Name of a variable.
     * @return Object associated with given variable name or <code>null</code>
     *         when there is no object associated with given name.
     */
    public K getKey(String variableName);

    /**
     * Returns names for objects.
     * 
     * @param variableKey
     *            Object representing given variable.
     * @return Name of the variable or <code>null</code> when there is no name
     *         associated with the object.
     */
    public String getName(K variableKey);
}
