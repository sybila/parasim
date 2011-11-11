package org.sybila.parasim.suport.gpu.model.ode;

import java.util.Arrays;
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.CUdeviceptr;
import jcuda.runtime.JCuda;
import jcuda.runtime.cudaMemcpyKind;
import org.sybila.parasim.model.ode.ArrayOdeSystemEncoding;
import org.sybila.parasim.model.ode.DefaultOdeSystem;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.ode.OdeSystemEncoding;
import org.sybila.parasim.suport.gpu.Utils;

/**
 * WARNING: This class is not thread safe!
 * 
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class OdeSystemWorkspace {

    private CUdeviceptr deviceCoefficentIndexes;
    private CUdeviceptr deviceCoefficents;
    private CUdeviceptr deviceFactorIndexes;
    private CUdeviceptr deviceFactors;
    private int[] hostCoefficientIndexes;
    private float[] hostCoefficients;
    private int[] hostFactorIndexes;
    private int[] hostFactors;
    private boolean initialized = false;
    private OdeSystem savedOdeSystem;
    
    public void free() {
        if (!isInitialized()) {
            return;
        }
        // forget array pointers
        hostCoefficientIndexes = null;
        hostCoefficients = null;
        hostFactorIndexes = null;
        hostFactors = null;
        // forget saved ode system pointer
        savedOdeSystem = null;
        // free device memory
        Utils.checkErrorCode(JCuda.cudaFree(deviceCoefficentIndexes));
        Utils.checkErrorCode(JCuda.cudaFree(deviceCoefficents));
        Utils.checkErrorCode(JCuda.cudaFree(deviceFactorIndexes));
        Utils.checkErrorCode(JCuda.cudaFree(deviceFactors));
        // reset initialized
        initialized = false;        
    }

    public CUdeviceptr getCoefficientIndexesPointer() {
        if (!isInitialized()) {
            throw new IllegalStateException("The workspace is not initialized yet.");
        }
        return deviceCoefficentIndexes;
    }    
    
    public CUdeviceptr getCoefficientsPointer() {
        if (!isInitialized()) {
            throw new IllegalStateException("The workspace is not initialized yet.");
        }
        return deviceCoefficents;
    }
    
    public CUdeviceptr getFactorIndexesPointer() {
        if (!isInitialized()) {
            throw new IllegalStateException("The workspace is not initialized yet.");
        }
        return deviceFactorIndexes;
    }    
    
    public CUdeviceptr getFactorsPointer() {
        if (!isInitialized()) {
            throw new IllegalStateException("The workspace is not initialized yet.");
        }
        return deviceFactors;
    }
    
    public OdeSystem getSavedSystem() {
        if (savedOdeSystem == null) {
            throw new IllegalStateException("There is no saved ODE system.");
        }
        return savedOdeSystem;
    }
    
    public OdeSystem loadSystem() {
        if (!isInitialized()) {
            throw new IllegalStateException("The workspace is not initialized yet.");
        }
        // init new host arrays
        int[] newHostCoefficientIndexes = new int[hostCoefficientIndexes.length];
        float[] newHostCoefficients = new float[hostCoefficients.length];
        int[] newHostFactorIndexes = new int[hostFactorIndexes.length];
        int[] newHostFactors = new int[hostFactors.length];
        // copy data
        Utils.checkErrorCode(JCuda.cudaMemcpy(Pointer.to(newHostCoefficientIndexes), deviceCoefficentIndexes, newHostCoefficientIndexes.length * Sizeof.INT, cudaMemcpyKind.cudaMemcpyDeviceToHost));
        Utils.checkErrorCode(JCuda.cudaMemcpy(Pointer.to(newHostCoefficients), deviceCoefficents, newHostCoefficients.length * Sizeof.FLOAT, cudaMemcpyKind.cudaMemcpyDeviceToHost));
        Utils.checkErrorCode(JCuda.cudaMemcpy(Pointer.to(newHostFactorIndexes), deviceFactorIndexes, newHostFactorIndexes.length * Sizeof.INT, cudaMemcpyKind.cudaMemcpyDeviceToHost));
        Utils.checkErrorCode(JCuda.cudaMemcpy(Pointer.to(newHostFactors), deviceFactors, newHostFactors.length * Sizeof.INT, cudaMemcpyKind.cudaMemcpyDeviceToHost));
        // return a new system
        return new DefaultOdeSystem(new ArrayOdeSystemEncoding(newHostCoefficientIndexes, newHostCoefficients, newHostFactorIndexes, newHostFactors));
    }
    
    public void saveSystem(OdeSystem system) {
        if (system == null) {
            throw new IllegalArgumentException("The parameter system is null.");
        }
        OdeSystemEncoding encoding = system.encoding();
        if (!isInitialized() || encoding.countVariables() + 1 != hostCoefficientIndexes.length || encoding.countCoefficients() != hostCoefficients.length || encoding.countFactors() != hostFactors.length) {
            free();
            initialize(encoding);
        }
        // copy data into host arrays
        hostCoefficientIndexes[0] = 0;
        hostFactorIndexes[0] = 0;
        for(int v = 0; v < encoding.countVariables(); v++) {
            hostCoefficientIndexes[v+1] = hostCoefficientIndexes[v] + encoding.countCoefficients(v);
            for(int c = 0; c < encoding.countCoefficients(v); c++) {
                hostFactorIndexes[hostCoefficientIndexes[v] + c + 1] = hostFactorIndexes[hostCoefficientIndexes[v] + c] + encoding.countFactors(v, c);
                hostCoefficients[hostCoefficientIndexes[v] + c] = encoding.coefficient(v, c);
                for(int f = 0; f < encoding.countFactors(v, c); f++) {
                    hostFactors[hostFactorIndexes[hostCoefficientIndexes[v] + c]] = encoding.factor(v, c, f);
                }
            }
        }        
        // copy data from host to device
        Utils.checkErrorCode(JCuda.cudaMemcpy(deviceCoefficentIndexes, Pointer.to(hostCoefficientIndexes), hostCoefficientIndexes.length * Sizeof.INT, cudaMemcpyKind.cudaMemcpyHostToDevice));
        Utils.checkErrorCode(JCuda.cudaMemcpy(deviceCoefficents, Pointer.to(hostCoefficients), hostCoefficients.length * Sizeof.FLOAT, cudaMemcpyKind.cudaMemcpyHostToDevice));
        Utils.checkErrorCode(JCuda.cudaMemcpy(deviceFactorIndexes, Pointer.to(hostFactorIndexes), hostFactorIndexes.length * Sizeof.INT, cudaMemcpyKind.cudaMemcpyHostToDevice));
        Utils.checkErrorCode(JCuda.cudaMemcpy(deviceFactors, Pointer.to(hostFactors), hostFactors.length * Sizeof.INT, cudaMemcpyKind.cudaMemcpyHostToDevice));
        // update saved ode system
        this.savedOdeSystem = system;
    }
    
    private void initialize(OdeSystemEncoding encoding) {
        // init host arrays
        hostCoefficientIndexes = new int[encoding.countVariables() + 1];
        hostCoefficients = new float[encoding.countCoefficients()];
        hostFactorIndexes = new int[encoding.countCoefficients() + 1];
        hostFactors = new int[encoding.countFactors()];
        // init device pointers
        deviceCoefficentIndexes = new CUdeviceptr();
        deviceCoefficents = new CUdeviceptr();
        deviceFactorIndexes = new CUdeviceptr();
        deviceFactors = new CUdeviceptr();
        // malloc device memory
        Utils.checkErrorCode(JCuda.cudaMalloc(deviceCoefficentIndexes, hostCoefficientIndexes.length * Sizeof.INT));
        Utils.checkErrorCode(JCuda.cudaMalloc(deviceCoefficents, hostCoefficients.length * Sizeof.FLOAT));
        Utils.checkErrorCode(JCuda.cudaMalloc(deviceFactorIndexes, hostFactorIndexes.length * Sizeof.INT));
        Utils.checkErrorCode(JCuda.cudaMalloc(deviceFactors, hostFactors.length * Sizeof.INT));
        // set initialized
        initialized = true;
    }    
    
    private boolean isInitialized() {
        return initialized;
    }    
    
}
