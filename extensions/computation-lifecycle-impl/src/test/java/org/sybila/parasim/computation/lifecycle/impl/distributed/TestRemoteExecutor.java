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
package org.sybila.parasim.computation.lifecycle.impl.distributed;

import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;
import org.sybila.parasim.computation.lifecycle.api.Computation;
import org.sybila.parasim.computation.lifecycle.api.MutableStatus;
import org.sybila.parasim.computation.lifecycle.api.ProgressAdapter;
import org.sybila.parasim.computation.lifecycle.api.RemoteExecutor;
import org.sybila.parasim.computation.lifecycle.api.RemoteMutableStatus;
import org.sybila.parasim.computation.lifecycle.impl.shared.SimpleStatus;
import org.sybila.parasim.computation.lifecycle.test.MultiplicativeInteger;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.test.RemoteParasimTest;
import org.sybila.parasim.model.Mergeable;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestRemoteExecutor extends RemoteParasimTest {

    private UUID computationId;
    private MutableStatus status;
    private RemoteExecutor executor;
    private final Object waitMonitor = new Object();

    @BeforeMethod(dependsOnMethods={"startServer"})
    protected void startTheComputation() throws URISyntaxException, IOException {
        computationId = UUID.randomUUID();
        status = new SimpleStatus();
        executor = getRemoteControl().lookup(RemoteExecutor.class, Default.class);
        executor.startComputation(ConstComputation.class, (RemoteMutableStatus) UnicastRemoteObject.exportObject(new RemoteMutableStatusWrapper(status)), computationId);
    }

    @Test
    public void testQueueAvailable() throws URISyntaxException, IOException {
        Assert.assertNotNull(executor.getQueue(computationId));
        Assert.assertEquals(executor.getQueue(computationId).size(), 0);
        executor.destroyComputation(computationId);
        try {
            executor.getQueue(computationId);
            Assert.fail("After destroying the computation no offerer should be available.");
        } catch(Exception ignored) {
        }
    }

    @Test
    public void testSubmit() throws RemoteException, InterruptedException {
        status.addProgressListerner(new WakeUpProgressListener());
        executor.getQueue(computationId).emit(new ConstComputation<>(new MultiplicativeInteger(0)));
        synchronized(waitMonitor) {
            waitMonitor.wait(1000);
        }
        for (int i=0; i<5; i++) {
            Thread.sleep(100);
            if (status.isFinished()) {
                return;
            }
        }
        Assert.fail("status isn't finished");
    }

    private class WakeUpProgressListener extends ProgressAdapter {

        @Override
        public void done(UUID node, Mergeable event) {
            synchronized (waitMonitor) {
                waitMonitor.notifyAll();
            }
        }

    }

    private static class ConstComputation<RESULT extends Mergeable<RESULT>> implements Computation<RESULT> {

        private final RESULT result;

        public ConstComputation(RESULT result) {
            this.result = result;
        }

        @Override
        public RESULT call() throws Exception {
            Thread.sleep(400);
            return result;
        }

        @Override
        public void destroy() throws Exception {
        }

    }

}
