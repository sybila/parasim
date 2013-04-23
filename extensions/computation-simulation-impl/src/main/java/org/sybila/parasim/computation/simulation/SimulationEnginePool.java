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
package org.sybila.parasim.computation.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sybila.parasim.computation.simulation.cpu.SimulationEngine;
import org.sybila.parasim.computation.simulation.cpu.SimulationEngineFactory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SimulationEnginePool {

    private Map<SimulationEngineFactory, List<SimulationEngine>> engines = new HashMap<>();

    public synchronized <E extends SimulationEngine> E get(SimulationEngineFactory<E> factory) {
        List<SimulationEngine> engines = this.engines.get(factory);
        if (engines == null || engines.isEmpty()) {
            return factory.simulationEngine();
        } else {
            return (E) engines.remove(0);
        }
    }

    public synchronized void back(SimulationEngine engine) {
        List<SimulationEngine> engines = this.engines.get(engine.factory());
        if (engines == null) {
            engines = new ArrayList<>();
            this.engines.put(engine.factory(), engines);
        }
        engines.add(engine);
    }

    public synchronized void destroy() {
        for (List<SimulationEngine> engines: this.engines.values()) {
            for (SimulationEngine engine: engines) {
                engine.close();
            }
        }
    }

}
