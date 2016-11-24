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
package org.sybila.parasim.computation.lifecycle.impl.shared;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.sybila.parasim.computation.lifecycle.api.Computation;
import org.sybila.parasim.computation.lifecycle.api.ComputationContainer;
import org.sybila.parasim.computation.lifecycle.api.Emitter;
import org.sybila.parasim.computation.lifecycle.api.Selector;
import org.sybila.parasim.computation.lifecycle.api.SharedMemoryExecutor;
import org.sybila.parasim.computation.lifecycle.api.annotations.RunWith;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.annotation.Inject;
import org.sybila.parasim.core.test.RemoteParasimTest;
import org.sybila.parasim.model.MergeableBox;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestCustomOffererComparator extends RemoteParasimTest {

    @Test(enabled = false)
    public void test() throws InterruptedException, ExecutionException, TimeoutException {
        ComputationContainer container = getManager().resolve(ComputationContainer.class, Default.class);
        MergeableString result = container.compute(new StringComputation("AHOJ")).get(10, TimeUnit.SECONDS);
        Assert.assertEquals(result.get(), "JOHA");
    }

    @Override
    protected void beforeManagerCreated() {
        super.beforeManagerCreated();
        System.setProperty("parasim.computation.lifecycle.default.executor", SharedMemoryExecutor.class.getName());
        System.setProperty("parasim.computation.lifecycle.core.pool.size", "1");
        System.setProperty("parasim.computation.lifecycle.node.threshold", "1");
    }

    @AfterClass
    public void resetProperties() {
        System.clearProperty("parasim.computation.lifecycle.default.executor");
        System.clearProperty("parasim.computation.lifecycle.core.pool.size");
        System.clearProperty("parasim.computation.lifecycle.node.threshold");
    }

    @RunWith(offerer = StringComputationSelector.class)
    public static class StringComputation implements Computation<MergeableString> {

        private final String load;
        public final int index;

        @Inject
        private Emitter emitter;

        public StringComputation(String load, int index) {
            this.load = load;
            this.index = index;
        }

        public StringComputation(String load) {
            this(load, -1);
        }

        @Override
        public MergeableString call() throws Exception {
            if (load.length() > 1) {
                for (int i=0; i < load.length(); i++) {
                    emitter.emit(new StringComputation(""+load.charAt(i), i));
                }
                Thread.sleep(100);
                return new MergeableString("");
            } else {
                return new MergeableString(load);
            }
        }

        @Override
        public void destroy() throws Exception {
        }

    }

    public static class StringComputationSelector implements Selector<StringComputation> {

        @Override
        public StringComputation select(Collection<StringComputation> items) {
            return Collections.min(items, new StringComputationComparator());
        }

    }

    public static class StringComputationComparator implements Comparator<StringComputation> {

        @Override
        public int compare(StringComputation o1, StringComputation o2) {
            return o2.index - o1.index;
        }

    }

    public static class MergeableString extends MergeableBox<MergeableString, String> {

        public MergeableString(String load) {
            super(load);
        }

        @Override
        public MergeableString merge(MergeableString toMerge) {
            return new MergeableString(this.get() + toMerge.get());
        }

    }

}
