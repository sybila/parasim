package org.sybila.parasim.computation;

/**
 * Represents a computational job and enables it to be
 * - carried out using the ComputationController interface
 * - affected by the used using the ComputationInteraction interface
 * - visualizes by providing information through the ComputationPresentation interface.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dra�an</a>
 */
public interface Computation
{
    ComputationController getController();

    ComputationInteraction getInteraction();

    ComputationPresentation getPresentation();
}
