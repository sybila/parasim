package org.sybila.parasim.application.model;

import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.computation.density.api.LimitedDistance;
import org.sybila.parasim.computation.density.api.PointDistanceMetric;
import org.sybila.parasim.computation.density.spawn.api.SpawnedDataBlock;
import org.sybila.parasim.computation.density.spawn.api.TrajectorySpawner;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.model.verification.result.VerifiedDataBlock;
import org.sybila.parasim.model.verification.result.VerifiedDataBlockResultAdapter;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ExperimentLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExperimentLauncher.class);

    private ExperimentLauncher() {
    }

    public static VerificationResult launch(Manager manager, Experiment experiment) throws Exception {
        TrajectorySpawner spawner = manager.resolve(TrajectorySpawner.class, Default.class, manager.getRootContext());
        final SpawnedDataBlock spawned = spawner.spawn(experiment.getInitialSpaceResource().getRoot(), experiment.getInitialSamplingResource().getRoot(), new PointDistanceMetric() {
                public LimitedDistance distance(float[] first, float[] second) {
                    return null;
                }
                public LimitedDistance distance(Point first, Point second) {
                    return null;
                }
            });
        return new VerifiedDataBlockResultAdapter<Trajectory>(new VerifiedDataBlock<Trajectory>() {

                public float getRobustness(int index) {
                    float result;
                    if (index < spawned.size()) {
                        result = 1;
                    } else {
                        result = -1;
                    }
                    LOGGER.debug("[" + index + "] : [" + result + "]");
                    return result;
                }

                public Trajectory getTrajectory(int index) {
                    if (index < spawned.size()) {
                        return spawned.getTrajectory(index);
                    } else {
                        return spawned.getSecondaryTrajectories().getTrajectory(index - spawned.size());
                    }
                }

                public int size() {
                    return spawned.size() + spawned.getSecondaryTrajectories().size();
                }

                public Iterator<Trajectory> iterator() {
                    return new Iterator<Trajectory>() {
                        private int index = 0;

                        public boolean hasNext() {
                            return index < size();
                        }

                        public Trajectory next() {
                            index++;
                            return getTrajectory(index - 1);
                        }

                        public void remove() {
                            throw new UnsupportedOperationException("Not supported yet.");
                        }
                    };
                }
            });
    }

}
