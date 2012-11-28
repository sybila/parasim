package org.sybila.parasim.extension.progresslogger.view;

import java.util.Date;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.apache.log4j.spi.LoggingEvent;
import org.sybila.parasim.extension.progresslogger.api.LoggerOutput;

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
    public void simulationStop(Date time) {
        output.append("Simulation stopped.\n");
    }

    @Override
    public JComponent getComponent() {
        return this;
    }
}
