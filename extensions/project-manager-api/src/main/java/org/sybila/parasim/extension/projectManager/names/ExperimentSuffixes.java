package org.sybila.parasim.extension.projectManager.names;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public enum ExperimentSuffixes {

    MODEL(".model.xml"),
    FORMULA(".formula.xml"),
    INITIAL_SAMPLING(".sampling.xml"),
    INITIAL_SPACE(".init_space.xml"),
    PRECISION_CONFIGURATION(".precision.xml"),
    SIMULATION_SPACE(".sim_space.xml"),
    VERIFICATION_RESULT(".result.xml");
    private final String suffix;

    private ExperimentSuffixes(String suffix) {
        this.suffix = suffix;
    }

    public String add(String base) {
        return base + suffix;
    }

    public String remove(String target) {
        if (!target.endsWith(suffix)) {
            return null;
        }
        return target.substring(0, target.length() - suffix.length());
    }

    public String getSuffix() {
        return suffix;
    }

    public static ExperimentSuffixes getSuffix(String name) {
        if (!name.endsWith(".xml")) {
            return null;
        }
        for (ExperimentSuffixes suff : values()) {
            if (name.endsWith(suff.getSuffix())) {
                return suff;
            }
        }
        return null;
    }
}
