package com.github.mikephil.chartingNIR.formatterNIR;

import com.github.mikephil.chartingNIR.dataNIR.LineDataSetNIR;
import com.github.mikephil.chartingNIR.interfaces.LineDataProvider;

/**
 * Interface for providing a custom logic to where the filling line of a LineDataSetNIR
 * should end. This of course only works if setFillEnabled(...) is set to true.
 * 
 * @author Philipp Jahoda
 */
public interface FillFormatter {

    /**
     * Returns the vertical (y-axis) position where the filled-line of the
     * LineDataSetNIR should end.
     * 
     * @param dataSet the LineDataSetNIR that is currently drawn
     * @param dataProvider
     * @return
     */
    float getFillLinePosition(LineDataSetNIR dataSet, LineDataProvider dataProvider);
}
