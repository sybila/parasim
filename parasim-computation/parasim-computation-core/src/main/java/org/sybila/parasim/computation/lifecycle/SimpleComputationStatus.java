package org.sybila.parasim.computation.lifecycle;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SimpleComputationStatus implements ComputationStatus {

        private boolean finalized;
        private boolean finished;
        private boolean initialized;
        private boolean started;
        
        private long lastConsumedTime = 0;
        private long lastStartRunningTime = -1;
        private long totalConsumedTime = 0;
        
        private int numberOfRunning;
        
        public boolean isFinalized() {
            return finalized;
        }
        
        public boolean isFinished() {
            return finished;
        }
        
        public boolean isInitialized() {
            return initialized;
        }

        public boolean isStarted() {
            return started;
        }

        public boolean isRunning() {
            return numberOfRunning > 0;
        }

        public long getLastConsumedTime() {
            if (isRunning()) {
                throw new IllegalStateException("The computation is running.");
            }
            return lastConsumedTime;
        }

        public long getTotalConsumedTime() {
            return totalConsumedTime;
        }    
        
        public void setFinalized() {
            finalized = true;
        }
        
        public void setFinished() {
            finished = true;
        }
        
        public void setInitialized() {
            initialized = true;
        }
        
        public void startRunning() {
            numberOfRunning++;
            started = true;
            synchronized(this) {
                if (lastStartRunningTime == -1) {
                    lastStartRunningTime = System.currentTimeMillis();
                }
            }
        }
        
        public void stopRunning() {
            synchronized(this) {
                numberOfRunning--;
                if (numberOfRunning == 0) {
                    lastConsumedTime = System.currentTimeMillis() - lastStartRunningTime;
                    totalConsumedTime += lastConsumedTime;
                    lastStartRunningTime = -1;
                    
                }
            }            
        }
}
