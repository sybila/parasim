package org.sybila.parasim.model.verification.result;

import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 *  Adapter from {@link VerifiedDataBlock} to {@link VerificationResult}.
 * 
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class VerifiedDataBlockResultAdapter<T extends Trajectory> extends AbstractVerificationResult {
    private VerifiedDataBlock<T> data;

    public VerifiedDataBlockResultAdapter(VerifiedDataBlock<T> data) {
        this.data = data;
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public Point getPoint(int index) {
        return data.getTrajectory(index).getFirstPoint();
    }

    @Override
    public float getRobustness(int index) {
        return data.getRobustness(index);
    }
    
}
