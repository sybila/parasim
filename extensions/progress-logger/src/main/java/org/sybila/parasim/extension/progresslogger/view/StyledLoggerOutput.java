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
package org.sybila.parasim.extension.progresslogger.view;

import java.awt.Color;
import java.awt.EventQueue;
import java.text.DateFormat;
import java.util.Date;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.sybila.parasim.extension.progresslogger.api.LoggerOutput;
import org.sybila.parasim.model.verification.Robustness;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class StyledLoggerOutput extends JScrollPane implements LoggerOutput {

    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
    private static final String DEFAULT_STYLE = "MESSAGE";
    private final StyledDocument output;

    private Style getBaseStyle() {
        return output.getStyle(StyleContext.DEFAULT_STYLE);
    }

    private void buildStyles() {
        Style def = output.addStyle(DEFAULT_STYLE, getBaseStyle());
        StyleConstants.setFontSize(def, 14);
        StyleConstants.setItalic(def, false);
        StyleConstants.setBold(def, true);

        Style unimportant = output.addStyle("UNIMPORTANT", def);
        StyleConstants.setBold(unimportant, false);
        output.addStyle(Level.DEBUG.toString(), unimportant);
        StyleConstants.setItalic(output.addStyle(Level.TRACE.toString(), unimportant), true);

        StyleConstants.setForeground(output.addStyle(Level.WARN.toString(), def), Color.ORANGE);
        Style err = output.addStyle("ERROR", def);
        StyleConstants.setForeground(err, Color.RED);
        output.addStyle(Level.ERROR.toString(), err);
        StyleConstants.setItalic(output.addStyle(Level.FATAL.toString(), err), true);
    }

    public StyledLoggerOutput() {
        output = new DefaultStyledDocument();
        buildStyles();

        JTextPane pane = new JTextPane(output);
        pane.setEditable(false);
        DefaultCaret caret = new DefaultCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        pane.setCaret(caret);
        setViewportView(pane);
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void simulationStart(Date time) {
        if (output.getLength() != 0) {
            appendNewLine();
        }
        append("Simulation started.", Level.INFO, time);
    }

    @Override
    public void simulationStop(Date time, Robustness globalRobustness) {
        append("Simulation stopped. Global robustness: " + globalRobustness.toString(), Level.INFO, time);
    }

    @Override
    public void append(LoggingEvent event) {
        append(event.getMessage().toString(), event.getLevel(), new Date(event.getTimeStamp()));
    }

    public void append(String message, Level level, Date time) {
        StringBuilder build = new StringBuilder();
        build.append(DATE_FORMAT.format(time));
        build.append(" - ");
        build.append(message);
        append(build.toString(), level);
    }

    private void appendNewLine() {
        try {
            output.insertString(output.getLength(), "\n", output.getStyle(DEFAULT_STYLE));
        } catch (BadLocationException ble) {
            throw new IllegalStateException("End of document is a bad location.", ble);
        }
    }

    private void append(String message, Level level) {
        Style style = output.getStyle(level.toString());
        if (style == null) {
            style = output.getStyle(DEFAULT_STYLE);
        }
        try {
            output.insertString(output.getLength(), message, style);
            appendNewLine();
        } catch (BadLocationException ble) {
            throw new IllegalStateException("End of document is bad location.", ble);
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame frame = new JFrame();
                StyledLoggerOutput output = new StyledLoggerOutput();
                frame.add(output);
                frame.setSize(250, 300);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setVisible(true);

                output.simulationStart(new Date());
                output.append("Debug message", Level.DEBUG, new Date());
                output.append("Trace message", Level.TRACE, new Date());
                output.append("Info message", Level.INFO, new Date());
                output.append("Warning message", Level.WARN, new Date());
                output.append("Error message", Level.ERROR, new Date());
                output.simulationStop(new Date(), Robustness.UNDEFINED);
                output.append("Fatal error", Level.FATAL, new Date());
            }
        });
    }
}
