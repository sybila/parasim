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
package org.sybila.parasim.computation.monitoring.stl.cpu;

import org.sybila.parasim.model.verification.stl.TimeInterval;
import org.sybila.parasim.computation.monitoring.api.PropertyRobustness;
import java.util.List;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * Algorithm: Lemire's "STREAMING MAXIMUM-MINIMUM FILTER USING NO MORE THAN
 * THREE COMPARISONS PER ELEMENT" Nordic Journal of Computing, Volume 13,
 * Number 4, pages 328-339, 2006.
 *
 * Unlike the original algorithm, in this implementation the window is not
 * specified by a fix number of consecutive elements - width - because the signal
 * density may vary. Instead a time interval is given, so the minimum is
 * computed from all points who's time falls into the interval.
 *
 * Another major differenci is that data points do not represent constant values
 * over the time interval to the next data point, but are linear functions given
 * by a initial value and a derivative (PropertyRobustness), thus the value
 * changes also between individual datapoints and this must be taken into account.
 *
 * Original algorithm
 * =============================================================================
<code>
// input: array a, integer window width w
// output: arrays maxval and minval
// buffer: lists U and L
// requires: STL for deque support

deque<int> U,L;
for (uint i = 1; i < a.size(); i++)
{
if (i >= w)
{
maxval[i-w] = a[U.size()>0 ? U.front() : i-1];
maxval[i-w] = a[L.size()>0 ? L.front() : i-1];
} // end if
if (a[i] > a[i-1])
{
L.push_back(i-1);
if (i == w+L.front()) L.pop_front();
while (U.size() > 0)
{
if (a[i] <= a[U.back()])
{
if (i == w+U.front()) U.pop_front();
break;
} // end if
U.pop_back();
} // end while
} else
{
U.push_back(i-1);
if (i == w+U.front()) U.pop_front();
while (L.size() > 0)
{
if (a[i] >= a[L.back()])
{
if (i == w+L.front()) L.pop_front();
break;
} // end if
L.pop_back();
} // end while
} // end if else
} // end for
maxval[a.size()-w] = a[U.size()>0 ? U.front() : a.size()-1];
maxval[a.size()-w] = a[L.size()>0 ? L.front() : a.size()-1];
</code>
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public class LemireEngine {

    LemireEngine() {
    }

    List<SimplePropertyRobustness> evaluateMinimum(List<PropertyRobustness> signal, TimeInterval interval) {
        if (signal == null) {
            throw new IllegalArgumentException("Parameter signal is null.");
        }
        if (interval == null) {
            throw new IllegalArgumentException("Parameter interval is null.");
        }
        if (signal.isEmpty()) {
            throw new IllegalArgumentException("List signal is empty.");
        }

        Deque<Integer> U = new LinkedList<Integer>();
        Deque<Integer> L = new LinkedList<Integer>();
        Iterator<PropertyRobustness> it = signal.iterator();



        return null;
    }

    List<SimplePropertyRobustness> evaluateMaximum(List<SimplePropertyRobustness> signal, TimeInterval interval) {
        return null;
    }

    List<SimplePropertyRobustness>[] evaluateMinMax(List<SimplePropertyRobustness> signal, TimeInterval interval) {
        return null;
    }
}
