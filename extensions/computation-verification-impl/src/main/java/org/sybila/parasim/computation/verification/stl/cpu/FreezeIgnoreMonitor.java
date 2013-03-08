package org.sybila.parasim.computation.verification.stl.cpu;

import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.verification.api.Monitor;
import org.sybila.parasim.computation.verification.cpu.AbstractMonitor;
import org.sybila.parasim.model.verification.Property;
import org.sybila.parasim.model.verification.Robustness;

/**
 * Simple freeze monitor which ignores freeze formulae. Included for the case
 * when freeze operator is included in formula without freezing time.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class FreezeIgnoreMonitor extends AbstractMonitor {

    private final Monitor subMonitor;

    /**
     * Create freeze ignoring monitor from a underyling monitor.
     */
    public FreezeIgnoreMonitor(Property property, Monitor underlying) {
        super(property);
        Validate.notNull(underlying);
        subMonitor = underlying;
    }

    @Override
    public Collection<Monitor> getSubmonitors() {
        return Arrays.asList(subMonitor);
    }

    @Override
    public Robustness getRobustness(int index) {
        return subMonitor.getRobustness(index);
    }

    @Override
    public int size() {
        return subMonitor.size();
    }
}
