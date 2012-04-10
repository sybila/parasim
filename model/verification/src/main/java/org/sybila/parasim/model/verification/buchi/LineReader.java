/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.model.verification.buchi;

import java.util.ArrayList;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Enables reading files into an array of Strings and reporting errors.
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public class LineReader {

    private ArrayList<String> lines;
    private String error;

    public LineReader() {
        lines = new ArrayList<String>();
        error = null;
    }

    public boolean readLines(String fileName) {
        BufferedReader reader = null;
        String line;
        lines.clear();
        error = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            error = e.getMessage();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    error = e.getMessage();
                }
            }
        }
        if (error != null) {
            return false;
        } else {
            return true;
        }
    }

    public String getError() {
        return error;
    }

    public String[] getLines() {
        return (String[]) lines.toArray();
    }

    public ArrayList<String> getLinesList() {
        return lines;
    }
}
