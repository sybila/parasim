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
package org.sybila.parasim.computation.simulation;

import org.sybila.parasim.execution.api.annotations.ComputationScope;
import org.sybila.parasim.core.context.Context;
import java.lang.annotation.Annotation;
import org.sybila.parasim.computation.simulation.api.AdaptiveStepConfiguration;
import org.sybila.parasim.computation.simulation.api.AdaptiveStepSimulator;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.ManagerImpl;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.core.context.AbstractContext;
import org.sybila.parasim.core.extension.configuration.api.ExtensionDescriptorMapper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestLoadableExtension {

    private Manager manager;

    @BeforeMethod
    public void startManager() throws Exception {
        System.setProperty("parasim.config.file", "src/test/resources/org/sybila/parasim/computation/simulation/parasim.xml");
        manager = ManagerImpl.create();
        manager.start();
        assertNotNull(manager.resolve(ExtensionDescriptorMapper.class, Default.class, manager.getRootContext()));
    }

    @AfterMethod
    public void stopManager() {
        manager.shutdown();
    }

    @Test
    public void testSimulatorLoaded() {
        assertNotNull(manager.resolve(AdaptiveStepSimulator.class, Default.class, manager.getRootContext()));
    }

    @Test
    public void testConfigurationLoaded() {
        Context context = new AbstractContext() {
            public Class<? extends Annotation> getScope() {
                return ComputationScope.class;
            }
        };
        manager.initializeContext(context);
        assertNotNull(manager.resolve(AdaptiveStepConfiguration.class, Default.class, context));
    }

}
