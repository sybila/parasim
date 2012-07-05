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

import org.sybila.parasim.extension.performence.api.MeasurementExport;
import org.sybila.parasim.extension.performence.api.MeasurementExportResource;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.extension.performence.api.MethodMeasurement;
import org.sybila.parasim.extension.performence.api.MethodSummarizedMeasurement;
import org.sybila.parasim.extension.performence.conf.Configuration;
import org.sybila.parasim.extension.performence.event.MeasurementEvent;
import org.sybila.parasim.model.xml.XMLException;

/**
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class MeasurementExporter {

    public static final String EXPORT_FILE_PREFIX = "parasim-performence";
    public static final Logger LOGGER = LoggerFactory.getLogger(MeasurementExporter.class);

    public void export(@Observes MeasurementEvent event, @Observes Configuration configuration) throws IOException, XMLException {
        File exportDirectory = new File(configuration.getExportDirectory());
        if (!exportDirectory.exists()) {
            exportDirectory.mkdirs();
        }
        String[] old = exportDirectory.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(EXPORT_FILE_PREFIX);
            }
        });
        if (configuration.shouldDeleteOldExportFile()) {
            for (String filename : old) {
                File oldFile = new File(exportDirectory, filename);
                oldFile.delete();
                LOGGER.info(oldFile.getAbsolutePath() + " deleted");
            }
        }
        File exportFile = null;
        if (old.length == 0 || configuration.shouldDeleteOldExportFile()) {
            exportFile = new File(configuration.getExportDirectory(), EXPORT_FILE_PREFIX + ".xml");
        } else {
            exportFile = new File(configuration.getExportDirectory(), EXPORT_FILE_PREFIX + "-" + UUID.randomUUID() + ".xml");
        }
        MeasurementExport export = new MeasurementExport();
        for (Collection<MethodMeasurement> measurements : event.getMethodMeasurements().values()) {
            MethodSummarizedMeasurement toWrite = computeMethodMeasurement(measurements);
            export.addMethodMeasurement(toWrite);
        }
        MeasurementExportResource resource = new MeasurementExportResource(exportFile);
        resource.setRoot(export);
        resource.store();
        LOGGER.info("Exporting peformence information containing " + event.getMethodMeasurements().size() + " entries to " + exportFile.getAbsolutePath());
    }

    private MethodSummarizedMeasurement computeMethodMeasurement(Collection<MethodMeasurement> data) {
        if (data.isEmpty()) {
            throw new IllegalArgumentException("The data doesn't contain any data.");
        }
        long timeSum = 0;
        long memorySum = 0;
        for (MethodMeasurement measurement : data) {
            timeSum += measurement.getTime();
            memorySum += measurement.getUsedMemory();
        }
        final long timeAvg = timeSum / data.size();
        final long memoryAvg = memorySum / data.size();
        final long timeTotal = timeSum;
        final long memoryTotal = memorySum;
        final long numberOfInvocations = data.size();
        final Method method = data.iterator().next().getMethod();
        return new MethodSummarizedMeasurement() {

            @Override
            public long getNumberOfInvocations() {
                return numberOfInvocations;
            }

            @Override
            public long getUsedMemory() {
                return memoryTotal;
            }

            @Override
            public long getTime() {
                return timeTotal;
            }

            @Override
            public Method getMethod() {
                return method;
            }

            @Override
            public long getAverageTime() {
                return timeAvg;
            }

            @Override
            public long getAverageUsedMemory() {
                return memoryAvg;
            }
        };
    }
}
