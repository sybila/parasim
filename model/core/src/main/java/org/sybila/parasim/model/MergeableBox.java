package org.sybila.parasim.model;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class MergeableBox<T extends Mergeable<T>, L> implements Mergeable<T> {

    private final L load;

    public MergeableBox(L load) {
        if (load == null) {
            throw new IllegalArgumentException("The parameter [load] is null.");
        }
        this.load = load;
    }

    public L get() {
        return load;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MergeableBox<T, L> other = (MergeableBox<T, L>) obj;
        if (this.load != other.load && (this.load == null || !this.load.equals(other.load))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.load != null ? this.load.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return load.toString();
    }
}
