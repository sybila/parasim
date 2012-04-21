package org.sybila.parasim.visualisation.plot.impl.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import javax.swing.JPanel;
import javax.swing.plaf.DimensionUIResource;
import org.sybila.parasim.visualisation.plot.impl.ResultPlotterConfiguration;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class Rule extends JPanel {

    public static enum Orientation {

        HORIZONTAL,
        VERTICAL;

        public boolean isHorizontal() {
            return equals(Orientation.HORIZONTAL);
        }
    }
    private static final Color BLANK = Color.WHITE;
    private static final Color TICKS = Color.BLACK;
    private static final int PADDING = 5;
    // non-static //
    private ResultPlotterConfiguration conf;
    private Orientation orient;
    private float min, max;
    private DecimalFormat format;

    public Rule(ResultPlotterConfiguration conf, Orientation orientation) {
        this.conf = conf;
        orient = orientation;

        setLayout(null);

        if (orient.isHorizontal()) {
            setPreferredSize(new Dimension(Integer.MAX_VALUE, conf.getRuleHeight()));
        } else {
            setPreferredSize(new DimensionUIResource(conf.getRuleWidth(), Integer.MAX_VALUE));
        }

        StringBuilder formatBuilder = new StringBuilder("0.");
        for (int i = 0; i < conf.getRuleDecimalDigits(); i++) {
            formatBuilder.append('#');
        }
        format = new DecimalFormat(formatBuilder.toString());
    }

    public void update(float min, float max) {
        this.min = min;
        this.max = max;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D canvas = (Graphics2D) g;
        Rectangle bounds = canvas.getClipBounds();

        canvas.setColor(BLANK);
        canvas.fill(bounds);

        canvas.setColor(TICKS);

        int start;
        if (orient.isHorizontal()) {
            start = bounds.x + Canvas.PADDING;
        } else {
            start = bounds.y + bounds.height - Canvas.PADDING;
        }
        int size = (orient.isHorizontal() ? bounds.width : bounds.height) - 2 * Canvas.PADDING;
        int numTicks = size / conf.getRuleTickSpacing() + 1;

        for (int i = 0; i < numTicks; i++) {
            int length = (i % conf.getRuleTickRatio() == 0) ? conf.getRuleBigTick() : conf.getRuleSmalltick();
            int span = i * conf.getRuleTickSpacing();
            int coord = start;
            if (orient.isHorizontal()) {
                coord += span;
                canvas.drawLine(coord, bounds.x, coord, bounds.x + length);
            } else {
                coord -= span;
                int end = bounds.y + bounds.width;
                canvas.drawLine(end - length, coord, end, coord);
            }
        }
        int numBigTicks = size / conf.getRuleTickSpacing() / conf.getRuleTickRatio() + 1;
        float perTick = (max - min) * conf.getRuleTickSpacing() * conf.getRuleTickRatio() / size;

        //g.setFont(new Font("SansSerif", Font.PLAIN, 10));

        for (int i = 0; i < numBigTicks; i++) {
            String label = format.format(min + i * perTick);
            int span = i * conf.getRuleTickSpacing() * conf.getRuleTickRatio();
            int coord = start;
            if (orient.isHorizontal()) {
                coord += span;
                g.drawString(label, coord, bounds.y + bounds.height - PADDING);
            } else {
                coord -= span;
                g.drawString(label, bounds.y + PADDING, coord);
            }
        }


        /*removeAll();
         int numBigTicks = size / conf.getRuleTickSpacing() / conf.getRuleTickRatio() + 1;
         float perTick = (max - min) * conf.getRuleTickSpacing() * conf.getRuleTickRatio() / size;
         JLabel[] labels = generateLabels(numBigTicks, perTick);
         for (int i = 0; i < numBigTicks; i++) {
         JLabel target = labels[i];
         int span = i * conf.getRuleTickSpacing() * conf.getRuleTickRatio();
         int coord = start;
         if (orient.isHorizontal()) {
         coord += span;
         target.setLocation(coord - target.getSize().width / 2, bounds.x + conf.getRuleBigTick() + PADDING);
         } else {
         coord -= span;
         target.setLocation(bounds.y + bounds.width - PADDING - target.getSize().width, coord - target.getSize().height / 2);
         }
         }*/
    }
}
