/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.extension.projectmanager.names;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public enum ExperimentSuffixes {

    EXPERIMENT(".experiment.properties"),
    MODEL(".model.xml"),
    FORMULA(".formula.xml"),
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
        for (ExperimentSuffixes suff : values()) {
            if (name.endsWith(suff.getSuffix())) {
                return suff;
            }
        }
        return null;
    }
}
