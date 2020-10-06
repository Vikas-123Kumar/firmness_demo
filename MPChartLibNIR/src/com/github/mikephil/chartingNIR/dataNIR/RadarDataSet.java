
package com.github.mikephil.chartingNIR.dataNIR;

import java.util.ArrayList;
import java.util.List;

public class RadarDataSet extends LineRadarDataSet<EntryNIR> {
    
    public RadarDataSet(List<EntryNIR> yVals, String label) {
        super(yVals, label);
    }

    @Override
    public DataSet<EntryNIR> copy() {

        List<EntryNIR> yVals = new ArrayList<EntryNIR>();

        for (int i = 0; i < mYVals.size(); i++) {
            yVals.add(mYVals.get(i).copy());
        }

        RadarDataSet copied = new RadarDataSet(yVals, getLabel());
        copied.mColors = mColors;
        copied.mHighLightColor = mHighLightColor;

        return copied;
    }
}
