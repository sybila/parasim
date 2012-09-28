package org.sybila.parasim.extension.visualisation.projection.view.display.rule;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import org.sybila.parasim.extension.visualisation.projection.view.display.util.DimensionFunctional;
import org.sybila.parasim.extension.visualisation.projection.view.display.util.Orientation;
import org.sybila.parasim.visualisation.projection.api.scale.Scale;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class NaturalScaleFormat implements ScaleFormat {

    private static final Dimension MIN_SIZE = new Dimension(60, 30);

    private static int getMagnitude(double number) {
        return Double.valueOf(Math.ceil(Math.log10(number))).intValue();
    }

    private static String getWidestNumber(int precision, int magnitude, boolean negative, boolean halves) {
        StringBuilder result = new StringBuilder();
        if (negative) {
            result.append("-");
        }
        for (int i = 0; i < precision; i++) {
            result.append("0");
        }
        if (halves) {
            result.append(".5");
        }
        result.append("E");
        result.append(magnitude);
        return result.toString();
    }

    private static Iterable<TickModel> formatLinearScale(Scale scale, Orientation orientation, int size, int maxWidth, StringMeasure measure) {
        DimensionFunctional df = orientation.getDimensionFunctional();
        double minExtent = Math.abs(scale.getModelCoordinate(0) - scale.getModelCoordinate(orientation.getDimensionFunctional().get(MIN_SIZE)));
        int magnitude = getMagnitude(minExtent);

        double extent = Math.pow(10, magnitude);
        int tickWidth = scale.getViewCoordinate(Double.valueOf(scale.getModelCoordinate(0) + df.get(extent, -extent)).floatValue());
        boolean negative = (scale.getModelCoordinate(0) < 0) || (scale.getModelCoordinate(size) < 0);
        boolean halves = false;
        if (extent / 2 > minExtent) {
            extent /= 2;
            tickWidth /= 2;
            halves = true;
        }
        int precision = getMagnitude(((double) size) / tickWidth);

        while (precision > 1 && measure.getStringBounds(getWidestNumber(precision, magnitude, negative, halves)).width > df.get(tickWidth, MIN_SIZE.width)) {
            if (halves) {
                extent *= 2;
                tickWidth *= 2;
                halves = false;
            } else {
                extent *= 5;
                tickWidth *= 5;
                magnitude++;
                halves = true;
            }
            precision = getMagnitude(((double) size) / tickWidth);
        }
        double smallTickWidth = ((double) tickWidth) / (halves ? 5 : 10);

        List<TickModel> ticks = new ArrayList<>();

        int start = Double.valueOf(Math.ceil(scale.getModelCoordinate(df.get(0, size)) / extent)).intValue();
        for (int i = start; i * extent < scale.getModelCoordinate(df.get(size, 0)); i++) {
            StringBuilder label = new StringBuilder();
            if (halves) {
                label.append(i / 2);
                if (i % 2 == 1) {
                    label.append(".5");
                }
            } else {
                label.append(i);
            }
            label.append("E");
            label.append(magnitude);
            int value = scale.getViewCoordinate(Double.valueOf(i * extent).floatValue());
            ticks.add(new SimpleTickModel(value, label.toString()));

            double pos = value;
            for (int j = 1; j < (halves ? 5 : 10); j++) {
                pos += df.get(smallTickWidth, -smallTickWidth);
                ticks.add(new SimpleTickModel(Double.valueOf(pos).intValue(), null));
            }
        }

        for (double pos = start * extent; pos > scale.getModelCoordinate(df.get(0, size)); pos -= extent / (halves ? 5 : 10)) {
            ticks.add(new SimpleTickModel(scale.getViewCoordinate(Double.valueOf(pos).floatValue()), null));
        }
        return ticks;
    }

    @Override
    public Iterable<TickModel> formatHorizontalScale(Scale scale, int size, StringMeasure measure) {
        switch (scale.getType()) {
            case LINEAR:
                return formatLinearScale(scale, Orientation.HORIZONTAL, size, MIN_SIZE.width, measure);
            case LOGARITHMIC:
                throw new UnsupportedOperationException("Logarithmic scale not supported yet.");
            default:
                throw new IllegalArgumentException("Unknown scale type: " + scale.getType());
        }
    }

    @Override
    public Iterable<TickModel> formatVerticalScale(Scale scale, int size, StringMeasure measure, int maxWidth) {
        switch (scale.getType()) {
            case LINEAR:
                return formatLinearScale(scale, Orientation.VERTICAL, size, maxWidth, measure);
            case LOGARITHMIC:
                throw new UnsupportedOperationException("Logarithmic scale not supported yet.");
            default:
                throw new IllegalArgumentException("Unknown scale type: " + scale.getType());
        }
    }
}
