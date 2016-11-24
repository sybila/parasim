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
package org.sybila.parasim.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * It holds elements where the first element is the largest one.
 * When a new element is inserted, elements already present in the dequeue can
 * be deleted to hold the sorted sequence.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class LemireDeque<O> implements Deque<O> {

    private final Comparator<O> comparator;
    private final Deque<O> deque;

    /**
     * Creates a new instance of {@link LemireDequeue}.
     *
     * @param comparator comparator used to hold the sorted sequence, elements
     * presented closer to the front are given as the first parameter to
     * {@link java.util.Comparator#compare(java.lang.Object, java.lang.Object) }
     */
    public LemireDeque(Comparator<O> comparator) {
        this(new LinkedList<O>(), comparator);
    }

    /**
     * Creates a new instance of {@link LemireDequeue}.
     *
     * @param deque dequeue where the elements are stored
     * @param comparator comparator used to hold the sorted sequence, elements
     * presented closer to the front are given as the first parameter to
     * {@link java.util.Comparator#compare(java.lang.Object, java.lang.Object) }
     */
    public LemireDeque(Deque<O> deque, Comparator<O> comparator) {
        if (deque == null) {
            throw new IllegalArgumentException("The parameter [dequeue] is null.");
        }
        if (comparator == null) {
            throw new IllegalArgumentException("The parameter  [comparator] is null.");
        }
        if (!deque.isEmpty()) {
            throw new IllegalArgumentException("The dequeu is not empty.");
        }
        this.deque = deque;
        this.comparator = comparator;
    }

    public void addFirst(O e) {
        prepareAddingFirst(e);
        deque.addFirst(e);
    }

    public void addLast(O e) {
        prepareAddingLast(e);
        deque.addLast(e);
    }

    public boolean offerFirst(O e) {
        prepareAddingFirst(e);
        return deque.offerFirst(e);
    }

    public boolean offerLast(O e) {
        prepareAddingLast(e);
        return deque.offerLast(e);
    }

    public O removeFirst() {
        return deque.removeFirst();
    }

    public O removeLast() {
        return deque.remove();
    }

    public O pollFirst() {
        return deque.pollFirst();
    }

    public O pollLast() {
        return deque.pollLast();
    }

    public O getFirst() {
        return deque.getFirst();
    }

    public O getLast() {
        return deque.getLast();
    }

    public O peekFirst() {
        return deque.peekFirst();
    }

    public O peekLast() {
        return deque.peekLast();
    }

    public boolean removeFirstOccurrence(Object o) {
        return deque.removeFirstOccurrence(o);
    }

    public boolean removeLastOccurrence(Object o) {
        return deque.removeLastOccurrence(o);
    }

    public boolean add(O e) {
        prepareAddingLast(e);
        return deque.add(e);
    }

    public boolean offer(O e) {
        prepareAddingLast(e);
        return deque.offer(e);
    }

    public O remove() {
        return deque.remove();
    }

    public O poll() {
        return deque.poll();
    }

    public O element() {
        return deque.element();
    }

    public O peek() {
        return deque.peek();
    }

    public void push(O e) {
        prepareAddingFirst(e);
        deque.push(e);
    }

    public O pop() {
        return deque.pop();
    }

    public boolean remove(Object o) {
        return deque.remove(o);
    }

    public boolean contains(Object o) {
        return deque.contains(o);
    }

    public int size() {
        return deque.size();
    }

    public Iterator<O> iterator() {
        return deque.iterator();
    }

    public Iterator<O> descendingIterator() {
        return deque.descendingIterator();
    }

    public boolean isEmpty() {
        return deque.isEmpty();
    }

    public Object[] toArray() {
        return deque.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return deque.toArray(a);
    }

    public boolean containsAll(Collection<?> c) {
        return deque.containsAll(c);
    }

    /**
     * This method is not supported.
     */
    public boolean addAll(Collection<? extends O> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean removeAll(Collection<?> c) {
        return deque.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return deque.retainAll(c);
    }

    public void clear() {
        deque.clear();
    }

    protected void prepareAddingFirst(O e) {
        if (e == null) {
            throw new IllegalArgumentException("The parameter [e] is null.");
        }
        while (!deque.isEmpty() && comparator.compare(e, deque.getFirst()) < 0) {
            deque.removeFirst();
        }
    }

    protected void prepareAddingLast(O e) {
        if (e == null) {
            throw new IllegalArgumentException("The parameter [e] is null.");
        }
        while (!deque.isEmpty() && comparator.compare(deque.getLast(), e) < 0) {
            deque.removeLast();
        }
    }

    @Override
    public String toString() {
        return deque.toString();
    }
}
