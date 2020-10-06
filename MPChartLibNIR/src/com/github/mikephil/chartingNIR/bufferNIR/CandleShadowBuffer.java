
package com.github.mikephil.chartingNIR.bufferNIR;

import com.github.mikephil.chartingNIR.dataNIR.CandleEntryNIR;

import java.util.List;

public class CandleShadowBuffer extends AbstractBuffer<CandleEntryNIR> {

    public CandleShadowBuffer(int size) {
        super(size);
    }

    private void addShadow(float x1, float y1, float x2, float y2) {

        buffer[index++] = x1;
        buffer[index++] = y1;
        buffer[index++] = x2;
        buffer[index++] = y2;
    }

    @Override
    public void feed(List<CandleEntryNIR> entries) {

        int size = (int)Math.ceil((mTo - mFrom) * phaseX + mFrom);

        for (int i = mFrom; i < size; i++) {

            CandleEntryNIR e = entries.get(i);
            addShadow(e.getXIndex(), e.getHigh() * phaseY, e.getXIndex(), e.getLow() * phaseY);
        }

        reset();
    }
}
