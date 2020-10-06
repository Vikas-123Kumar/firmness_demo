package com.github.mikephil.chartingNIR.interfaces;

import com.github.mikephil.chartingNIR.dataNIR.CandleData;

public interface CandleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    CandleData getCandleData();
}
