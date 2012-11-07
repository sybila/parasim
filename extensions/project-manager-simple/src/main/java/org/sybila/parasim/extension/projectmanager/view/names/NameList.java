package org.sybila.parasim.extension.projectmanager.view.names;

import java.util.Collections;
import java.util.Set;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface NameList {

    public void addName(String name);

    public void removeName(String name);

    public void renameName(String name, String newName);

    public void selectName(String name);

    public Set<String> getNames();

    public static class Adapter implements NameList {

        @Override
        public void addName(String name) {
        }

        @Override
        public void removeName(String name) {
        }

        @Override
        public void renameName(String name, String newName) {
        }

        @Override
        public void selectName(String name) {
        }

        @Override
        public Set<String> getNames() {
            return Collections.<String>emptySet();
        }
    }
}
