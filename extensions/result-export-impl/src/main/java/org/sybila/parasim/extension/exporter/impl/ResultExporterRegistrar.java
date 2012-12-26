package org.sybila.parasim.extension.exporter.impl;

import org.sybila.parasim.core.annotations.Provide;
import org.sybila.parasim.extension.exporter.api.ResultExporter;
import org.sybila.parasim.extension.exporter.impl.annotations.Csv;

public class ResultExporterRegistrar {

    @Csv
    @Provide
    public ResultExporter provideCsvResultExporter() {
        return new CsvResultExporter();
    }

}
