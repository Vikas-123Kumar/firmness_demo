package com.github.mikephil.chartingNIR.formatterNIR;

import com.github.mikephil.chartingNIR.dataNIR.EntryNIR;
import com.github.mikephil.chartingNIR.utilsNIR.ViewPortHandler;

/**
 * Interface that allows custom formatting of all values inside the chart before they are
 * being drawn to the screen. Simply create your own formatting class and let
 * it implement ValueFormatter. Then override the getFormattedValue(...) method
 * and return whatever you want.
 *
 * @author Philipp Jahoda
 */
public interface ValueFormatter {

    /**
     * Called when a value (from labels inside the chart) is formatted
     * before being drawn. For performance reasons, avoid excessive calculations
     * and memory allocations inside this method.
     *
     * @param value           the value to be formatted
     * @param entryNIR           the entryNIR the value belongs to - in e.g. BarChart, this is of class BarEntryNIR
     * @param dataSetIndex    the index of the DataSet the entryNIR in focus belongs to
     * @param viewPortHandler provides information about the current chart state (scale, translation, ...)
     * @return the formatted label ready for being drawn
     */
    String getFormattedValue(float value, EntryNIR entryNIR, int dataSetIndex, ViewPortHandler viewPortHandler);
}
