package com.github.mikephil.chartingNIR.listener;

import com.github.mikephil.chartingNIR.dataNIR.EntryNIR;
import com.github.mikephil.chartingNIR.highlightNIR.Highlight;

/**
 * Listener for callbacks when selecting values inside the chart by
 * touch-gesture.
 * 
 * @author Philipp Jahoda
 */
public interface OnChartValueSelectedListener {

    /**
     * Called when a value has been selected inside the chart.
     * 
     * @param e The selected EntryNIR.
     * @param dataSetIndex The index in the datasets array of the data object
     *            the Entrys DataSet is in.
     * @param h the corresponding highlight object that contains information
     *            about the highlighted position
     */
    void onValueSelected(EntryNIR e, int dataSetIndex, Highlight h);

    /**
     * Called when nothing has been selected or an "un-select" has been made.
     */
    void onNothingSelected();
}
