
package com.github.mikephil.chartingNIR.dataNIR;

import java.util.ArrayList;
import java.util.List;

/**
 * Data object that allows the combination of Line-, Bar-, Scatter-, Bubble- and
 * CandleData. Used in the CombinedChart class.
 * 
 * @author Philipp Jahoda
 */
public class CombinedData extends BarLineScatterCandleBubbleData<BarLineScatterCandleBubbleDataSet<?>> {

    private LineDataNIR mLineDataNIR;
    private BarData mBarData;
    private ScatterData mScatterData;
    private CandleData mCandleData;
    private BubbleData mBubbleData;

    public CombinedData() {
        super();
    }

    public CombinedData(List<String> xVals) {
        super(xVals);
    }

    public CombinedData(String[] xVals) {
        super(xVals);
    }

    public void setData(LineDataNIR data) {
        mLineDataNIR = data;
        mDataSets.addAll(data.getDataSets());
        init();
    }

    public void setData(BarData data) {
        mBarData = data;
        mDataSets.addAll(data.getDataSets());
        init();
    }

    public void setData(ScatterData data) {
        mScatterData = data;
        mDataSets.addAll(data.getDataSets());
        init();
    }

    public void setData(CandleData data) {
        mCandleData = data;
        mDataSets.addAll(data.getDataSets());
        init();
    }

    public void setData(BubbleData data) {
        mBubbleData = data;
        mDataSets.addAll(data.getDataSets());
        init();
    }

    public BubbleData getBubbleData() {
        return mBubbleData;
    }

    public LineDataNIR getLineData() {
        return mLineDataNIR;
    }

    public BarData getBarData() {
        return mBarData;
    }

    public ScatterData getScatterData() {
        return mScatterData;
    }

    public CandleData getCandleData() {
        return mCandleData;
    }

    /**
     * Returns all data objects in row: line-bar-scatter-candle-bubble if not null.
     * @return
     */
    public List<ChartData> getAllData() {

        List<ChartData> data = new ArrayList<ChartData>();
        if(mLineDataNIR != null)
            data.add(mLineDataNIR);
        if(mBarData != null)
            data.add(mBarData);
        if(mScatterData != null)
            data.add(mScatterData);
        if(mCandleData != null)
            data.add(mCandleData);
        if(mBubbleData != null)
            data.add(mBubbleData);

        return data;
    }

    @Override
    public void notifyDataChanged() {
        if (mLineDataNIR != null)
            mLineDataNIR.notifyDataChanged();
        if (mBarData != null)
            mBarData.notifyDataChanged();
        if (mCandleData != null)
            mCandleData.notifyDataChanged();
        if (mScatterData != null)
            mScatterData.notifyDataChanged();
        if (mBubbleData != null)
            mBubbleData.notifyDataChanged();

        init(); // recalculate everything
    }
}
