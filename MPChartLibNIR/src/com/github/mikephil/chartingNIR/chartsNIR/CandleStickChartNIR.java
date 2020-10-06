
package com.github.mikephil.chartingNIR.chartsNIR;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.chartingNIR.dataNIR.CandleData;
import com.github.mikephil.chartingNIR.interfaces.CandleDataProvider;
import com.github.mikephil.chartingNIR.rendererNIR.CandleStickChartRenderer;

/**
 * Financial chart type that draws candle-sticks (OHCL chart).
 * 
 * @author Philipp Jahoda
 */
public class CandleStickChartNIR extends BarLineChartNIRBase<CandleData> implements CandleDataProvider {

    public CandleStickChartNIR(Context context) {
        super(context);
    }

    public CandleStickChartNIR(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CandleStickChartNIR(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mRenderer = new CandleStickChartRenderer(this, mAnimator, mViewPortHandler);
        mXChartMin = -0.5f;
    }

    @Override
    protected void calcMinMax() {
        super.calcMinMax();

        mXChartMax += 0.5f;
        mDeltaX = Math.abs(mXChartMax - mXChartMin);
    }

    @Override
    public CandleData getCandleData() {
        return mData;
    }
}
