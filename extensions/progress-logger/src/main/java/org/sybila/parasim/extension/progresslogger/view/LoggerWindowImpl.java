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
package org.sybila.parasim.extension.progresslogger.view;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.Date;
import javax.swing.JFrame;
import org.apache.log4j.spi.LoggingEvent;
import org.sybila.parasim.application.ui.ParasimIconFactory;
import org.sybila.parasim.extension.progresslogger.api.LoggerOutput;
import org.sybila.parasim.extension.progresslogger.api.LoggerWindow;
import org.sybila.parasim.model.verification.Robustness;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class LoggerWindowImpl extends JFrame implements LoggerWindow {

    private final LoggerOutput output;
    private final TimePanel time;

    public LoggerWindowImpl(LoggerOutput output) {
        if (output == null) {
            throw new IllegalArgumentException("");
        }
        this.output = output;
        time = new TimePanel();

        setSize(400, 300);
        setLocation(850, 50);
        setIconImage(ParasimIconFactory.getInstance().getIcon());
        setTitle("PARASIM Log");

        setDefaultCloseOperation(HIDE_ON_CLOSE);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(time, BorderLayout.PAGE_START);
        getContentPane().add(output.getComponent(), BorderLayout.CENTER);
    }

    @Override
    public void simulationStarted() {
        setVisible(true);
        time.startSimulation();
        output.simulationStart(new Date());
    }

    @Override
    public void simulationStopped(Robustness globalRobustness) {
        time.stopSimulation();
        output.simulationStop(new Date(), globalRobustness);
    }

    @Override
    public void log(LoggingEvent event) {
        output.append(event);
    }

    @Override
    public void dispose() {
        simulationStopped(Robustness.UNDEFINED);
        super.dispose();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                LoggerWindowImpl window = new LoggerWindowImpl(new StyledLoggerOutput());
                window.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                window.setVisible(true);
                window.simulationStarted();
                window.simulationStopped(Robustness.UNDEFINED);
            }
        });
    }
}
