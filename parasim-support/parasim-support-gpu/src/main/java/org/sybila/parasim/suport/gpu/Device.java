package org.sybila.parasim.suport.gpu;

import jcuda.runtime.cudaDeviceProp;
import jcuda.runtime.JCuda;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class Device {
    
    private GpuContext context;
    private int id;
    private cudaDeviceProp properties;
    
    public Device(GpuContext context, int id) {
        if (context == null) {
            throw new IllegalArgumentException("The parameter context is null.");
        }
        if (id < 0) {
            throw new IllegalArgumentException("The device id has to be a non negative  number.");
        }
        if (id >= context.getNumberOfDevices()) {
            throw new IllegalArgumentException("The device id has to be lower than number of available GPU devices.");
        }
        this.context = context;
        this.id = id;
    }
    
    public GpuContext getContext() {
        return context;
    }
    
    public cudaDeviceProp getDeviceProperties() {
        if (properties == null) {
            properties = new cudaDeviceProp();
            JCuda.cudaGetDeviceProperties(properties, id);
        }
        return properties;
    }
    
    public int getId() {
        return id;
    }
    
}
