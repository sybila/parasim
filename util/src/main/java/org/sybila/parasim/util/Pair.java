package org.sybila.parasim.util;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class Pair<S, T> {

    private S fst;
    private T snd;

    public Pair(S first, T second) {
        fst = first;
        snd = second;
    }

    public S first() {
        return fst;
    }

    public T second() {
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
