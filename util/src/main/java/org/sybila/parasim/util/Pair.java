package org.sybila.parasim.util;

/**
 * Pair of objects of different type. Immutable (references to contained objects cannot be changed).
 *
 * Two pairs are equal if and only if both contained objects are equal (respectively).
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 * @param F type of first object.
 * @param S type of second object.
 */
public class Pair<F, S> {

    private F fst;
    private S snd;

    /**
     * Specifies both objects.
     * @param first First object.
     * @param second Second object.
     */
    public Pair(F first, S second) {
        fst = first;
        snd = second;
    }

    /**
     * Projection to first component.
     * @return First contained object.
     */
    public F first() {
        return fst;
    }

    /**
     * Projection to the second component.
     * @return Second contained object.
     */
    public S second() {
        return snd;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair)) {
            return false;
        }
        Pair<?, ?> target = (Pair<?, ?>) obj;
        return first().equals(target.first()) && second().equals(target.second());
    }

    @Override
    public int hashCode() {
        return first().hashCode() + 43 * second().hashCode();
    }
}
