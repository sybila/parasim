package org.sybila.parasim.model.verification.stl;

import java.util.Locale;

import org.sybila.parasim.model.xml.XMLRepresentable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Structural type of a STL formula.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public enum FormulaType implements XMLRepresentable {

    PREDICATE, AND, OR, NOT, UNTIL, FUTURE, GLOBALLY;
    private static final FormulaType[] values = {PREDICATE, AND, OR, NOT, UNTIL, FUTURE, GLOBALLY};

    FormulaType fromInt(int value) {
        if (value < 0 || value >= values.length) {
            throw new IllegalArgumentException("Unknown ordinal value of type.");
        }
        return values[value];
    }

    @Override
    public Element toXML(Document doc) {
        return doc.createElement(toString().toLowerCase(Locale.ENGLISH));
    }
}
