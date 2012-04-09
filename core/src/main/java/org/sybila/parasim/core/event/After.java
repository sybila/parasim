package org.sybila.parasim.core.event;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class After<L> {

    private L load;

    private After(L load) {
        if (load == null) {
            throw new IllegalArgumentException("The parameter [load] is null.");
        }
        this.load = load;
    }

    public static <L> After<L> of(L load) {
        return new After<L>(load);
    }

    public L getLoad() {
        return load;
    }

}
