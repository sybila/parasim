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
package org.sybila.parasim.extension.performence.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.core.Event;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.core.event.ManagerStarted;
import org.sybila.parasim.core.event.ManagerStopping;
import org.sybila.parasim.extension.performence.api.MethodMeasurement;
import org.sybila.parasim.extension.performence.conf.Configuration;
import org.sybila.parasim.extension.performence.event.MeasurementEvent;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class MeasurementDatabase {

    @Inject
    private Event<MeasurementEvent> event;

    private static boolean isEnabled = true;
    private static List<MethodMeasurement> measured = new ArrayList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(MeasurementDatabase.class);

    public void prepare(@Observes ManagerStarted event, @Observes Configuration configuration) {
        isEnabled = configuration.isEnabled();
    }

    public void export(@Observes ManagerStopping event, @Observes Configuration configuration) {
        if (!configuration.isEnabled()) {
            return;
        }
        // reorganize data
        Map<Method, Collection<MethodMeasurement>> measurements = new HashMap<>();
        synchronized (measured) {
            for (MethodMeasurement measurement: measured) {
                Collection<MethodMeasurement> methodMeasurements = measurements.get(measurement.getMethod());
                if (methodMeasurements == null) {
                    methodMeasurements = new ArrayList<>();
                    methodMeasurements.add(measurement);
                    measurements.put(measurement.getMethod(), methodMeasurements);
                } else {
                    methodMeasurements.add(measurement);
                }
            }
            // clean
            measured = Collections.synchronizedList(new ArrayList<MethodMeasurement>());
        }
        // fix data
        final Map<Method, Collection<MethodMeasurement>> result = new HashMap<>();
        for (Entry<Method, Collection<MethodMeasurement>> entry: measurements.entrySet()) {
            result.put(entry.getKey(), Collections.unmodifiableCollection(entry.getValue()));
        }
        // event
        this.event.fire(new MeasurementEvent() {
            @Override
            public Map<Method, Collection<MethodMeasurement>> getMethodMeasurements() {
                return Collections.unmodifiableMap(result);
            }
        });
    }

    public static void process(MethodMeasurement element) {
        if (!isEnabled) {
            return;
        }
        synchronized(measured) {
            measured.add(element);
        }
    }


}
