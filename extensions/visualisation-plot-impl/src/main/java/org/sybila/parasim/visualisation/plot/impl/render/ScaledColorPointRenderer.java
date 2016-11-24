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
package org.sybila.parasim.visualisation.plot.impl.render;

import java.awt.Color;
import java.awt.Graphics2D;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.visualisation.plot.impl.ResultPlotterConfiguration;
import org.sybila.parasim.visualisation.plot.impl.gui.PointRenderer;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ScaledColorPointRenderer extends PointRendererDecorator {

    private final Color invalid, valid, invalid_max, valid_max;
    private final float min, max;

    public ScaledColorPointRenderer(PointRenderer renderer, Color valid, Color invalid, float minRobustness, float maxRobustness) {
        super(renderer);
        if (minRobustness >= 0) {
            throw new IllegalArgumentException("Minimum robustness should be negative.");
        }
        if (maxRobustness <= 0) {
            throw new IllegalArgumentException("Maximum robustness should be positive.");
        }
        min = minRobustness;
        max = maxRobustness;
        Color mid = brighten(scaleColor(valid, invalid, 0.5f));
        this.valid = scaleColor(mid, valid, 0.5f);
        this.invalid = scaleColor(mid, invalid, 0.5f);
        invalid_max = scaleColor(invalid, Color.BLACK, 0.5f);
        valid_max = scaleColor(valid, Color.BLACK, 0.5f);
    }

    private static int scale(int start, int end, float scale) {
        return Double.valueOf(start * (1 - scale) + end * scale).intValue();
    }

    private static Color scaleColor(Color start, Color end, float scale) {
        if (scale < 0 || scale > 1) {
            throw new IllegalArgumentException("Scale factor should be between zero and one.");
        }
        int red = scale(start.getRed(), end.getRed(), scale);
        int blue = scale(start.getBlue(), end.getBlue(), scale);
        int green = scale(start.getGreen(), end.getGreen(), scale);
        return new Color(red, green, blue);
    }

    private static Color brighten(Color target) {
        int R = target.getRed();
        int G = target.getGreen();
        int B = target.getBlue();
        int dB = Color.WHITE.getBlue() - B;
        int dR = Color.WHITE.getRed() - R;
        int dG = Color.WHITE.getGreen() - G;
        int d = Math.min(dG, Math.min(dR, dB));
        return new Color((R != 0) ? (R + d) : 0, (G != 0) ? (G + d) : 0, (B != 0) ? (B + d) : 0);
    }

    @Override
    protected void decorateCanvas(Graphics2D canvas, float x, float y, float robustness) {
        if (robustness > 0) {
            canvas.setPaint(scaleColor(valid, valid_max, robustness / max));
        } else {
            canvas.setPaint(scaleColor(invalid, invalid_max, robustness / min));
        }
    }

    public static PointRenderer getFromResult(ResultPlotterConfiguration conf, VerificationResult result, PointRenderer contents) {
        float min = 0;
        float max = 0;
        for (int i = 0; i < result.size(); i++) {
            Robustness robustness = result.getRobustness(i);
            float value = robustness.getValue();
            if (value < min) {
                min = value;
            } else if (robustness.getValue() > max) {
                max = value;
            }
        }
        if (Math.abs(min) < conf.getMinimumDifference() || max < conf.getMinimumDifference()) {
            return new ValidityPointRenderer(conf);
        }
        return new ScaledColorPointRenderer(contents, conf.getPointColorValid(), conf.getPointColorInvalid(), min, max);
    }

    public static PointRenderer getCircleRenderer(ResultPlotterConfiguration conf, VerificationResult result) {
        return new ZeroRemover(getFromResult(conf, result, new CirclePointRenderer(conf.getPointRadius())), conf);
    }
}
