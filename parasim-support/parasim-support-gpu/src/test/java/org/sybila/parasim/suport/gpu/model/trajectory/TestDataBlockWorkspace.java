package org.sybila.parasim.suport.gpu.model.trajectory;

import java.util.ArrayList;
import java.util.List;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.ListDataBlock;
import org.sybila.parasim.model.trajectory.ListTrajectory;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.suport.gpu.AbstractGpuTest;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestDataBlockWorkspace extends AbstractGpuTest {

    private DataBlock<Trajectory> trajectories;
    private DataBlockWorkspace workspace;

    @BeforeMethod(alwaysRun = true)
    public void prepareWorkspace() {
        workspace = new DataBlockWorkspace();
    }

    @AfterMethod
    public void destroyWorkspac() {
        workspace.free();
    }
    
    @Test
    public void testSaveAndLoad() {
        if (!getContext().isAvailable()) {
            throw new SkipException("The CUDA is not available.");
        }
        DataBlock<Trajectory> expected = getTrajectories();
        workspace.saveDataBlock(expected);
        DataBlock<Trajectory> loaded = workspace.loadDataBlock();
        for (int t = 0; t < 10; t++) {
            for (int p = 0; p < 5; p++) {
                for (int dim = 0; dim < 2; dim++) {
                    assertEquals(loaded.getTrajectory(t).getPoint(p).getValue(dim), expected.getTrajectory(t).getPoint(p).getValue(dim), "Trajectory number <" + t +"> doesn't match in point <" + p +"> in dimension <" + dim + ">.");
                }
                assertEquals(loaded.getTrajectory(t).getPoint(p).getTime(), loaded.getTrajectory(t).getPoint(p).getTime(), "Trajectory number <" + t +"> doesn't match in point <" + p +"> in time.");
            }
        }
    }

    @Test
    public void testSaveAndLoadLimit() {
        if (!getContext().isAvailable()) {
            return;
        }
        DataBlock<Trajectory> expected = getTrajectories();
        workspace.saveDataBlock(expected, 0, 3);
        DataBlock<Trajectory> loaded = workspace.loadDataBlock();
        for (int t = 0; t < 10; t++) {
            assertEquals(loaded.getTrajectory(t).getLength(), 3, "The trajectory length doesn't match.");
            for (int p = 0; p < 3; p++) {
                for (int dim = 0; dim < 2; dim++) {
                    assertEquals(loaded.getTrajectory(t).getPoint(p).getValue(dim), expected.getTrajectory(t).getPoint(p).getValue(dim), "Trajectory number <" + t +"> doesn't match in point <" + p +"> in dimension <" + dim + ">.");
                }
                assertEquals(loaded.getTrajectory(t).getPoint(p).getTime(), loaded.getTrajectory(t).getPoint(p).getTime(), "Trajectory number <" + t +"> doesn't match in point <" + p +"> in time.");
            }
        }        
    }
    
    private DataBlock<Trajectory> getTrajectories() {
        if (trajectories == null) {
            List<Trajectory> newTrajectories = new ArrayList<Trajectory>();
            for (int t = 0; t < 10; t++) {
                List<Point> points = new ArrayList<Point>();
                for (int p = 0; p < 5; p++) {
                    float[] data = new float[2];
                    for (int dim = 0; dim < 2; dim++) {
                        data[dim] = (float) (t + p * 0.1 + 0.01 * dim);
                    }
                    points.add(new ArrayPoint(p, data));
                }
                newTrajectories.add(new ListTrajectory(points));
            }
            return new ListDataBlock<Trajectory>(newTrajectories);
        }
        return trajectories;
    }
}
