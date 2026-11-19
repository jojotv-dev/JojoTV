package w4;

/* JADX INFO: loaded from: classes3.dex */
public final class q implements p {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f62003b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f62004c = -1;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f62005e = -1;

    public q(int i10) {
        this.f62003b = i10;
    }

    @Override // w4.p
    public final boolean e(CharSequence charSequence, int i10, int i11, x xVar) {
        int i12 = this.f62003b;
        if (i10 > i12 || i12 >= i11) {
            return i11 <= i12;
        }
        this.f62004c = i10;
        this.f62005e = i11;
        return false;
    }

    @Override // w4.p
    public final Object getResult() {
        return this;
    }
}
