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
public class Shutdown extends AbstractAction<Void> {

    public Shutdown(Manager manager, ParasimOptions options) {
        super(manager, options);
    }

    @Override
    public boolean isEnabled() {
        return getOptions().isBatch();
    }

    @Override
    public Void call() throws Exception {
        getManager().destroy();
        return null;
    }

}
