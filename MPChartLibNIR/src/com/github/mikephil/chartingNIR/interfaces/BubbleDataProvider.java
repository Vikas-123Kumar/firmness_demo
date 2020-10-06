package com.github.mikephil.chartingNIR.interfaces;

import com.github.mikephil.chartingNIR.dataNIR.BubbleData;

public interface BubbleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    BubbleData getBubbleData();
}
