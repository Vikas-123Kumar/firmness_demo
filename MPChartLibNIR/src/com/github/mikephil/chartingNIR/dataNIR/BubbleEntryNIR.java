
package com.github.mikephil.chartingNIR.dataNIR;

/**
 * Subclass of EntryNIR that holds a value for one entry in a BubbleChart. Bubble
 * chart implementation: Copyright 2015 Pierre-Marc Airoldi Licensed under
 * Apache License 2.0
 *
 * @author Philipp Jahoda
 */
public class BubbleEntryNIR extends EntryNIR {

    /** size value */
    private float mSize = 0f;

    /**
     * Constructor.
     *
     * @param xIndex The index on the x-axis.
     * @param val The value on the y-axis.
     * @param size The size of the bubble.
     */
    public BubbleEntryNIR(int xIndex, float val, float size) {
        super(val, xIndex);

        this.mSize = size;
    }

    /**
     * Constructor.
     *
     * @param xIndex The index on the x-axis.
     * @param val The value on the y-axis.
     * @param size The size of the bubble.
     * @param data Spot for additional data this EntryNIR represents.
     */
    public BubbleEntryNIR(int xIndex, float val, float size, Object data) {
        super(val, xIndex, data);

        this.mSize = size;
    }

    public BubbleEntryNIR copy() {

        BubbleEntryNIR c = new BubbleEntryNIR(getXIndex(), getVal(), mSize, getData());

        return c;
    }

    /**
     * Returns the size of this entry (the size of the bubble).
     *
     * @return
     */
    public float getSize() {
        return mSize;
    }

    public void setSize(float size) {
        this.mSize = size;
    }

}
