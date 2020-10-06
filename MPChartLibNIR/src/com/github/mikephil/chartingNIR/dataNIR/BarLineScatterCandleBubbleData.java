
package com.github.mikephil.chartingNIR.dataNIR;

import java.util.List;

/**
 * Baseclass for all Line, Bar, Scatter, Candle and Bubble data.
 * 
 * @author Philipp Jahoda
 */
public abstract class BarLineScatterCandleBubbleData<T extends BarLineScatterCandleBubbleDataSet<? extends EntryNIR>>
        extends ChartData<T> {
    
    public BarLineScatterCandleBubbleData() {
        super();
    }
    
    public BarLineScatterCandleBubbleData(List<String> xVals) {
        super(xVals);
    }
    
    public BarLineScatterCandleBubbleData(String[] xVals) {
        super(xVals);
    }

    public BarLineScatterCandleBubbleData(List<String> xVals, List<T> sets) {
        super(xVals, sets);
    }

    public BarLineScatterCandleBubbleData(String[] xVals, List<T> sets) {
        super(xVals, sets);
    }
}
