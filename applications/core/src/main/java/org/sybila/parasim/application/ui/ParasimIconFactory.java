package org.sybila.parasim.application.ui;

import java.awt.Image;
import java.awt.Toolkit;

/**
 * Provides application windows with icons.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public enum ParasimIconFactory {

    INSTANCE;
    private final Image icon;

    private ParasimIconFactory() {
        icon = Toolkit.getDefaultToolkit().createImage(getClass().getClassLoader().getResource("icon-64.png"));
    }

    /**
     * Get parasim icon of default size.
     *
     * @return Parasim icon loaded as an image.
     */
    public Image getIcon() {
        return icon;
    }

    /**
     * Get instance of this factory.
     *
     * @return Instance of this factory.
     */
    public static ParasimIconFactory getInstance() {
        return INSTANCE;
    }
}
