package k3;

import android.text.TextPaint;
import android.text.style.CharacterStyle;

/* JADX INFO: loaded from: classes.dex */
public final class j extends CharacterStyle {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f27729a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final float f27730b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final float f27731c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final float f27732d;

    public j(int i10, float f10, float f11, float f12) {
        this.f27729a = i10;
        this.f27730b = f10;
        this.f27731c = f11;
        this.f27732d = f12;
    }

    @Override // android.text.style.CharacterStyle
    public final void updateDrawState(TextPaint textPaint) {
        textPaint.setShadowLayer(this.f27732d, this.f27730b, this.f27731c, this.f27729a);
    }
}
