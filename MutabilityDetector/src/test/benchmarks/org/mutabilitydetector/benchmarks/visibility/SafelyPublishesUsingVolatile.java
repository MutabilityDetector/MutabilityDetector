package org.mutabilitydetector.benchmarks.visibility;

/**
 * Non final fields are not guaranteed to be visible after publishing
 * an instance of their enclosing class. However, if the last assignment
 * is to a volatile variable, it will be guaranteed the preceding assignments
 * are visible[citation needed].
 * 
 *
 */
public final class SafelyPublishesUsingVolatile {

    private int x;
    private int y;
    private volatile int z;
    
    public SafelyPublishesUsingVolatile(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getZ() {
        return z;
    }
    
}
