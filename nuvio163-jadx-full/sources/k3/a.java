package k3;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

/* JADX INFO: loaded from: classes.dex */
public final class a extends MetricAffectingSpan {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f27698b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final float f27699c;

    public /* synthetic */ a(float f10, int i10) {
        this.f27698b = i10;
        this.f27699c = f10;
    }

    @Override // android.text.style.CharacterStyle
    public final void updateDrawState(TextPaint textPaint) {
        switch (this.f27698b) {
            case 0:
                textPaint.baselineShift += (int) Math.ceil(textPaint.ascent() * this.f27699c);
                break;
            default:
                textPaint.setTextSkewX(textPaint.getTextSkewX() + this.f27699c);
                break;
        }
    }

    @Override // android.text.style.MetricAffectingSpan
    public final void updateMeasureState(TextPaint textPaint) {
        switch (this.f27698b) {
            case 0:
                textPaint.baselineShift += (int) Math.ceil(textPaint.ascent() * this.f27699c);
                break;
            default:
                textPaint.setTextSkewX(textPaint.getTextSkewX() + this.f27699c);
                break;
        }
    }
}
