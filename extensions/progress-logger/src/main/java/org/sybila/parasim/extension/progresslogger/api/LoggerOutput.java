package org.sybila.parasim.extension.progresslogger.api;

import java.util.Date;
import javax.swing.JComponent;
import org.apache.log4j.spi.LoggingEvent;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface LoggerOutput {

    public void append(LoggingEvent event);

    public void simulationStart(Date time);

    public void simulationStop(Date time);

    public JComponent getComponent();
}
