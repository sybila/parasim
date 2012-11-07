package org.sybila.parasim.extension.projectManager.model.projectImpl;

import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.extension.projectManager.names.ExperimentSuffixes;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class FileManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileManager.class);
    private final File dir;
    private final ExperimentSuffixes suffix;

    public FileManager(File directory, ExperimentSuffixes fileExtension) {
        if (directory == null) {
            throw new IllegalArgumentException("Argument (directory) is null.");
        }
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Target file is not a directory.");
        }
        dir = directory;
        suffix = fileExtension;
    }

    public File getFile(String name) {
        if (suffix != null) {
            name = suffix.add(name);
        }
        return new File(dir, name);
    }

    public File createFile(String name) {
        File target = getFile(name);
        try {
            if (target.createNewFile()) {
                return target;
            } else {
                return null;
            }
        } catch (IOException ioe) {
            LOGGER.warn("Unable to create file `" + target.toString() + "'.", ioe);
            return null;
        }
    }

    public void deleteFile(String name) {
        File target = getFile(name);
        if (!target.delete()) {
            target.deleteOnExit();
            LOGGER.error("Unable to delete `" + target.toString() + "'. Will try removing it on program exit.");
        }
    }
}
