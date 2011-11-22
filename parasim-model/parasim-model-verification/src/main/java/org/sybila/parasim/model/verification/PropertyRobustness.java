package org.sybila.parasim.model.verification;

import org.sybila.parasim.model.distance.Distance;

/**
 * Represents the robustness of a property on a single trajectory of an OdeSystem.
 *
 * It is either a number or a vector stating how much another trajectory may
 * deviate from this one in every dimension and still have the same property
 * validity.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public interface PropertyRobustness extends Distance
{

}
