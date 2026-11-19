package com.liskovsoft.sharedutils.misc;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

/* JADX INFO: loaded from: classes4.dex */
public class RoundedBackgroundSpan extends ReplacementSpan {
    private final int mBackgroundColor;
    private final int mMarginLeft;
    private final int mMarginRight;
    private final int mPaddingLeft;
    private final int mPaddingRight;

    public RoundedBackgroundSpan(int i10) {
        this(i10, 10, 10, 10, 10);
    }

    @Override // android.text.style.ReplacementSpan
    public void draw(Canvas canvas, CharSequence charSequence, int i10, int i11, float f10, int i12, int i13, int i14, Paint paint) {
        float fMeasureText = paint.measureText(charSequence.subSequence(i10, i11).toString());
        int color = paint.getColor();
        RectF rectF = new RectF(this.mMarginLeft + f10, (i12 - paint.getFontMetricsInt().top) + paint.getFontMetricsInt().ascent, fMeasureText + f10 + this.mMarginLeft + this.mPaddingLeft + this.mPaddingRight, i14);
        paint.setColor(this.mBackgroundColor);
        canvas.drawRect(rectF, paint);
        paint.setColor(color);
        canvas.drawText(charSequence, i10, i11, f10 + this.mMarginLeft + this.mPaddingLeft, i13 - (paint.getFontMetricsInt().descent / 2), paint);
    }

    @Override // android.text.style.ReplacementSpan
    public int getSize(Paint paint, CharSequence charSequence, int i10, int i11, Paint.FontMetricsInt fontMetricsInt) {
        return (int) (paint.measureText(charSequence.subSequence(i10, i11).toString()) + this.mMarginLeft + this.mPaddingLeft + this.mPaddingRight + this.mMarginRight);
    }

    public RoundedBackgroundSpan(int i10, int i11, int i12, int i13, int i14) {
        this.mBackgroundColor = i10;
        this.mPaddingLeft = i11;
        this.mPaddingRight = i12;
        this.mMarginLeft = i13;
        this.mMarginRight = i14;
    }
}
