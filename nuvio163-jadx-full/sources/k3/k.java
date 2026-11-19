package k3;

import android.text.TextPaint;
import android.text.style.CharacterStyle;

/* JADX INFO: loaded from: classes.dex */
public final class k extends CharacterStyle {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final boolean f27733a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final boolean f27734b;

    public k(boolean z2, boolean z10) {
        this.f27733a = z2;
        this.f27734b = z10;
    }

    @Override // android.text.style.CharacterStyle
    public final void updateDrawState(TextPaint textPaint) {
        textPaint.setUnderlineText(this.f27733a);
        textPaint.setStrikeThruText(this.f27734b);
    }
}
