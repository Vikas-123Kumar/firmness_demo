package com.github.mikephil.chartingNIR.interfaces;

import com.github.mikephil.chartingNIR.componentsNIR.YAxis.AxisDependency;
import com.github.mikephil.chartingNIR.dataNIR.BarLineScatterCandleBubbleData;
import com.github.mikephil.chartingNIR.utilsNIR.Transformer;

public interface BarLineScatterCandleBubbleDataProvider extends ChartInterface {

    Transformer getTransformer(AxisDependency axis);
    int getMaxVisibleCount();
    boolean isInverted(AxisDependency axis);
    
    int getLowestVisibleXIndex();
    int getHighestVisibleXIndex();

    BarLineScatterCandleBubbleData getData();
}
