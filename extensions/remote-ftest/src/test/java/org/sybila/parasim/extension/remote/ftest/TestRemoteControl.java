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
package org.sybila.parasim.extension.remote.ftest;

import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.ManagerImpl;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.core.extension.configuration.api.ParasimDescriptor;
import org.sybila.parasim.extension.remote.api.RemoteControl;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestRemoteControl {

    @Test
    public void testStart() throws Exception {
        System.setProperty("parasim.config.file", "src/test/resources/parasim.xml");
        System.setProperty("parasim.remote.target", "target/dependency/remote-impl.jar");
        Manager manager = ManagerImpl.create();
        manager.start();
        ParasimDescriptor descriptor = manager.resolve(ParasimDescriptor.class, Default.class, manager.getRootContext());
        RemoteControl control = null;
        try {
            control = manager.resolve(RemoteControl.class, Default.class, manager.getRootContext());
            Assert.assertEquals(control.getHostControls().size(), 1, "Number of remote hosts doesn't match.");
            Assert.assertTrue(control.getHostControls().iterator().next().isRunning(false), "The remote host should be running, but it isn't.");
            Assert.assertTrue(control.getHostControls().iterator().next().isRunning(true), "The remote host should be running, but it isn't.");
            Assert.assertEquals(control.getRunningStatus(true), RemoteControl.RemoteRunningStatus.ALL, "The remote host doesn't match.");
        } finally {
            if (control != null) {
                control.shutdown();
            }
        }
    }

}
