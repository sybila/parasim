/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sybila.parasim.application.actions;

import org.sybila.parasim.application.ParasimOptions;
import org.sybila.parasim.core.api.Manager;

/**
 *
 * @author jpapouse
 */
public class Help extends AbstractAction<Void> {

    public Help(Manager manager, ParasimOptions options) {
        super(manager, options);
    }

    @Override
    public boolean isEnabled() {
        return getOptions().isHelp();
    }

    @Override
    public Void call() throws Exception {
        ParasimOptions.printHelp(System.out);
        return null;
    }

}
