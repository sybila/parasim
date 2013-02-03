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
package org.sybila.parasim.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ExtremesQueue<Item, Value extends Comparable<Value>> implements Iterable<Item> {

    private final List<Item> items = new ArrayList<>();
    private final Evaluator<Item, Value> evaluator;
    private boolean increasing = true;

    public ExtremesQueue(Evaluator<Item, Value> evaluator) {
        if (evaluator == null) {
            throw new IllegalArgumentException("The parameter [evaluator] is null.");
        }
        this.evaluator = evaluator;
    }

    public void offer(Item item) {
        if (items.isEmpty()) {
            items.add(item);
        } else if (items.size() == 1) {
            int compared = evaluator.evaluate(item).compareTo(evaluator.evaluate(items.get(0)));
            if (compared > 0) {
                items.add(item);
            } else if (compared < 0) {
                items.add(item);
                increasing = false;
            }
        } else {
            int compared = evaluator.evaluate(item).compareTo(evaluator.evaluate(items.get(items.size() - 1)));
            if (increasing && compared > 0) {
                items.remove(items.size() - 1);
                offer(item);
            } else if (!increasing && compared > 0) {
                items.add(item);
                increasing = true;
            } else if (!increasing && compared < 0) {
                items.remove(items.size() - 1);
                offer(item);
            } else if (increasing && compared < 0) {
                items.add(item);
                increasing = false;
            }
        }

    }

    @Override
    public Iterator<Item> iterator() {
        return items.iterator();
    }

    public int size() {
        return items.size();
    }

    public interface Evaluator<Item, Value extends Comparable<Value>> {

        Value evaluate(Item item);

    }

}
