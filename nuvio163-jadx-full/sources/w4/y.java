package w4;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.MetricAffectingSpan;
import android.text.style.ReplacementSpan;

/* JADX INFO: loaded from: classes3.dex */
public final class y extends ReplacementSpan {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final x f62032c;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public TextPaint f62035m;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Paint.FontMetricsInt f62031b = new Paint.FontMetricsInt();

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public short f62033e = -1;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public float f62034i = 1.0f;

    public y(x xVar) {
        a.a.m(xVar, "rasterizer cannot be null");
        this.f62032c = xVar;
    }

    /* JADX WARN: Code duplicated, block: B:22:0x0046  */
    /* JADX WARN: Code duplicated, block: B:24:0x004a  */
    @Override // android.text.style.ReplacementSpan
    public final void draw(Canvas canvas, CharSequence charSequence, int i10, int i11, float f10, int i12, int i13, int i14, Paint paint) {
        TextPaint textPaint = null;
        if (charSequence instanceof Spanned) {
            CharacterStyle[] characterStyleArr = (CharacterStyle[]) ((Spanned) charSequence).getSpans(i10, i11, CharacterStyle.class);
            if (characterStyleArr.length != 0) {
                if (characterStyleArr.length != 1 || characterStyleArr[0] != this) {
                    TextPaint textPaint2 = this.f62035m;
                    if (textPaint2 == null) {
                        textPaint2 = new TextPaint();
                        this.f62035m = textPaint2;
                    }
                    textPaint = textPaint2;
                    textPaint.set(paint);
                    for (CharacterStyle characterStyle : characterStyleArr) {
                        if (!(characterStyle instanceof MetricAffectingSpan)) {
                            characterStyle.updateDrawState(textPaint);
                        }
                    }
                } else if (paint instanceof TextPaint) {
                    textPaint = (TextPaint) paint;
                }
            } else if (paint instanceof TextPaint) {
                textPaint = (TextPaint) paint;
            }
        } else if (paint instanceof TextPaint) {
            textPaint = (TextPaint) paint;
        }
        TextPaint textPaint3 = textPaint;
        if (textPaint3 != null && textPaint3.bgColor != 0) {
            int color = textPaint3.getColor();
            Paint.Style style = textPaint3.getStyle();
            textPaint3.setColor(textPaint3.bgColor);
            textPaint3.setStyle(Paint.Style.FILL);
            canvas.drawRect(f10, i12, f10 + this.f62033e, i14, textPaint3);
            textPaint3.setStyle(style);
            textPaint3.setColor(color);
        }
        k.a().getClass();
        float f11 = i13;
        Paint paint2 = textPaint3;
        if (textPaint3 == null) {
            paint2 = paint;
        }
        x xVar = this.f62032c;
        g5.e eVar = xVar.f62029b;
        Typeface typeface = (Typeface) eVar.f18809i;
        Typeface typeface2 = paint2.getTypeface();
        paint2.setTypeface(typeface);
        canvas.drawText((char[]) eVar.f18807c, xVar.f62028a * 2, 2, f10, f11, paint2);
        paint2.setTypeface(typeface2);
    }

    @Override // android.text.style.ReplacementSpan
    public final int getSize(Paint paint, CharSequence charSequence, int i10, int i11, Paint.FontMetricsInt fontMetricsInt) {
        Paint.FontMetricsInt fontMetricsInt2 = this.f62031b;
        paint.getFontMetricsInt(fontMetricsInt2);
        float fAbs = Math.abs(fontMetricsInt2.descent - fontMetricsInt2.ascent) * 1.0f;
        x xVar = this.f62032c;
        x4.a aVarB = xVar.b();
        int iA = aVarB.a(14);
        this.f62034i = fAbs / (iA != 0 ? aVarB.f63425b.getShort(iA + aVarB.f63424a) : (short) 0);
        x4.a aVarB2 = xVar.b();
        int iA2 = aVarB2.a(14);
        if (iA2 != 0) {
            aVarB2.f63425b.getShort(iA2 + aVarB2.f63424a);
        }
        x4.a aVarB3 = xVar.b();
        int iA3 = aVarB3.a(12);
        short s2 = (short) ((iA3 != 0 ? aVarB3.f63425b.getShort(iA3 + aVarB3.f63424a) : (short) 0) * this.f62034i);
        this.f62033e = s2;
        if (fontMetricsInt != null) {
            fontMetricsInt.ascent = fontMetricsInt2.ascent;
            fontMetricsInt.descent = fontMetricsInt2.descent;
            fontMetricsInt.top = fontMetricsInt2.top;
            fontMetricsInt.bottom = fontMetricsInt2.bottom;
        }
        return s2;
    }
}
