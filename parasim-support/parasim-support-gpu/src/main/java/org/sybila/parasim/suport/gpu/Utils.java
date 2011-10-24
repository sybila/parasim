package org.sybila.parasim.suport.gpu;

import jcuda.CudaException;
import jcuda.runtime.JCuda;
import jcuda.runtime.cudaError;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class Utils {
    
    /**
     * Checks whether the error code represents a success. If not
     * throws {@link CudaException}.
     * 
     * @param error 
     */
    public static void checkErrorCode(int error) {
        if (error != cudaError.cudaSuccess) {
            throw new CudaException(JCuda.cudaGetErrorString(error));
        }
    }
    
}
