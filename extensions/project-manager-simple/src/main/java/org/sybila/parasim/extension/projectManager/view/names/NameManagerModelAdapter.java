package org.sybila.parasim.extension.projectManager.view.names;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class NameManagerModelAdapter implements ExtendedNameManagerModel {

    private final NameManagerModel model;

    public NameManagerModelAdapter(NameManagerModel model) {
        if (model == null) {
            throw new IllegalArgumentException("Argument (model) is null.");
        }
        this.model = model;
    }

    @Override
    public void chooseCurrent(String name) {
        // do nothing //
    }

    @Override
    public void newName() {
        model.newName();
    }

    @Override
    public boolean removeCurrent(String name) {
        return model.removeCurrent(name);
    }

    @Override
    public boolean renameCurrent(String name, String newName) {
        return model.renameCurrent(name, newName);
    }

    @Override
    public boolean saveCurrent(String name) {
        return model.saveCurrent(name);
    }

    @Override
    public void selectionChanged(String name) {
        model.selectionChanged(name);
    }
}