package org.sybila.parasim.suport.gpu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jcuda.runtime.JCuda;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class GpuContext {
    
    private List<Device> devices;
    private int isAvailable = -1;
    private int numberOfDevices = -1;
   
    public List<Device> getDevices() {
        if (devices == null) {
            List<Device> loadedDevices = new ArrayList<Device>();
            for(int id=0; id<getNumberOfDevices(); id++) {
                loadedDevices.add(new Device(this, id));
            }
            devices = Collections.unmodifiableList(loadedDevices);
        }
        return devices;
    }
    
    public int getNumberOfDevices() {
        if (numberOfDevices == -1) {
            int[] number = new int[1];
            JCuda.cudaGetDeviceCount(number);
            numberOfDevices = number[0];
        }
        return numberOfDevices;
    }
    
    public boolean isAvailable() {
        if (isAvailable == -1) {
            try {
                getNumberOfDevices();
                isAvailable = 1;
            }
            catch (Exception e) {
                isAvailable = 0;
            }
        }
        return isAvailable == 1;
    }
   
}