package org.sybila.parasim.model.trajectory;

public class InheritingPoint extends ArrayPoint {

    private final Point parent;

    public InheritingPoint(Point parent, float time, float... data) {
        super(time, data);
        this.parent = parent;
    }

    public InheritingPoint(Point parent, float time, float[] data, int startIndex, int dimension) {
        super(time, data, startIndex, dimension);
        this.parent = parent;
    }

    @Override
    public float getValue(int index) {
        if (index >= super.getDimension()) {
            return parent.getValue(index);
        } else {
            return super.getValue(index);
        }
    }

    @Override
    public int getDimension() {
        return parent.getDimension();
    }

}
