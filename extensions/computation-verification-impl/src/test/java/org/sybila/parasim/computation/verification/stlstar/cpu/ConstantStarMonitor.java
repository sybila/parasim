package org.sybila.parasim.computation.verification.stlstar.cpu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.verification.api.Monitor;
import org.sybila.parasim.computation.verification.cpu.StarMonitor;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.verification.Property;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.SimpleRobustness;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stl.Predicate;
import org.sybila.parasim.model.verification.stlstar.MultiPoint;
import org.sybila.parasim.util.Coordinate;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ConstantStarMonitor implements StarMonitor {

    public static interface Function {

        float compute(float[] values);
    }
    protected static final Property EMPTY_PROPERTY = new Property() {

        @Override
        public float getTimeNeeded() {
            return 0f;
        }
    };
    protected static final float EPSILON = 0.0001f;
    private final List<Float> times;
    private final List<Float> values;
    private final int starNum;

    private static Coordinate getCoordinate(int index, int tSize, int stars) {
        Coordinate.Builder coord = new Coordinate.Builder(stars + 1);
        for (int i = 0; i <= stars; i++) {
            coord.setCoordinate(i, index % tSize);
            index /= tSize;
        }
        return coord.create();
    }

    public ConstantStarMonitor(List<Float> times, List<Float> values) {
        Validate.notNull(times);
        Validate.notNull(values);

        int num = 0;
        int tSize = times.size();
        int vSize = tSize;
        while (vSize < values.size()) {
            vSize *= tSize;
            num++;
        }
        if (vSize != values.size()) {
            throw new IllegalArgumentException("Size of values is not an exponent of the size of times.");
        }
        this.times = times;
        this.values = values;
        starNum = num;
    }

    @Override
    public Collection<Monitor> getSubmonitors() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Property getProperty() {
        return EMPTY_PROPERTY;
    }

    @Override
    public Robustness getRobustness(Coordinate index) {
        Validate.isTrue(index.getDimension() > starNum);
        int valueIndex = 0;
        for (int i = starNum; i >= 0; i--) {
            valueIndex *= times.size();
            valueIndex += index.getCoordinate(i);
        }
        return new SimpleRobustness(values.get(valueIndex), times.get(index.getCoordinate(0)), EMPTY_PROPERTY);
    }

    @Override
    public Robustness getRobustness(int index) {
        Validate.validIndex(times, index);
        return new SimpleRobustness(values.get(index), times.get(index), EMPTY_PROPERTY);
    }

    @Override
    public Iterator<Robustness> iterator() {
        return new Iterator<Robustness>() {

            int nextIndex = 0;

            @Override
            public boolean hasNext() {
                return (nextIndex < times.size());
            }

            @Override
            public Robustness next() {
                Robustness result = getRobustness(nextIndex);
                nextIndex++;
                return result;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("You cannot remove from a monitor.");
            }
        };
    }

    @Override
    public int size() {
        return times.size();
    }

    public boolean isEqualTo(StarMonitor target) {
        if (target.size() != size()) {
            return false;
        }
        for (int i = 0; i < values.size(); i++) {
            Robustness targetRobustness = target.getRobustness(getCoordinate(i, times.size(), starNum));
            if (Math.abs(targetRobustness.getTime() - times.get(i % times.size())) > EPSILON) {
                return false;
            }
            if (Math.abs(targetRobustness.getValue() - values.get(i)) > EPSILON) {
                return false;
            }
        }
        return true;
    }

    public static ConstantStarMonitor createLinearMonitor(int size, int stars, float timeBase, float timeIncrement, float valueBase, float valueIncrement) {
        List<Float> times = new ArrayList<>();
        float time = timeBase;
        for (int i = 0; i < size; i++) {
            times.add(time);
            time += timeIncrement;
        }

        int dim = 1;
        for (int i = 0; i <= stars; i++) {
            dim *= size;
        }

        List<Float> values = new ArrayList<>();
        for (int i = 0; i < dim; i++) {
            float result = valueBase;
            int num = i;
            for (int j = 0; j <= stars; j++) {
                result += valueIncrement * (num % size);
                num /= size;
            }
            values.add(result);
        }

        return new ConstantStarMonitor(times, values);
    }

    public static ConstantStarMonitor computePointWiseMonitor(Function function, ConstantStarMonitor[] monitors) {
        Validate.notNull(monitors);
        Validate.notEmpty(monitors);
        Validate.notNull(function);

        int dim = monitors.length;

        List<Float> times = monitors[0].times;
        int tSize = times.size();
        int starNum = monitors[0].starNum;
        for (int i = 0; i < dim; i++) {
            int newTSize = monitors[i].times.size();
            if (newTSize < tSize) {
                tSize = newTSize;
                times = monitors[i].times;
            }
            starNum = Math.max(starNum, monitors[i].starNum);
        }
        int vSize = tSize;
        for (int i = 0; i < starNum; i++) {
            vSize *= tSize;
        }

        List<Float> values = new ArrayList<>();
        for (int i = 0; i < vSize; i++) {
            Coordinate coord = getCoordinate(i, tSize, starNum);
            float[] arguments = new float[dim];
            for (int j = 0; j < dim; j++) {
                arguments[j] = monitors[j].getRobustness(coord).getValue();
            }
            values.add(function.compute(arguments));
        }

        return new ConstantStarMonitor(times, values);
    }

    public Formula getFormula() {
        return new Predicate(Collections.EMPTY_LIST) {

            @Override
            public int getStarNumber() {
                return starNum;
            }

            @Override
            public Set<Integer> getStars() {
                Set<Integer> result = new HashSet<>();
                for (int i = 0; i <= starNum; i++) {
                    result.add(i);
                }
                return result;
            }

            @Override
            public Predicate mergeFrozenDimensions(Set<Integer> frozen) {
                throw new UnsupportedOperationException("Not supported.");
            }

            @Override
            public Predicate substituteStars(Map<Integer, Integer> substitution) {
                throw new UnsupportedOperationException("Not supported.");
            }

            @Override
            public float getValue(Point point) {
                throw new UnsupportedOperationException("Not supported.");
            }

            @Override
            public float getValue(float[] point) {
                throw new UnsupportedOperationException("Not supported.");
            }

            @Override
            public boolean isValid(Point p) {
                throw new UnsupportedOperationException("Not supported.");
            }

            @Override
            public float getValue(MultiPoint mp) {
                throw new UnsupportedOperationException("Not supported.");
            }

            @Override
            public boolean isValid(MultiPoint mp) {
                throw new UnsupportedOperationException("Not supported.");
            }

            @Override
            public String toString() {
                throw new UnsupportedOperationException("Not supported.");
            }
        };
    }
}