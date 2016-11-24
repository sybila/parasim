/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.computation.verification.stlstar.cpu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.stl.FormulaInterval;
import org.sybila.parasim.model.verification.stl.TemporalFormula;
import org.sybila.parasim.model.verification.stlstar.FormulaStarInfo;
import org.sybila.parasim.util.Coordinate;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class TemporalStarMonitor extends AbstractStarMonitor {

    /*
     * How this works: For each frozen time combination, there is one "line",
     * i.e. list of robustness for each time and given frozen times. Each line
     * is computed as a whole.
     *
     * When I get an index, first I look whether I have such a line, then I
     * return correct time; otherwise, I have to compute the whole line (and
     * save it).
     */
    private final Map<Coordinate, List<Robustness>> robustness = new HashMap<>();
    private final int starNum;
    private final FormulaInterval interval;

    /**
     * Removes first component of coordinate, i.e. sets it to zero.
     */
    private static Coordinate removeTime(Coordinate coordinate) {
        Coordinate.Builder coord = new Coordinate.Builder(coordinate);
        coord.setCoordinate(0, 0);
        return coord.create();
    }

    public TemporalStarMonitor(TemporalFormula property, FormulaStarInfo info) {
        super(property, info);
        starNum = info.getStarNumber();
        interval = property.getInterval();
    }

    private Robustness getRobustness(List<Robustness> list, Coordinate index) {
        return list.get(index.getCoordinate(0));
    }

    private List<Robustness> addLine(Coordinate frozen) {
        List<Robustness> line = computeLine(frozen);
        robustness.put(frozen, line);
        return line;
    }

    /**
     * Precomputes robustness for one line, i.e. for given frozen times and
     * variable time.
     *
     * @param frozen Values of frozen times (normal time, the first coordinate,
     * should be zero).
     * @return List of robustness wrt. given frozen times.
     */
    protected abstract List<Robustness> computeLine(Coordinate frozen);

    /**
     * Returns interval of monitored temporal formula.
     *
     * @return Temporal interval.
     */
    protected FormulaInterval getInterval() {
        return interval;
    }

    @Override
    protected Robustness computeRobustness(Coordinate index) {
        Coordinate frozen = removeTime(index);
        List<Robustness> line = robustness.get(frozen);
        if (line == null) {
            line = addLine(frozen);
        }
        return getRobustness(line, index);
    }

    @Override
    public int size() {
        Coordinate zero = new Coordinate.Builder(starNum + 1).create();
        List<Robustness> line;
        if (robustness.isEmpty()) {
            line = addLine(zero);
        } else {
            line = robustness.get(zero);
        }
        return line.size();
    }
}