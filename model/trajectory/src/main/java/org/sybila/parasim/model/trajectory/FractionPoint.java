/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.model.trajectory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public final class FractionPoint implements Comparable<FractionPoint>, Serializable {

    private final Fraction[] fractions;

    public FractionPoint(Fraction[] fractions) {
        if (fractions == null) {
            throw new IllegalArgumentException("The parameter [fractions] is null.");
        }
        for (int i=0; i<fractions.length; i++) {
            if (fractions[i] == null) {
                throw new IllegalArgumentException("Fraction on position <" + i + "> is null.");
            }
        }
        this.fractions = fractions;
    }

    public FractionPoint(long[] nominators, long[] denominators) {
        if (nominators == null) {
            throw new IllegalArgumentException("The parameter [nominators] nis null.");
        }
        if (denominators == null) {
            throw new IllegalArgumentException("The parameter [denominators] nis null.");
        }
        fractions = new Fraction[nominators.length];
        for (int i=0; i<fractions.length; i++) {
            fractions[i] = new Fraction(nominators[i], denominators[i]);
        }
    }

    public static FractionPoint maximum(final int dimension) {
        Fraction[] fractions = repeat(dimension, new Fraction(1, 1));
        return new FractionPoint(fractions);
    }

    public static FractionPoint minimum(final int dimension) {
        Fraction[] fractions = repeat(dimension, new Fraction(0, 1));
        return new FractionPoint(fractions);
    }

    public static Collection<FractionPoint> extremes(final int dimension, final boolean[] skip) {
        final long[] denominators = repeat(dimension, 1);
        List<long[]> nominators = new ArrayList<>();
        nominators.add(new long[0]);
        for (int dim=1; dim<=dimension; dim++) {
            List<long[]> current = new ArrayList<>();
            for (long[] n: nominators) {
                for (long e: new long[] {0, 1}) {
                    if (skip != null && skip[dim-1] && e == 1) {
                        continue;
                    }
                    long[] data = new long[dim];
                    System.arraycopy(n, 0, data, 0, n.length);
                    data[n.length] = e;
                    current.add(data);
                }
            }
            nominators = current;
        }
        List<FractionPoint> points = new ArrayList<>();
        for (long[] n: nominators) {
            points.add(new FractionPoint(n, denominators));
        }
        return points;
    }

    public static Collection<FractionPoint> extremes(final int dimension) {
        return extremes(dimension, null);
    }

    public int getDimension() {
        return fractions.length;
    }

    public int diffDimension(FractionPoint other) {
        if (other.getDimension() != this.getDimension()) {
            throw new IllegalStateException("The dimension doesn't match, expected <" + this.getDimension() + ">, <" + other.getDimension() + "> given.");
        }
        for (int dim=0; dim<getDimension(); dim++) {
            if (!this.fractions[dim].equals(other.fractions[dim])) {
                return dim;
            }
        }
        throw new IllegalStateException("The fractions point are equal.");
    }

    public Fraction diffDistance(FractionPoint other) {
        int diffDim = diffDimension(other);
        return this.fractions[diffDim].distance(other.fractions[diffDim]);
    }

    public Collection<FractionPoint> surround(Fraction radius, boolean[] skip) {
        List<FractionPoint> result = new ArrayList<>();
        for (int dim=0; dim<getDimension(); dim++) {
            if (skip != null && skip[dim]) {
                continue;
            }
            Fraction[] first = toArrayCopy();
            Fraction[] second = toArrayCopy();
            first[dim] = first[dim].addition(radius);
            second[dim] = second[dim].difference(radius);
            result.add(new FractionPoint(first));
            result.add(new FractionPoint(second));
        }
        return result;
    }

    public Collection<FractionPoint> surround(Fraction radius) {
        return surround(radius, null);
    }

    public FractionPoint middle(FractionPoint other) {
        if (other.getDimension() != this.getDimension()) {
            throw new IllegalStateException("The dimension doesn't match, expected <" + this.getDimension() + ">, <" + other.getDimension() + "> given.");
        }
        final Fraction[] fractions = new Fraction[this.getDimension()];
        for (int dim=0; dim<fractions.length; dim++) {
            fractions[dim] = this.fractions[dim].middle(other.fractions[dim]);
        }
        return new FractionPoint(fractions);
    }

    public Fraction fraction(int dimension) {
        return fractions[dimension];
    }

    public Point value(Point point) {
        if (point.getDimension() != this.getDimension()) {
            throw new IllegalStateException("The dimension doesn't match, expected <" + this.getDimension() + ">, <" + point.getDimension() + "> given.");
        }
        float[] data = point.toArrayCopy();
        for (int dim=0; dim<point.getDimension(); dim++) {
            data[dim] = (float) fractions[dim].toDouble() * data[dim];
        }
        return new ArrayPoint(point.getTime(), data);
    }

    public Fraction[] toArrayCopy() {
        Fraction[] copy = new Fraction[fractions.length];
        System.arraycopy(fractions, 0, copy, 0, fractions.length);
        return copy;
    }

    public boolean isValid() {
        for (Fraction fraction: fractions) {
            if (fraction.nominator < 0 || fraction.denominator < fraction.nominator) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Arrays.deepHashCode(this.fractions);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FractionPoint other = (FractionPoint) obj;
        if (!Arrays.deepEquals(this.fractions, other.fractions)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return Arrays.toString(fractions);
    }

    private static Fraction[] repeat(int length, Fraction x) {
        Fraction[] result = new Fraction[length];
        for (int i=0; i<result.length; i++) {
            result[i] = x;
        }
        return result;
    }

    private static long[] repeat(int length, long x) {
        long[] result = new long[length];
        for (int i=0; i<result.length; i++) {
            result[i] = x;
        }
        return result;
    }

    @Override
    public int compareTo(FractionPoint other) {
        for (int dim=0; dim<Math.min(this.getDimension(), other.getDimension()); dim++) {
            int dimComp = this.fraction(dim).compareTo(other.fraction(dim));
            if (dimComp != 0) {
                return dimComp;
            }
        }
        return this.getDimension() - other.getDimension();
    }

    public static final class Fraction implements Comparable<Fraction>, Serializable {

        private final long nominator;
        private final long denominator;

        public Fraction(long nominator, long denominator) {
            if (denominator == 0) {
                throw new IllegalArgumentException("Denominator can't be zero.");
            }
            for (int candidate=2; candidate <= Math.ceil(Math.sqrt(denominator)); candidate++) {
                while (denominator % candidate == 0 && nominator % candidate == 0) {
                    denominator = denominator / candidate;
                    nominator = nominator / candidate;
                }
            }
            if (denominator < 0) {
                nominator = - nominator;
            }
            this.nominator = nominator;
            this.denominator = denominator;
        }

        public long getDenominator() {
            return denominator;
        }

        public long getNominator() {
            return nominator;
        }

        public Fraction addition(Fraction other) {
            long newNominator;
            long newDenominator;
            if (this.denominator == other.denominator) {
                newNominator = this.nominator + other.nominator;
                newDenominator = this.denominator;
            } else {
                newNominator = other.denominator * this.nominator + this.denominator * other.nominator;
                newDenominator = this.denominator * other.denominator;
            }
            return new Fraction(newNominator, newDenominator);
        }

        public Fraction difference(Fraction other) {
            long newNominator;
            long newDenominator;
            if (this.denominator == other.denominator) {
                newNominator = this.nominator - other.nominator;
                newDenominator = this.denominator;
            } else {
                newNominator = other.denominator * this.nominator - this.denominator * other.nominator;
                newDenominator = this.denominator * other.denominator;
            }
            return new Fraction(newNominator, newDenominator);
        }

        public Fraction divide(long number) {
            return new Fraction(nominator, denominator * number);
        }

        public Fraction distance(Fraction other) {
            long newNominator;
            long newDenominator;
            if (this.denominator == other.denominator) {
                newNominator = this.nominator - other.nominator;
                newDenominator = this.denominator;
            } else {
                newNominator = other.denominator * this.nominator - this.denominator * other.nominator;
                newDenominator = this.denominator * other.denominator;
            }
            return new Fraction(Math.abs(newNominator), Math.abs(newDenominator));
        }

        public Fraction middle(Fraction other) {
            long newNominator;
            long newDenominator;
            if (this.denominator == other.denominator) {
                newNominator = this.nominator + other.nominator;
                newDenominator = this.denominator * 2;
            } else {
                newNominator = this.denominator * other.nominator + other.denominator * this.nominator;
                newDenominator = this.denominator * other.denominator * 2;
            }
            return new Fraction(newNominator, newDenominator);
        }

        public double toDouble() {
            return ((double) nominator) / denominator;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Fraction other = (Fraction) obj;
            if (this.nominator != other.nominator) {
                return false;
            }
            if (this.denominator != other.denominator) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return nominator + "/" + denominator;
        }

        @Override
        public int compareTo(Fraction other) {
            return (int) Math.signum(this.toDouble() - other.toDouble());
        }

    }

}
