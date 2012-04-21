package org.sybila.parasim.visualisation.plot.impl.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.text.DecimalFormat;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import org.sybila.parasim.model.ode.PointVariableMapping;
import org.sybila.parasim.visualisation.plot.impl.ResultPlotterConfiguration;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class StatusBar extends JPanel {

    private static final int PADDING = 2;
    private static final Border border = new CompoundBorder(new BevelBorder(BevelBorder.LOWERED),
            new EmptyBorder(PADDING, PADDING, PADDING, PADDING));

    private class StatusLabel extends JPanel {

        private static final int INSET = 5;
        private JLabel caption;

        public StatusLabel(String name) {
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            setBorder(border);

            JLabel cptn = new JLabel(name + ":");
            cptn.setFont(labelFont);
            add(cptn);
            add(Box.createRigidArea(new Dimension(INSET, INSET)));

            caption = new JLabel();
            caption.setFont(labelFont);
            add(caption);
        }

        public void setCaption(float value) {
            caption.setText(format.format(value));
        }
    }
    private StatusLabel[] labels;
    private Font labelFont;
    private DecimalFormat format;

    public StatusBar(ResultPlotterConfiguration conf, int dimension, PointVariableMapping names) {
        setLayout(new BorderLayout());
        labelFont = new Font("SansSerif", Font.PLAIN, conf.getStatusFontSize());
        StringBuilder formatBuilder = new StringBuilder("0.");
        for (int i = 0; i < conf.getStatusDecimalDigits(); i++) {
            formatBuilder.append('#');
        }
        format = new DecimalFormat(formatBuilder.toString());

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.LINE_AXIS));
        labels = new StatusLabel[dimension];
        for (int i = 0; i < dimension; i++) {
            labels[i] = new StatusLabel(names.getName(i));
            labelPanel.add(labels[i]);
        }
        add(labelPanel, BorderLayout.LINE_START);

        JPanel filler = new JPanel();
        filler.setBorder(border);
        add(filler, BorderLayout.CENTER);
    }

    public void setValue(int index, float value) {
        labels[index].setCaption(value);
    }
}
