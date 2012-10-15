/*
 * Copyright (c) 2012, Stephan Beisken. All rights reserved.
 *
 * This file is part of BiNChe.
 *
 * BiNChe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BiNChe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BiNChe. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.metware.binche.execs;

import org.apache.commons.cli.*;

import java.io.File;
import java.util.ArrayList;

/**
 * Abstract object for when a main is runnable on the command line. The object
 * effectively provides a wrapper for command option processing utilising the
 * Apache Commons CLI library
 *
 * @author johnmay <john.wilkinsonmay@gmail.com>
 */
public abstract class CommandLineMain extends ArrayList<Option> {

    private static final long serialVersionUID = -4753390544967053367L;
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CommandLineMain.class);

    private Options options = new Options();
    private CommandLineParser parser = new PosixParser();
    private CommandLine cmdLine = null;

    public abstract void setupOptions();

    /**
     * Main processing method
     */
    public abstract void process();

    public CommandLineMain(String[] args) {

        setupOptions();

        for (Option opt : this) {
            options.addOption(opt);
        }
        options.addOption(new Option("h", "help", false, "print the help section"));

        try {
            cmdLine = parser.parse(options, args);
        } catch (ParseException ex) {
            logger.error("There was a problem parsing command line options: " + ex.getMessage());
            printHelp();
        }

        if (cmdLine.hasOption('h') || cmdLine.hasOption("help")) {
            printHelp();
        }
    }

    public CommandLine getCommandLine() {

        return cmdLine;
    }

    public boolean hasOption(String option) {

        return getCommandLine().hasOption(option);
    }

    /**
     * Convenience method for accessing a file from the parsed options. Note the
     * method does not check if the file exists
     */
    public File getFileOption(String option) throws IllegalArgumentException {

        if (getCommandLine().hasOption(option)) {

            return new File(getCommandLine().getOptionValue(option));

        } else {

            throw new IllegalArgumentException();
        }

    }

    @SuppressWarnings("unchecked")
    public void printHelp() {

        for (Object obj : options.getOptions().toArray(new Option[0])) {
            Option opt = (Option) obj;
            System.out.println(String.format("  -%s|--%-30s ", opt.getOpt(), opt.getLongOpt()) + opt.getDescription());
        }
        System.exit(0);
    }
}
