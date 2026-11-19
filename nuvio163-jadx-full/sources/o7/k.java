package o7;

/* JADX INFO: loaded from: classes3.dex */
public final class k extends b0 {

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ int f43604i;

    @Override // o7.b0
    public final boolean T(int i10) {
        switch (this.f43604i) {
            case 0:
                return true;
            case 1:
                return (i10 == 10 || i10 == 13 || (i10 | 1) == 8233 || i10 == 133) ? false : true;
            default:
                return i10 != 10;
        }
    }
}
