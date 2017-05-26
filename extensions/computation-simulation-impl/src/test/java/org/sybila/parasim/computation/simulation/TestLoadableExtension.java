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
package org.sybila.parasim.computation.simulation;

import org.sybila.parasim.computation.lifecycle.api.annotations.ComputationInstanceScope;
import org.sybila.parasim.computation.simulation.api.AdaptiveStepConfiguration;
import org.sybila.parasim.computation.simulation.api.AdaptiveStepSimulator;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.api.Context;
import org.sybila.parasim.core.test.ParasimTest;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestLoadableExtension extends ParasimTest {

    @Test
    public void testSimulatorLoaded() {
        Context context = getManager().context(ComputationInstanceScope.class);
        assertNotNull(context.resolve(AdaptiveStepSimulator.class, Default.class));
    }

    @Test
    public void testConfigurationLoaded() {
        Context context = getManager().context(ComputationInstanceScope.class);
        assertNotNull(context.resolve(AdaptiveStepConfiguration.class, Default.class));
    }

}
