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
package org.sybila.parasim.application.cli;

import java.io.PrintStream;
import java.io.PrintWriter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.Validate;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ParasimOptions {

    public static final String VERSION = "1.0.0-SNAPSHOT";
    private static Options options;
    private CommandLine line;

    private ParasimOptions(CommandLineParser parser, String[] args) throws ParseException {
        Validate.notNull(parser);
        Validate.notNull(args);
        line = parser.parse(getOptions(), args);
    }

    public static ParasimOptions create(String[] args) throws ParseException {
        return new ParasimOptions(new GnuParser(), args);
    }

    public static Options getOptions() {
        if (options == null) {
            Option config = new Option("c", "config", true, "specify config file, it uses 'parasim.xml' if not specified");
            config.setArgName("file");
            Option experiment = new Option("e", "experiment", true, "specify experiment properties file");
            experiment.setArgName("file");
            Option result = new Option("r", "result", false, "do not execute experiment, only print result");
            Option help = new Option("h", "help", false, "show help");
            Option version = new Option("v", "version", false, "show version");
            Option cvs = new Option("csv", "csv", true, "specify CSV file");
            cvs.setArgName("file");
            options = new Options().addOption(config).addOption(experiment).addOption(result).addOption(help).addOption(version).addOption(cvs);
        }
        return options;
    }

    public static void printHelp(PrintStream out) {
        PrintWriter output = new PrintWriter(out);
        output.println();
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printUsage(output, HelpFormatter.DEFAULT_WIDTH, "parasim", getOptions());
        output.println();
        helpFormatter.printOptions(output, HelpFormatter.DEFAULT_WIDTH, getOptions(), HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD);
        output.println();
        output.flush();
    }

    public static void printVersion(PrintStream out) {
        PrintWriter output = new PrintWriter(out);
        output.println();
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printWrapped(output, HelpFormatter.DEFAULT_WIDTH, "Parasim CLI " + VERSION + " - tool for parallel simulation and verification ");
        output.flush();
    }

    public String getConfiFile() {
        return line.getOptionValue("c");
    }

    public String getExperimentFile() {
        return line.getOptionValue("e");
    }

    public boolean isResultOnly() {
        return line.hasOption("r");
    }

    public boolean isHelp() {
        return line.hasOption("h");
    }

    public boolean isVersion() {
        return line.hasOption("v");
    }

    public String getCsvFile() {
        return line.getOptionValue("csv");
    }
}
