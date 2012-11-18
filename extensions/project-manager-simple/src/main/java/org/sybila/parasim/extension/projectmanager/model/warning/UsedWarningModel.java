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
