package org.sybila.parasim.computation.density;

import org.sybila.parasim.model.trajectory.LimitedPointDistanceMetric;
import java.util.concurrent.ExecutionException;
import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.computation.density.distancecheck.api.DistanceChecker;
import org.sybila.parasim.computation.density.spawn.api.SpawnedDataBlock;
import org.sybila.parasim.computation.density.spawn.api.TrajectorySpawner;
import org.sybila.parasim.computation.lifecycle.api.ComputationContainer;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.ManagerImpl;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.extension.cdi.api.ServiceFactory;
import org.sybila.parasim.model.MergeableBox;
import org.sybila.parasim.model.computation.AbstractComputation;
import org.sybila.parasim.model.computation.Computation;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestInteractionWithComputation extends AbstractDensityTest{

    private Manager manager;
    @Inject
    private ComputationContainer computationContainer;

    @BeforeMethod
    public void startManager() throws Exception {
        System.setProperty("parasim.config.file", "src/test/resources/org/sybila/parasim/computation/density/parasim.xml");
        manager = ManagerImpl.create();
        manager.start();
    }

    @BeforeMethod(dependsOnMethods={"startManager"})
    public void injectFields() {
        manager.resolve(ServiceFactory.class, Default.class, manager.getRootContext()).injectFields(this, manager.getRootContext());
    }

    @AfterMethod
    public void stopManager() {
        manager.shutdown();
    }

    @Test
    public void testInitialSpawn() throws InterruptedException, ExecutionException {
        int dimension = 3;
        int toSpawn = 5;
        Computation<MergeableInteger> computation = new TestComputation(
                createInitialSpace(100, dimension),
                createInitialSampling(createInitialSpace(100, dimension), toSpawn),
                createPointDistanceMetric(1, 10));
        MergeableInteger result = computationContainer.compute(computation).get();
        assertEquals(result.get().intValue(), (int) Math.pow(toSpawn, dimension));
    }

    private static class TestComputation extends AbstractComputation<MergeableInteger> {

        private OrthogonalSpace initialSpace;
        private InitialSampling initialSampling;
        private LimitedPointDistanceMetric pointDistanceMetric;
        @Inject
        private TrajectorySpawner trajectorySpawner;
        @Inject
        private DistanceChecker distanceChecker;

        public TestComputation(OrthogonalSpace initialSpace, InitialSampling initialSampling, LimitedPointDistanceMetric pointDistanceMetric) {
            this.initialSampling = initialSampling;
            this.initialSpace = initialSpace;
            this.pointDistanceMetric = pointDistanceMetric;
        }

        public MergeableInteger compute() {
            SpawnedDataBlock spawnedDataBlock = trajectorySpawner.spawn(initialSpace, initialSampling);
            return new MergeableInteger(spawnedDataBlock.size() + spawnedDataBlock.getSecondaryTrajectories().size());
        }

        public Computation<MergeableInteger> cloneComputation() {
            return new TestComputation(initialSpace, initialSampling, pointDistanceMetric);
        }
    }

    private static class MergeableInteger extends MergeableBox<MergeableInteger, Integer> {
        public MergeableInteger(Integer load) {
            super(load);
        }
        public MergeableInteger merge(MergeableInteger toMerge) {
            return new MergeableInteger(get() + toMerge.get());
        }

    }

}
