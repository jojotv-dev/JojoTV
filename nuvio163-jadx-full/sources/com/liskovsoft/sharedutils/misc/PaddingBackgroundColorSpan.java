package com.liskovsoft.sharedutils.misc;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.style.LineBackgroundSpan;

/* JADX INFO: loaded from: classes4.dex */
public class PaddingBackgroundColorSpan implements LineBackgroundSpan {
    private int mBackgroundColor;
    private Rect mBgRect;
    private int mPadding;

    public PaddingBackgroundColorSpan(int i10) {
        this(i10, 10);
    }

    @Override // android.text.style.LineBackgroundSpan
    public void drawBackground(Canvas canvas, Paint paint, int i10, int i11, int i12, int i13, int i14, CharSequence charSequence, int i15, int i16, int i17) {
        int iRound = Math.round(paint.measureText(charSequence, i15, i16));
        int color = paint.getColor();
        int i18 = ((i11 - i10) - iRound) / 2;
        Rect rect = this.mBgRect;
        int i19 = this.mPadding;
        rect.set(i18 - (i19 * 2), i12 - (i17 == 0 ? i19 / 2 : -(i19 / 2)), (i19 * 2) + i18 + iRound, (i19 / 2) + i14);
        paint.setColor(this.mBackgroundColor);
        canvas.drawRect(this.mBgRect, paint);
        paint.setColor(color);
    }

    public PaddingBackgroundColorSpan(int i10, int i11) {
        this.mBackgroundColor = i10;
        this.mPadding = i11;
        this.mBgRect = new Rect();
    }
}
