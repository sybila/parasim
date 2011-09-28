package org.sybila.cuda;

import jcuda.CudaException;
import jcuda.runtime.cudaDeviceProp;
import jcuda.runtime.cudaError;
import jcuda.runtime.JCuda;

/**
 * CUDA device descriptor
 * 
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class Device {
   
    private int id;
    private Context context;
    private cudaDeviceProp properties;
    
    /**
     * Creates a new instance of device descriptor
     * 
     * @param context CUDA context
     * @param id device id
     */
    public Device(Context context, int id) {
        if (context == null) {
            throw new IllegalArgumentException("The parameter context is null.");
        }        
        if (id < 0) {
            throw new IllegalArgumentException("The parameter id has to be a positive number.");
        }
        this.context = context;
        this.id = id;
    }
    
    /**
     * Returns using CUDA cotext
     * @return CUDA context
     */
    public Context getContext() {
        return context;
    }
    
    /**
     * Returns device id
     * 
     * @return device id
     */
    public int getId() {
        return id;
    }
    
    /**
     * Returns device properties 
     * 
     * @return device properties
     * @throws CudaException if an error occurs during finding the information about this device
     */
    public cudaDeviceProp getProperties() {
        if (properties == null) {
            properties = new cudaDeviceProp();  
            int error = JCuda.cudaGetDeviceProperties(properties, id);
            if (error != cudaError.cudaSuccess) {
                throw new CudaException(JCuda.cudaGetErrorString(error));
            }
        }
        return properties;
    }
    
    /**
     * Sets this device as the current device for the calling host thread.
     * 
     * This call may be made from any host thread, to any device, and at any time.
     * This function will do no synchronization with the previous or new device,
     * and should be considered a very low overhead call. 
     * 
     * @throws CudaException if an error occurs during the setting.
     */
    public void use() {
        int error = JCuda.cudaSetDevice(id);
        if (error != cudaError.cudaSuccess) {
            throw new CudaException(JCuda.cudaGetErrorString(error));
        }
    }
    
}
