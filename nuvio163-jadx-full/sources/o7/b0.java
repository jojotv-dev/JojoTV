package o7;

/* JADX INFO: loaded from: classes3.dex */
public abstract class b0 extends io.sentry.internal.debugmeta.c {
    public /* synthetic */ b0() {
        super(27);
    }

    @Override // io.sentry.internal.debugmeta.c
    public final boolean N(m1 m1Var) {
        m1Var.f43611a++;
        m1Var.f43612b++;
        return ((io.sentry.internal.debugmeta.c) this.f25625c).N(m1Var);
    }

    public abstract boolean T(int i10);

    @Override // io.sentry.internal.debugmeta.c
    public boolean y(b bVar, int i10, CharSequence charSequence) {
        if (i10 >= bVar.f43534b) {
            return false;
        }
        int iCodePointAt = Character.codePointAt(charSequence, i10);
        if (!T(iCodePointAt)) {
            return false;
        }
        int iCharCount = Character.charCount(iCodePointAt);
        bVar.f43542j += iCharCount;
        return ((io.sentry.internal.debugmeta.c) this.f25625c).y(bVar, i10 + iCharCount, charSequence);
    }
}
