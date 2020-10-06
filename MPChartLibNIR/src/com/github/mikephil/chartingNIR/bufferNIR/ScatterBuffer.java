
package com.github.mikephil.chartingNIR.bufferNIR;

import com.github.mikephil.chartingNIR.dataNIR.EntryNIR;

import java.util.List;

public class ScatterBuffer extends AbstractBuffer<EntryNIR> {
    
    public ScatterBuffer(int size) {
        super(size);
    }

    protected void addForm(float x, float y) {
        buffer[index++] = x;
        buffer[index++] = y;
    }

    @Override
    public void feed(List<EntryNIR> entries) {
        
        float size = entries.size() * phaseX;
        
        for (int i = 0; i < size; i++) {

            EntryNIR e = entries.get(i);
            addForm(e.getXIndex(), e.getVal() * phaseY);
        }
        
        reset();
    }
}
