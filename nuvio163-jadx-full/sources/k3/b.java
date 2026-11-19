package k3;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

/* JADX INFO: loaded from: classes.dex */
public final class b extends MetricAffectingSpan {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f27700b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Object f27701c;

    public /* synthetic */ b(Object obj, int i10) {
        this.f27700b = i10;
        this.f27701c = obj;
    }

    @Override // android.text.style.CharacterStyle
    public final void updateDrawState(TextPaint textPaint) {
        switch (this.f27700b) {
            case 0:
                textPaint.setFontFeatureSettings((String) this.f27701c);
                break;
            default:
                textPaint.setTypeface((Typeface) this.f27701c);
                break;
        }
    }

    @Override // android.text.style.MetricAffectingSpan
    public final void updateMeasureState(TextPaint textPaint) {
        switch (this.f27700b) {
            case 0:
                textPaint.setFontFeatureSettings((String) this.f27701c);
                break;
            default:
                textPaint.setTypeface((Typeface) this.f27701c);
                break;
        }
    }
}
