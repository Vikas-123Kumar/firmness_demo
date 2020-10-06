
package com.github.mikephil.chartingNIR.rendererNIR;

import android.graphics.Canvas;
import android.graphics.PointF;

import com.github.mikephil.chartingNIR.chartsNIR.RadarChartNIR;
import com.github.mikephil.chartingNIR.componentsNIR.XAxis;
import com.github.mikephil.chartingNIR.utilsNIR.Utils;
import com.github.mikephil.chartingNIR.utilsNIR.ViewPortHandler;

public class XAxisRendererRadarChart extends XAxisRenderer {

    private RadarChartNIR mChart;

    public XAxisRendererRadarChart(ViewPortHandler viewPortHandler, XAxis xAxis, RadarChartNIR chart) {
        super(viewPortHandler, xAxis, null);

        mChart = chart;
    }

    @Override
    public void renderAxisLabels(Canvas c) {

        if (!mXAxis.isEnabled() || !mXAxis.isDrawLabelsEnabled())
            return;

        final float labelRotationAngleDegrees = mXAxis.getLabelRotationAngle();
        final PointF drawLabelAnchor = new PointF(0.5f, 0.0f);

        mAxisLabelPaint.setTypeface(mXAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mXAxis.getTextSize());
        mAxisLabelPaint.setColor(mXAxis.getTextColor());

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        PointF center = mChart.getCenterOffsets();

        int mod = mXAxis.mAxisLabelModulus;
        for (int i = 0; i < mXAxis.getValues().size(); i += mod) {
            String label = mXAxis.getValues().get(i);

            float angle = (sliceangle * i + mChart.getRotationAngle()) % 360f;

            PointF p = Utils.getPosition(center, mChart.getYRange() * factor
                    + mXAxis.mLabelRotatedWidth / 2f, angle);

            drawLabel(c, label, i, p.x, p.y - mXAxis.mLabelRotatedHeight / 2.f,
                    drawLabelAnchor, labelRotationAngleDegrees);
        }
    }

	/**
	 * XAxis LimitLines on RadarChart not yet supported.
	 *
	 * @param c
	 */
	@Override
	public void renderLimitLines(Canvas c) {
		// this space intentionally left blank
	}
}
