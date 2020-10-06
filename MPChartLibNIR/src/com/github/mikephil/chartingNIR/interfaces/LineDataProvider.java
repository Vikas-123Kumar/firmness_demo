package com.github.mikephil.chartingNIR.interfaces;

import com.github.mikephil.chartingNIR.componentsNIR.YAxis;
import com.github.mikephil.chartingNIR.dataNIR.LineDataNIR;

public interface LineDataProvider extends BarLineScatterCandleBubbleDataProvider {

    LineDataNIR getLineData();

    YAxis getAxis(YAxis.AxisDependency dependency);
}
