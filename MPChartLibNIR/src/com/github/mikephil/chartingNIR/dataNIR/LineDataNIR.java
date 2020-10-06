
package com.github.mikephil.chartingNIR.dataNIR;

import java.util.ArrayList;
import java.util.List;

/**
 * Data object that encapsulates all data associated with a LineChart.
 * 
 * @author Philipp Jahoda
 */
public class LineDataNIR extends BarLineScatterCandleBubbleData<LineDataSetNIR> {

    public LineDataNIR() {
        super();
    }

    public LineDataNIR(List<String> xVals) {
        super(xVals);
    }

    public LineDataNIR(String[] xVals) {
        super(xVals);
    }

    public LineDataNIR(List<String> xVals, List<LineDataSetNIR> dataSets) {
        super(xVals, dataSets);
    }

    public LineDataNIR(String[] xVals, List<LineDataSetNIR> dataSets) {
        super(xVals, dataSets);
    }

    public LineDataNIR(List<String> xVals, LineDataSetNIR dataSet) {
        super(xVals, toList(dataSet));
    }

    public LineDataNIR(String[] xVals, LineDataSetNIR dataSet) {
        super(xVals, toList(dataSet));
    }

    private static List<LineDataSetNIR> toList(LineDataSetNIR dataSet) {
        List<LineDataSetNIR> sets = new ArrayList<LineDataSetNIR>();
        sets.add(dataSet);
        return sets;
    }
}
