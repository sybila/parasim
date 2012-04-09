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
