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
package org.sybila.parasim.extension.progresslogger.view;

import java.util.Date;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.apache.log4j.spi.LoggingEvent;
import org.sybila.parasim.extension.progresslogger.api.LoggerOutput;
import org.sybila.parasim.model.verification.Robustness;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class UnstyledLoggerOutput extends JScrollPane implements LoggerOutput {

    private final JTextArea output = new JTextArea();

    public UnstyledLoggerOutput() {
        setViewportView(output);
        output.setEditable(false);
        output.setRows(5);
    }

    @Override
    public void append(LoggingEvent event) {
        output.append(event.getMessage().toString());
        output.append("\n");
    }

    @Override
    public void simulationStart(Date time) {
        output.append("Simulation started.\n");
    }

    @Override
    public void simulationStop(Date time, Robustness globalRobustness) {
        output.append("Simulation stopped.\n");
    }

    @Override
    public JComponent getComponent() {
        return this;
    }
}
