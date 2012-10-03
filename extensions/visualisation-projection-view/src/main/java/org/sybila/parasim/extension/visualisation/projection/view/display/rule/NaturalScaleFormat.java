package org.sybila.parasim.extension.visualisation.projection.view.display.rule;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.sybila.parasim.extension.visualisation.projection.view.util.DimensionFunctional;
import org.sybila.parasim.extension.visualisation.projection.view.util.Orientation;
import org.sybila.parasim.visualisation.projection.api.scale.InverseScale;
import org.sybila.parasim.visualisation.projection.api.scale.Scale;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public enum NaturalScaleFormat implements ScaleFormat {

    INSTANCE;
    private static final Dimension MIN_SIZE = new Dimension(60, 30);
    private static final int MAX_MAGNITUDE = 4;
    private static final int LOG_SMALL_TICK_DIVIDER = 3;

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

    private static String getWholeDecimal(int magnitude) {
        StringBuilder result = new StringBuilder();
        if (magnitude >= 0) {
            result.append("1");
            for (int i = 0; i < magnitude; i++) {
                result.append("0");
            }
        } else {
            result.append("0.");
            for (int i = magnitude; i < -1; i++) {
                result.append("0");
            }
            result.append("1");
        }
        return result.toString();
    }

    private static void divideExtent(Collection<TickModel> ticks, int minPos, int maxPos) {
        int pos = minPos + (maxPos - minPos) / LOG_SMALL_TICK_DIVIDER;
        if (Math.abs(minPos - pos) > 1) {
            ticks.add(new SimpleTickModel(pos, null));
            divideExtent(ticks, pos, maxPos);
        }
    }

    private static Iterable<TickModel> formatLogarithmicScale(Scale scale, Orientation orientation, int size, int maxWidth, StringMeasure measure) {
        DimensionFunctional df = orientation.getDimensionFunctional();
        Scale adjScale = df.get(scale, InverseScale.getFromSize(scale, size));


        double minExtent = adjScale.getModelCoordinate(df.get(MIN_SIZE)) / adjScale.getModelCoordinate(0);
        int magnitude = getMagnitude(minExtent);
        double extentFactor = Math.pow(10, magnitude);
        int tickWidth = adjScale.getViewCoordinate(Double.valueOf(adjScale.getModelCoordinate(0) * extentFactor).floatValue());
        if (orientation == Orientation.HORIZONTAL) {
            double minMagnitude = getMagnitude(adjScale.getModelCoordinate(0));
            double maxMagnitude = getMagnitude(adjScale.getModelCoordinate(size));

            while (tickWidth < measure.getStringBounds("1E" + minMagnitude).width || tickWidth < measure.getStringBounds("1E" + maxMagnitude).width) {
                extentFactor *= 10;
                tickWidth *= 2;
                magnitude++;
            }
        }

        List<TickModel> ticks = new ArrayList<>();
        int startMagnitude = getMagnitude(adjScale.getModelCoordinate(0));
        double pos = Math.pow(10, startMagnitude);
        for (int i = startMagnitude; pos < adjScale.getModelCoordinate(size); pos *= extentFactor, i += magnitude) {
            int value = scale.getViewCoordinate(Double.valueOf(pos).floatValue());
            String caption;
            if (Math.abs(i) <= MAX_MAGNITUDE) {
                caption = getWholeDecimal(i);
                if (measure.getStringBounds(caption).width > df.get(tickWidth, maxWidth)) {
                    caption = "1E" + Integer.toString(i);
                }
            } else {
                caption = "1E" + Integer.toString(i);
            }
            ticks.add(new SimpleTickModel(value, caption));
            divideExtent(ticks, value, value + df.get(tickWidth, -tickWidth));
        }

        return ticks;
    }

    @Override
    public Iterable<TickModel> formatHorizontalScale(Scale scale, int size, StringMeasure measure) {
        switch (scale.getType()) {
            case LINEAR:
                return formatLinearScale(scale, Orientation.HORIZONTAL, size, MIN_SIZE.width, measure);
            case LOGARITHMIC:
                return formatLogarithmicScale(scale, Orientation.HORIZONTAL, size, MIN_SIZE.width, measure);
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
                return formatLogarithmicScale(scale, Orientation.VERTICAL, size, maxWidth, measure);
            default:
                throw new IllegalArgumentException("Unknown scale type: " + scale.getType());
        }
    }

    public static ScaleFormat getInstance() {
        return INSTANCE;
    }
}
