package org.sybila.parasim.computation.lifecycle;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class AbstractComputationTest {
    
    protected class ComputationMock implements Computation {
        public ComputationController controller;

        public ComputationController getController() {
            return controller;
        }

        public ComputationInteraction getInteraction() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public ComputationPresentation getPresentation() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }    
    
}
