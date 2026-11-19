package l4;

import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.view.View;

/* JADX INFO: loaded from: classes3.dex */
public final class a extends ClickableSpan {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f31917b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final f f31918c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final int f31919e;

    public a(int i10, f fVar, int i11) {
        this.f31917b = i10;
        this.f31918c = fVar;
        this.f31919e = i11;
    }

    @Override // android.text.style.ClickableSpan
    public final void onClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("ACCESSIBILITY_CLICKABLE_SPAN_ID", this.f31917b);
        this.f31918c.f31932a.performAction(this.f31919e, bundle);
    }
}
