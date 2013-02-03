/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.visualisation.plot.impl.layer;

import java.util.List;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.visualisation.plot.impl.LayerFactory;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class LayeredPointLayer extends OrthogonalBoundedPointLayer implements LayerFactory {

    public LayeredPointLayer(OrthogonalSpace bounds) {
        super(bounds);
    }

    protected abstract List<Layer> getLayers(int dim);

    @Override
    public int ticks(int index) {
        return getLayers(index).size();
    }

    @Override
    public int getTicks(int index, float value) {
        List<Layer> list = getLayers(index);

        //see if it is in layer//
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isIn(value)) {
                return i;
            }
        }

        //is before//
        if (value < list.get(0).getValue()) {
            return 0;
        }

        //is between or at the end//
        int i = 1;
        while ((i < list.size()) && (value < list.get(i).getValue())) {
            i++;
        }
        return i - 1;
    }

    @Override
    public float getValue(int index, int ticks) {
        return getLayers(index).get(ticks).getValue();
    }
}
