package com.github.mikephil.chartingNIR.dataNIR;

/**
 * EntryNIR class for the BarChart. (especially stacked bars)
 * 
 * @author Philipp Jahoda
 */
public class BarEntryNIR extends EntryNIR {

	/** the values the stacked barchart holds */
	private float[] mVals;

	/** the sum of all negative values this entry (if stacked) contains */
	private float mNegativeSum;

	/** the sum of all positive values this entry (if stacked) contains */
	private float mPositiveSum;

	/**
	 * Constructor for stacked bar entries.
	 * 
	 * @param vals
	 *            - the stack values, use at lest 2
	 * @param xIndex
	 */
	public BarEntryNIR(float[] vals, int xIndex) {
		super(calcSum(vals), xIndex);

		this.mVals = vals;
		calcPosNegSum();
	}

	/**
	 * Constructor for normal bars (not stacked).
	 * 
	 * @param val
	 * @param xIndex
	 */
	public BarEntryNIR(float val, int xIndex) {
		super(val, xIndex);
	}

	/**
	 * Constructor for stacked bar entries.
	 * 
	 * @param vals
	 *            - the stack values, use at least 2
	 * @param xIndex
	 * @param label
	 *            Additional description label.
	 */
	public BarEntryNIR(float[] vals, int xIndex, String label) {
		super(calcSum(vals), xIndex, label);

		this.mVals = vals;
		calcPosNegSum();
	}

	/**
	 * Constructor for normal bars (not stacked).
	 * 
	 * @param val
	 * @param xIndex
	 * @param data
	 *            Spot for additional data this EntryNIR represents.
	 */
	public BarEntryNIR(float val, int xIndex, Object data) {
		super(val, xIndex, data);
	}

	/**
	 * Returns an exact copy of the BarEntryNIR.
	 */
	public BarEntryNIR copy() {

		BarEntryNIR copied = new BarEntryNIR(getVal(), getXIndex(), getData());
		copied.setVals(mVals);
		return copied;
	}

	/**
	 * Returns the stacked values this BarEntryNIR represents, or null, if only a single value is represented (then, use
	 * getVal()).
	 * 
	 * @return
	 */
	public float[] getVals() {
		return mVals;
	}

	/**
	 * Set the array of values this BarEntryNIR should represent.
	 * 
	 * @param vals
	 */
	public void setVals(float[] vals) {
		setVal(calcSum(vals));
		mVals = vals;
		calcPosNegSum();
	}

	/**
	 * Returns the value of this BarEntryNIR. If the entry is stacked, it returns the positive sum of all values.
	 * 
	 * @return
	 */
	@Override
	public float getVal() {
		return super.getVal();
	}

	/**
	 * Returns true if this BarEntryNIR is stacked (has a values array), false if not.
	 * 
	 * @return
	 */
	public boolean isStacked() {
		return mVals != null;
	}

	public float getBelowSum(int stackIndex) {

		if (mVals == null)
			return 0;

		float remainder = 0f;
		int index = mVals.length - 1;

		while (index > stackIndex && index >= 0) {
			remainder += mVals[index];
			index--;
		}

		return remainder;
	}

	/**
	 * Reuturns the sum of all positive values this entry (if stacked) contains.
	 * 
	 * @return
	 */
	public float getPositiveSum() {
		return mPositiveSum;
	}

	/**
	 * Returns the sum of all negative values this entry (if stacked) contains. (this is a positive number)
	 * 
	 * @return
	 */
	public float getNegativeSum() {
		return mNegativeSum;
	}

	private void calcPosNegSum() {

		if (mVals == null) {
			mNegativeSum = 0;
			mPositiveSum = 0;
			return;
		}

		float sumNeg = 0f;
		float sumPos = 0f;

		for (float f : mVals) {
			if (f <= 0f)
				sumNeg += Math.abs(f);
			else
				sumPos += f;
		}

		mNegativeSum = sumNeg;
		mPositiveSum = sumPos;
	}

	/**
	 * Calculates the sum across all values of the given stack.
	 *
	 * @param vals
	 * @return
	 */
	private static float calcSum(float[] vals) {

		if (vals == null)
			return 0f;

		float sum = 0f;

		for (float f : vals)
			sum += f;

		return sum;
	}
}
