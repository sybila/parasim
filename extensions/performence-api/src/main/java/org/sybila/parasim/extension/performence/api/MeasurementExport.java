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
package org.sybila.parasim.extension.performence.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.sybila.parasim.model.xml.XMLRepresentable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class MeasurementExport implements XMLRepresentable {

    private List<MethodSummarizedMeasurement> methodMeasurements = new ArrayList<>();

    public void addMethodMeasurement(MethodSummarizedMeasurement measurement) {
        methodMeasurements.add(measurement);
    }

    @Override
    public Element toXML(Document doc) {
        Collections.sort(methodMeasurements, new Comparator<MethodSummarizedMeasurement>() {
            @Override
            public int compare(MethodSummarizedMeasurement m1, MethodSummarizedMeasurement m2) {
                int byClassName = m1.getMethod().getDeclaringClass().getName().compareTo(m2.getMethod().getDeclaringClass().getName());
                if (byClassName != 0) {
                    return byClassName;
                }
                return m1.getMethod().getName().compareTo(m2.getMethod().getName());
            }
        });
        Element root = doc.createElement(MeasurementExportFactory.PERFORMENCE);
        Class<?> previousClass = null;
        Element clazz = null;
        for (MethodSummarizedMeasurement measurement: methodMeasurements) {
            if (previousClass == null || !previousClass.equals(measurement.getMethod().getDeclaringClass())) {
                previousClass = measurement.getMethod().getDeclaringClass();
                clazz = doc.createElement(MeasurementExportFactory.CLASS);
                clazz.setAttribute(MeasurementExportFactory.NAME, previousClass.getCanonicalName());
                root.appendChild(clazz);
            }
            Element method = doc.createElement(MeasurementExportFactory.METHOD);
            method.setAttribute(MeasurementExportFactory.NAME, measurement.getMethod().getName());
            method.setAttribute(MeasurementExportFactory.TOTAL_TIME, Long.toString(measurement.getTime() / 1000000));
            method.setAttribute(MeasurementExportFactory.TOTAL_MEMORY, Long.toString(measurement.getUsedMemory()));
            method.setAttribute(MeasurementExportFactory.AVERAGE_MEMORY, Long.toString(measurement.getAverageUsedMemory()));
            method.setAttribute(MeasurementExportFactory.AVERAGE_TIME, Long.toString(measurement.getAverageTime() / 1000000));
            method.setAttribute(MeasurementExportFactory.INVOCATIONS, Long.toString(measurement.getNumberOfInvocations()));
            for (Class<?> paramClass: measurement.getMethod().getParameterTypes()) {
                Element param = doc.createElement(MeasurementExportFactory.PARAMETER);
                param.setAttribute(MeasurementExportFactory.TYPE, paramClass.getCanonicalName());
                method.appendChild(param);
            }
            clazz.appendChild(method);
        }
        return root;
    }

}
