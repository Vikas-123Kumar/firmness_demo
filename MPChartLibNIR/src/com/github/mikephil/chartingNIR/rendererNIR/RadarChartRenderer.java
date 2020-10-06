
package com.github.mikephil.chartingNIR.rendererNIR;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import com.github.mikephil.chartingNIR.animationNIR.ChartAnimator;
import com.github.mikephil.chartingNIR.chartsNIR.RadarChartNIR;
import com.github.mikephil.chartingNIR.dataNIR.EntryNIR;
import com.github.mikephil.chartingNIR.dataNIR.RadarData;
import com.github.mikephil.chartingNIR.dataNIR.RadarDataSet;
import com.github.mikephil.chartingNIR.highlightNIR.Highlight;
import com.github.mikephil.chartingNIR.utilsNIR.Utils;
import com.github.mikephil.chartingNIR.utilsNIR.ViewPortHandler;

import java.util.List;

public class RadarChartRenderer extends LineScatterCandleRadarRenderer {

    protected RadarChartNIR mChart;

    /**
     * paint for drawing the web
     */
    protected Paint mWebPaint;

    public RadarChartRenderer(RadarChartNIR chart, ChartAnimator animator,
                              ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;

        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.STROKE);
        mHighlightPaint.setStrokeWidth(2f);
        mHighlightPaint.setColor(Color.rgb(255, 187, 115));

        mWebPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWebPaint.setStyle(Paint.Style.STROKE);
    }

    public Paint getWebPaint() {
        return mWebPaint;
    }

    @Override
    public void initBuffers() {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawData(Canvas c) {

        RadarData radarData = mChart.getData();

        for (RadarDataSet set : radarData.getDataSets()) {

            if (set.isVisible() && set.getEntryCount() > 0)
                drawDataSet(c, set);
        }
    }

    protected void drawDataSet(Canvas c, RadarDataSet dataSet) {

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        PointF center = mChart.getCenterOffsets();

        List<EntryNIR> entries = dataSet.getYVals();

        Path surface = new Path();

        boolean hasMovedToPoint = false;

        for (int j = 0; j < entries.size(); j++) {

            mRenderPaint.setColor(dataSet.getColor(j));

            EntryNIR e = entries.get(j);

            PointF p = Utils.getPosition(center, (e.getVal() - mChart.getYChartMin()) * factor,
                    sliceangle * j + mChart.getRotationAngle());

            if (Float.isNaN(p.x))
                continue;

            if (!hasMovedToPoint) {
                surface.moveTo(p.x, p.y);
                hasMovedToPoint = true;
            } else
                surface.lineTo(p.x, p.y);
        }

        surface.close();

        // draw filled
        if (dataSet.isDrawFilledEnabled()) {
            mRenderPaint.setStyle(Paint.Style.FILL);
            mRenderPaint.setAlpha(dataSet.getFillAlpha());
            c.drawPath(surface, mRenderPaint);
            mRenderPaint.setAlpha(255);
        }

        mRenderPaint.setStrokeWidth(dataSet.getLineWidth());
        mRenderPaint.setStyle(Paint.Style.STROKE);

        // draw the line (only if filled is disabled or alpha is below 255)
        if (!dataSet.isDrawFilledEnabled() || dataSet.getFillAlpha() < 255)
            c.drawPath(surface, mRenderPaint);
    }

    @Override
    public void drawValues(Canvas c) {

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        PointF center = mChart.getCenterOffsets();

        float yoffset = Utils.convertDpToPixel(5f);

        for (int i = 0; i < mChart.getData().getDataSetCount(); i++) {

            RadarDataSet dataSet = mChart.getData().getDataSetByIndex(i);

            if (!dataSet.isDrawValuesEnabled() || dataSet.getEntryCount() == 0)
                continue;

            // apply the text-styling defined by the DataSet
            applyValueTextStyle(dataSet);

            List<EntryNIR> entries = dataSet.getYVals();

            for (int j = 0; j < entries.size(); j++) {

                EntryNIR entryNIR = entries.get(j);

                PointF p = Utils.getPosition(center, (entryNIR.getVal() - mChart.getYChartMin()) * factor,
                        sliceangle * j + mChart.getRotationAngle());

                drawValue(c, dataSet.getValueFormatter(), entryNIR.getVal(), entryNIR, i, p.x, p.y - yoffset);
            }
        }
    }

    @Override
    public void drawExtras(Canvas c) {
        drawWeb(c);
    }

    protected void drawWeb(Canvas c) {

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();
        float rotationangle = mChart.getRotationAngle();

        PointF center = mChart.getCenterOffsets();

        // draw the web lines that come from the center
        mWebPaint.setStrokeWidth(mChart.getWebLineWidth());
        mWebPaint.setColor(mChart.getWebColor());
        mWebPaint.setAlpha(mChart.getWebAlpha());

        final int xIncrements = 1 + mChart.getSkipWebLineCount();

        for (int i = 0; i < mChart.getData().getXValCount(); i += xIncrements) {

            PointF p = Utils.getPosition(center, mChart.getYRange() * factor, sliceangle * i
                    + rotationangle);

            c.drawLine(center.x, center.y, p.x, p.y, mWebPaint);
        }

        // draw the inner-web
        mWebPaint.setStrokeWidth(mChart.getWebLineWidthInner());
        mWebPaint.setColor(mChart.getWebColorInner());
        mWebPaint.setAlpha(mChart.getWebAlpha());

        int labelCount = mChart.getYAxis().mEntryCount;

        for (int j = 0; j < labelCount; j++) {

            for (int i = 0; i < mChart.getData().getXValCount(); i++) {

                float r = (mChart.getYAxis().mEntries[j] - mChart.getYChartMin()) * factor;

                PointF p1 = Utils.getPosition(center, r, sliceangle * i + rotationangle);
                PointF p2 = Utils.getPosition(center, r, sliceangle * (i + 1) + rotationangle);

                c.drawLine(p1.x, p1.y, p2.x, p2.y, mWebPaint);
            }
        }
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        float sliceangle = mChart.getSliceAngle();
        float factor = mChart.getFactor();

        PointF center = mChart.getCenterOffsets();

        for (int i = 0; i < indices.length; i++) {

            RadarDataSet set = mChart.getData()
                    .getDataSetByIndex(indices[i]
                            .getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            // get the index to highlight
            int xIndex = indices[i].getXIndex();

            EntryNIR e = set.getEntryForXIndex(xIndex);
            if (e == null || e.getXIndex() != xIndex)
                continue;

            int j = set.getEntryPosition(e);
            float y = (e.getVal() - mChart.getYChartMin());

            if (Float.isNaN(y))
                continue;

            PointF p = Utils.getPosition(center, y * factor,
                    sliceangle * j + mChart.getRotationAngle());

            float[] pts = new float[]{
                    p.x, p.y
            };

            // draw the lines
            drawHighlightLines(c, pts, set);
        }
    }

}
