package org.sybila.parasim.extension.projectmanager.model.project;

import java.io.File;
import java.util.Set;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface FormulaResourceList {

    public Set<String> getNames();

    public boolean add(String name, File target);

    public boolean rename(String name, String newName);

    public void remove(String name);

    public boolean isUsedInExperiment(String name);
}
