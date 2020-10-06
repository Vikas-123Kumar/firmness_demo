
package com.github.mikephil.chartingNIR.bufferNIR;

import com.github.mikephil.chartingNIR.dataNIR.EntryNIR;

import java.util.List;

public class CircleBuffer extends AbstractBuffer<EntryNIR> {

    public CircleBuffer(int size) {
        super(size);
    }

    protected void addCircle(float x, float y) {
        buffer[index++] = x;
        buffer[index++] = y;
    }

    @Override
    public void feed(List<EntryNIR> entries) {

        int size = (int)Math.ceil((mTo - mFrom) * phaseX + mFrom);

        for (int i = mFrom; i < size; i++) {

            EntryNIR e = entries.get(i);
            addCircle(e.getXIndex(), e.getVal() * phaseY);
        }
        
        reset();
    }
}
