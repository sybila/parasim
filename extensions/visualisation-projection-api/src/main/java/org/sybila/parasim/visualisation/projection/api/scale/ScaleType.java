package org.sybila.parasim.visualisation.projection.api.scale;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public enum ScaleType {

    LINEAR {

        @Override
        public Scale getFromSizes(float min, float max, int size) {
            return LinearScale.getFromSizes(min, max, size);
        }
    },
    LOGARITHMIC {

        @Override
        public Scale getFromSizes(float min, float max, int size) {
            return LogarithmicScale.getFromSizes(min, max, size);
        }
    };

    public abstract Scale getFromSizes(float min, float max, int size);
}
