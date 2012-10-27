package org.sybila.parasim.util;

/**
 * Counter of lock and unlock actions. Non-concurrent.
 *
 * Essentially a stack of "lock" actions, which are canceled by "unlock"
 * actions. It is in "accessible state" when the stack is empty.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class SimpleLock {

    private int lock = 0;

    /**
     * Constructs unlocked counter.
     */
    public SimpleLock() {
    }

    /**
     * Query the counter.
     *
     * @return
     * <code>true</code> when the stack is empty,
     * <code>false</code> otherwise.
     */
    public boolean isAccessible() {
        return lock == 0;
    }

    /**
     * Add "lock" to the top of the stack.
     *
     * @throws IllegalStateException on counter overflow.
     */
    public void lock() {
        if (lock == Integer.MAX_VALUE) {
            throw new IllegalStateException("Integer overflow.");
        }
        lock++;
    }

    /**
     * Remove the top of the stack.
     */
    public void unlock() {
        if (lock != 0) {
            lock--;
        }
    }
}
