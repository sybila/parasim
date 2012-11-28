package org.sybila.parasim.extension.progresslogger.view;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TimePanel extends JPanel {

    private static final int DELAY = 500; //ms
    private static final String LABEL_RUNNING = "Simulation running: ";
    private static final String LABEL_STOPPED = "Simulation stopped: ";
    //
    private final JLabel info;
    private final JLabel time;
    private final Timer timer;
    private Date simulationStart;

    public TimePanel() {
        timer = new Timer(DELAY, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                time.setText(formatTimeInMilliseconds(new Date().getTime() - simulationStart.getTime()));
            }
        });
        timer.stop();
        timer.setCoalesce(true);
        timer.setRepeats(true);

        time = new JLabel();
        info = new JLabel("Simulation not running.");

        Font font = time.getFont().deriveFont(time.getFont().getSize() * 1.25f);
        time.setFont(font);
        info.setFont(font);

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        add(info);
        add(time);
        setBorder(new EmptyBorder(5, 5, 5, 5));
    }

    private String formatTimeInMilliseconds(long time) {
        if (time < 0) {
            throw new IllegalArgumentException("Time is negative.");
        }
        time /= 1000;
        if (time == 0) {
            return "0 s";
        }
        int s = (int) (time % 60);
        time /= 60;
        int m = (int) (time % 60);
        time /= 60;
        StringBuilder builder = new StringBuilder();
        if (time != 0) {
            builder.append(time);
            builder.append(" h, ");
        }
        if (m != 0) {
            builder.append(m);
            builder.append(" m, ");
        }
        if (s != 0) {
            builder.append(s);
            builder.append(" s");
        }
        return builder.toString();
    }

    public void startSimulation() {
        timer.start();
        simulationStart = new Date();
        time.setText(formatTimeInMilliseconds(0));
        info.setText(LABEL_RUNNING);
    }

    public void stopSimulation() {
        timer.stop();
        info.setText(LABEL_STOPPED);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
                final TimePanel panel = new TimePanel();
                frame.getContentPane().add(panel);
                frame.add(new JButton(new AbstractAction("Start") {

                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        panel.startSimulation();
                    }
                }));
                frame.add(new JButton(new AbstractAction("Stop") {

                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        panel.stopSimulation();
                    }
                }));
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
