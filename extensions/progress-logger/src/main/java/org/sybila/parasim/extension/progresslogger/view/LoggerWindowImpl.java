package org.sybila.parasim.extension.progresslogger.view;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.Date;
import javax.swing.JFrame;
import org.apache.log4j.spi.LoggingEvent;
import org.sybila.parasim.application.ui.ParasimIconFactory;
import org.sybila.parasim.extension.progresslogger.api.LoggerOutput;
import org.sybila.parasim.extension.progresslogger.api.LoggerWindow;

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

        setDefaultCloseOperation(HIDE_ON_CLOSE);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(time, BorderLayout.PAGE_START);
        getContentPane().add(output.getComponent(), BorderLayout.CENTER);
    }

    @Override
    public void simulationStarted() {
        time.startSimulation();
        output.simulationStart(new Date());
    }

    @Override
    public void simulationStopped() {
        time.stopSimulation();
        output.simulationStop(new Date());
    }

    @Override
    public void log(LoggingEvent event) {
        output.append(event);
    }

    @Override
    public void dispose() {
        simulationStopped();
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
                window.simulationStopped();
            }
        });
    }
}
