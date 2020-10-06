package com.github.mikephil.chartingNIR.formatterNIR;

import com.github.mikephil.chartingNIR.dataNIR.EntryNIR;

/**
 * Interface that can be used to return a customized color instead of setting
 * colors via the setColor(...) method of the DataSet.
 * 
 * @author Philipp Jahoda
 */
public interface ColorFormatter {

    int getColor(EntryNIR e, int index);
}