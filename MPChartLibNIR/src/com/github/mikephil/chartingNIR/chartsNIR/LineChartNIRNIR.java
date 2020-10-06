
package com.github.mikephil.chartingNIR.chartsNIR;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.chartingNIR.dataNIR.LineDataNIR;
import com.github.mikephil.chartingNIR.interfaces.LineDataProvider;
import com.github.mikephil.chartingNIR.rendererNIR.LineChartRenderer;

/**
 * Chart that draws lines, surfaces, circles, ...
 * 
 * @author Philipp Jahoda
 */
public class LineChartNIRNIR extends BarLineChartNIRBase<LineDataNIR> implements LineDataProvider {

    public LineChartNIRNIR(Context context) {
        super(context);
    }

    public LineChartNIRNIR(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LineChartNIRNIR(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mRenderer = new LineChartRenderer(this, mAnimator, mViewPortHandler);
    }

    @Override
    protected void calcMinMax() {
        super.calcMinMax();

        if (mDeltaX == 0 && mData.getYValCount() > 0)
            mDeltaX = 1;
    }
    
    @Override
    public LineDataNIR getLineData() {
        return mData;
    }

    @Override
    protected void onDetachedFromWindow() {
        // releases the bitmap in the renderer to avoid oom error
        if(mRenderer != null && mRenderer instanceof LineChartRenderer) {
            ((LineChartRenderer) mRenderer).releaseBitmap();
        }
        super.onDetachedFromWindow();
    }
}
