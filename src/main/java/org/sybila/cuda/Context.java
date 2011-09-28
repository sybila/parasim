package org.sybila.cuda;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import jcuda.runtime.JCuda;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class Context {

    private Collection<Device> devices;

    public boolean isAvailable() {
        System.out.println(JCuda.CUDART_VERSION);
        return JCuda.CUDART_VERSION != 0;
    }
    
    public Collection<Device> getDevices() {
        if (devices == null) {
            int[] deviceCount = new int[1];
            JCuda.cudaGetDeviceCount(deviceCount);
            Collection<Device> newDevices = new ArrayList<Device>();
            for (int id=0; id<deviceCount[0]; id++) {
                newDevices.add(new Device(this, id));
            }
            devices = Collections.unmodifiableCollection(newDevices);
        }
        return devices;
    }
    
}
