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
package org.sybila.parasim.extension.projectmanager.model.warning;

import org.sybila.parasim.extension.projectmanager.view.frame.WarningLabel;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class UsedWarningModel {

    private final WarningLabel label;
    private final UsedControler controler;
    private String name;

    public UsedWarningModel(WarningLabel label, UsedControler controler) {
        if (label == null) {
            throw new IllegalArgumentException("Argument (label) is null.");
        }
        if (controler == null) {
            throw new IllegalArgumentException("Argument (controler) is null.");
        }
        this.label = label;
        this.controler = controler;
    }

    public void setName(String name) {
        this.name = name;
        check();
    }

    public void check() {
        if (name == null) {
            label.setVisible(false);
        } else {
            label.setVisible(controler.isUsed(name));
        }
    }
}
