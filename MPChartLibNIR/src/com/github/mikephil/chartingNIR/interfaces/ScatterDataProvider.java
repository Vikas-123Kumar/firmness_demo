package com.github.mikephil.chartingNIR.interfaces;

import com.github.mikephil.chartingNIR.dataNIR.ScatterData;

public interface ScatterDataProvider extends BarLineScatterCandleBubbleDataProvider {

    ScatterData getScatterData();
}
