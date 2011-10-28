package org.sybila.parasim.suport.gpu.model.trajectory;

import java.util.Arrays;
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.CUdeviceptr;
import jcuda.runtime.JCuda;
import jcuda.runtime.cudaMemcpyKind;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.suport.gpu.Utils;

/**
 * WARNING: This class is not thread safe!
 * 
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class DataBlockWorkspace {

    private WorkspaceDataBlockFactory dataBlockFactory;
    private CUdeviceptr deviceLengths;
    private CUdeviceptr devicePoints;
    private CUdeviceptr deviceTimes;
    private int[] hostLengths;
    private float[] hostPoints;
    private float[] hostTimes;
    private boolean initialized = false;
    private DataBlock<Trajectory> savedDataBlock;

    public DataBlockWorkspace() {
        this(new DefaultWorkspaceDataBlockFactory());
    }

    public DataBlockWorkspace(WorkspaceDataBlockFactory dataBlockFactory) {
        if (dataBlockFactory == null) {
            throw new IllegalArgumentException("The parameter dataBlockFactory is null.");
        }
        this.dataBlockFactory = dataBlockFactory;
    }

    public DataBlockWorkspace(int size, int length, int dimension) {
        this(new DefaultWorkspaceDataBlockFactory(), size, length, dimension);
    }

    public DataBlockWorkspace(WorkspaceDataBlockFactory dataBlockFactory, int size, int length, int dimension) {
        this(dataBlockFactory);
        if (size <= 0) {
            throw new IllegalArgumentException("The parameter size has to be a positive number.");
        }
        if (length <= 0) {
            throw new IllegalArgumentException("The parameter length has to be a positive number.");
        }
        if (dimension <= 0) {
            throw new IllegalArgumentException("The parameter dimension has to be a positive number.");
        }
        initialize(size, length, dimension);
    }

    public void free() {
        if (!isInitialized()) {
            return;
        }
        // forget array pointers
        hostLengths = null;
        hostPoints = null;
        hostTimes = null;
        // forget saved data block pointer
        savedDataBlock = null;
        // free device memory
        Utils.checkErrorCode(JCuda.cudaFree(deviceLengths));
        Utils.checkErrorCode(JCuda.cudaFree(devicePoints));
        Utils.checkErrorCode(JCuda.cudaFree(deviceTimes));
        // reset initialized
        initialized = false;
    }

    public CUdeviceptr getLenghtsPointer() {
        if (!isInitialized()) {
            throw new IllegalStateException("The workspace is not initialized yet.");
        }
        return deviceLengths;
    }

    public CUdeviceptr getPointsPointer() {
        if (!isInitialized()) {
            throw new IllegalStateException("The workspace is not initialized yet.");
        }
        return devicePoints;
    }

    public CUdeviceptr getTimesPointer() {
        if (!isInitialized()) {
            throw new IllegalStateException("The workspace is not initialized yet.");
        }
        return deviceTimes;
    }

    public DataBlock<Trajectory> loadDataBlock() {
        if (!isInitialized()) {
            throw new IllegalStateException("The workspace is not initialized yet.");
        }
        int[] newHostLengths = new int[getSavedDataBlock().size()];
        Utils.checkErrorCode(JCuda.cudaMemcpy(Pointer.to(newHostLengths), deviceLengths, getSavedDataBlock().size() * Sizeof.INT, cudaMemcpyKind.cudaMemcpyDeviceToHost));
        int maxLength = 0;
        for (int i = 0; i < getSavedDataBlock().size(); i++) {
            if (newHostLengths[i] > maxLength) {
                maxLength = newHostLengths[i];
            }
        }
        int dimension = getSavedDataBlock().getTrajectory(0).getDimension();
        float[] newHostPoints = new float[dimension * maxLength * getSavedDataBlock().size()];
        float[] newHostTimes = new float[maxLength * getSavedDataBlock().size()];
        Utils.checkErrorCode(JCuda.cudaMemcpy(Pointer.to(newHostPoints), devicePoints, getSavedDataBlock().size() * maxLength * getSavedDataBlock().getTrajectory(0).getDimension() * Sizeof.FLOAT, cudaMemcpyKind.cudaMemcpyDeviceToHost));
        Utils.checkErrorCode(JCuda.cudaMemcpy(Pointer.to(newHostTimes), deviceTimes, getSavedDataBlock().size() * maxLength * Sizeof.FLOAT, cudaMemcpyKind.cudaMemcpyDeviceToHost));
        return dataBlockFactory.createDataBlock(newHostPoints, newHostTimes, newHostLengths, dimension);
    }

    public void saveDataBlock(DataBlock<Trajectory> trajectories) {
        saveDataBlock(trajectories, getMaxDataBlockLength(trajectories), true);
    }

    public void saveDataBlock(DataBlock<Trajectory> trajectories, int lengthLimit) {
        saveDataBlock(trajectories, lengthLimit, false);
    }

    private DataBlock<Trajectory> getSavedDataBlock() {
        if (savedDataBlock == null) {
            throw new IllegalStateException("There is no saved data block.");
        }
        return savedDataBlock;
    }

    private int getMaxDataBlockLength(DataBlock<Trajectory> trajectories) {
        int maxLength = 0;
        for (Trajectory trajectory : trajectories) {
            if (maxLength < trajectory.getLength()) {
                maxLength = trajectory.getLength();
            }
        }
        return maxLength;
    }

    private void initialize(int size, int length, int dimension) {
        // init host arrays
        hostLengths = new int[size];
        hostPoints = new float[size * length * dimension];
        hostTimes = new float[size * length];
        // init device pointers
        deviceLengths = new CUdeviceptr();
        devicePoints = new CUdeviceptr();
        deviceTimes = new CUdeviceptr();
        // malloc device memory
        Utils.checkErrorCode(JCuda.cudaMalloc(deviceLengths, size * Sizeof.INT));
        Utils.checkErrorCode(JCuda.cudaMalloc(devicePoints, size * length * dimension * Sizeof.FLOAT));
        Utils.checkErrorCode(JCuda.cudaMalloc(deviceTimes, size * length * Sizeof.FLOAT));
        // set initialized
        initialized = true;
    }

    private boolean isInitialized() {
        return initialized;
    }

    private void saveDataBlock(DataBlock<Trajectory> trajectories, int lengthLimit, boolean lengthLimitIsNeeded) {
        if (trajectories == null) {
            throw new IllegalArgumentException("The parameter trajectories is null.");
        }
        int dimension = trajectories.getTrajectory(0).getDimension();
        int length = lengthLimitIsNeeded ? lengthLimit : Math.min(getMaxDataBlockLength(trajectories), lengthLimit);
        // check whether the allocated memeory is big enough
        // if not -> reinit
        if (!isInitialized() || trajectories.size() > hostLengths.length || trajectories.size() * length > hostTimes.length || trajectories.size() * length * dimension > hostPoints.length) {
            free();
            initialize(trajectories.size(), length, dimension);
        }
        // copy data to host arrays
        int trajectoryIndex = 0;
        int pointIndex;
        for (Trajectory trajectory : trajectories) {
            if (dimension != trajectory.getDimension()) {
                throw new IllegalArgumentException("The dimension of trajectories in data block are different!");
            }
            hostLengths[trajectoryIndex] = Math.min(trajectory.getLength(), length);
            pointIndex = 0;
            for (Point point : trajectory) {
                // length limit has been reached
                if (pointIndex >= lengthLimit) {
                    break;
                }
                // copy point to host arrays
                System.arraycopy(point.toArray(), 0, hostPoints, pointIndex * trajectories.size() * dimension + trajectoryIndex * dimension, dimension);
                hostTimes[pointIndex * trajectories.size() + trajectoryIndex] = point.getTime();
                pointIndex++;
            }
            trajectoryIndex++;
        }
        // copy data from host to device
        Utils.checkErrorCode(JCuda.cudaMemcpy(devicePoints, Pointer.to(hostPoints), trajectories.size() * length * dimension * Sizeof.FLOAT, cudaMemcpyKind.cudaMemcpyHostToDevice));
        Utils.checkErrorCode(JCuda.cudaMemcpy(deviceTimes, Pointer.to(hostTimes), trajectories.size() * length * Sizeof.FLOAT, cudaMemcpyKind.cudaMemcpyHostToDevice));
        Utils.checkErrorCode(JCuda.cudaMemcpy(deviceLengths, Pointer.to(hostLengths), trajectories.size() * Sizeof.INT, cudaMemcpyKind.cudaMemcpyHostToDevice));
        // update saved data block
        savedDataBlock = trajectories;
    }
}
