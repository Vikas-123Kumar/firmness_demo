package com.github.mikephil.chartingNIR.utilsNIR;

import com.github.mikephil.chartingNIR.dataNIR.EntryNIR;

import java.util.Comparator;

/**
 * Comparator for comparing EntryNIR-objects by their x-index.
 * Created by philipp on 17/06/15.
 */
public class EntryXIndexComparator implements Comparator<EntryNIR> {
    @Override
    public int compare(EntryNIR entryNIR1, EntryNIR entryNIR2) {
        return entryNIR1.getXIndex() - entryNIR2.getXIndex();
    }
}
