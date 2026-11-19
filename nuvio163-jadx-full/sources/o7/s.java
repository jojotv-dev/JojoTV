package o7;

/* JADX INFO: loaded from: classes3.dex */
public abstract class s extends b0 {
    @Override // o7.b0, io.sentry.internal.debugmeta.c
    public final boolean y(b bVar, int i10, CharSequence charSequence) {
        if (i10 >= bVar.f43534b || !T(charSequence.charAt(i10))) {
            return false;
        }
        bVar.f43542j++;
        return ((io.sentry.internal.debugmeta.c) this.f25625c).y(bVar, i10 + 1, charSequence);
    }
}
