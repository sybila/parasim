package org.sybila.parasim.computation.density;

import org.sybila.parasim.computation.density.distancecheck.api.DistanceChecker;
import org.sybila.parasim.computation.density.spawn.api.TrajectorySpawner;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.ManagerImpl;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestLoadableExtension {
    
    @Test
    public void testLoad() throws Exception {
        System.setProperty("parasim.config.file", "src/test/resources/org/sybila/parasim/computation/density/parasim.xml");
        Manager manager = ManagerImpl.create();
        manager.start();
        assertNotNull(manager.resolve(TrajectorySpawner.class, manager.getRootContext()));
        assertNotNull(manager.resolve(DistanceChecker.class, manager.getRootContext()));
    }
    
}
