package org.sybila.parasim.extension.visualisation.projection.view.display.rule;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
class SimpleTickModel implements TickModel {

    private int pos;
    private String cap;

    public SimpleTickModel(int position, String caption) {
        pos = position;
        cap = caption;
    }

    @Override
    public String getCaption() {
        return cap;
    }

    @Override
    public int getPosition() {
        return pos;
    }
}
