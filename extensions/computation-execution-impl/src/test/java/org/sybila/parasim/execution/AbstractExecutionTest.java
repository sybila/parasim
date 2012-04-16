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
package org.sybila.parasim.execution;

import org.sybila.parasim.model.Mergeable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sybila.parasim.execution.impl.TestSequentialExecution;
import org.sybila.parasim.model.computation.AbstractComputation;
import org.sybila.parasim.model.computation.annotations.ThreadId;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.ManagerImpl;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.extension.configuration.api.ExtensionDescriptorMapper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class AbstractExecutionTest {

    private Manager manager;

    @BeforeMethod
    public void startManager() throws Exception {
        System.setProperty("parasim.config.file", "src/test/resources/org/sybila/parasim/computation/execution/parasim.xml");
        manager = ManagerImpl.create();
        manager.start();
        assertNotNull(manager.resolve(ExtensionDescriptorMapper.class, Default.class, manager.getRootContext()));
    }

    @AfterMethod
    public void stopManager() {
        manager.shutdown();
    }
    
    protected final Manager getManager() {
        return manager;
    }
    
    protected static class MergeableString implements Mergeable<MergeableString> {

        private final String original;
        
        public MergeableString(String original) {
            this.original = original;
        }

        public MergeableString merge(MergeableString toMerge) {
            return new MergeableString(getOriginal() + toMerge.getOriginal());
        }
        
        public String getOriginal() {
            return original;
        }
    }    
    
    protected static class TestComputation extends AbstractComputation<MergeableString> {
        private String message;
        private long delay;
        @ThreadId
        @Inject
        private Integer threadId;
        public TestComputation(String message, long delay) {
            this.message = message;
            this.delay = delay;
        }
        public MergeableString compute() {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                Logger.getLogger(TestSequentialExecution.class.getName()).log(Level.SEVERE, null, ex);
            }
            return new MergeableString(threadId + message);
        }
    }    
}
