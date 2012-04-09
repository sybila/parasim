package org.sybila.parasim.core.event;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class Before<L> {

    private L load;

    private Before(L load) {
        if (load == null) {
            throw new IllegalArgumentException("The parameter [load] is null.");
        }
        this.load = load;
    }

    public static <L> Before<L> of(L context) {
        return new Before<L>(context);
    }

    public L getLoad() {
        return load;
    }

}
