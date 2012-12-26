package org.sybila.parasim.extension.exporter.impl;

import java.awt.Color;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.SimpleRobustness;
import org.sybila.parasim.model.verification.result.VerificationResult;

public class RobustnessColorScale {

    private final Robustness max;
    private final Robustness min;

    public RobustnessColorScale(Robustness max, Robustness min) {
        if (max.getValue() < 0) {
            this.max = new SimpleRobustness(0);
        } else {
            this.max = max;
        }
        if (min.getValue() > 0) {
            this.min = new SimpleRobustness(0);
        } else {
            this.min = min;
        }
    }

    public static RobustnessColorScale of(VerificationResult result) {
        Robustness max = null;
        Robustness min = null;
        for (int i=0; i<result.size(); i++) {
            if (max == null || max.getValue() < result.getRobustness(i).getValue()) {
                max = result.getRobustness(i);
            }
            if (min == null || min.getValue() > result.getRobustness(i).getValue()) {
                min = result.getRobustness(i);
            }
        }
        return new RobustnessColorScale(max, min);
    }

    public Color getColor(Robustness robustness) {
        if (robustness.getValue() > 0) {
            if (max.getValue() == 0) {
                return Color.GREEN;
            } else {
                return scaleColor(Color.GREEN, Color.BLACK, robustness.getValue() / max.getValue());
            }
        } else {
            if (min.getValue() == 0) {
                return Color.GREEN;
            } else {
                return scaleColor(Color.RED, Color.BLACK, robustness.getValue() / min.getValue());
            }
        }
    }

    protected Color scaleColor(Color start, Color end, double scale) {
        if (scale < 0 || scale > 1) {
            throw new IllegalArgumentException("Scale factor should be between zero and one.");
        }
        int red = scale(start.getRed(), end.getRed(), scale);
        int blue = scale(start.getBlue(), end.getBlue(), scale);
        int green = scale(start.getGreen(), end.getGreen(), scale);
        return new Color(red, green, blue);
    }

    protected int scale(int start, int end, double scale) {
        return Double.valueOf(start * (1 - scale) + end * scale).intValue();
    }

}
