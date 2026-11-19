package o7;

import java.util.Stack;

/* JADX INFO: loaded from: classes3.dex */
public final class u0 extends io.sentry.internal.debugmeta.c {

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public int f43666i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public int f43667m;

    @Override // io.sentry.internal.debugmeta.c
    public final boolean y(b bVar, int i10, CharSequence charSequence) {
        int i11 = this.f43666i;
        if (this.f43667m > 0) {
            bVar.getClass();
        }
        ((Stack) bVar.f43540h.get(i11)).push(Integer.valueOf(i10));
        boolean zY = ((io.sentry.internal.debugmeta.c) this.f25625c).y(bVar, i10, charSequence);
        ((Stack) bVar.f43540h.get(i11)).pop();
        return zY;
    }
}
