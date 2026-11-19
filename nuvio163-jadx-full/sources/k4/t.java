package k4;

/* JADX INFO: loaded from: classes3.dex */
public final class t {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public int f27841a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f27842b;

    public /* synthetic */ t(int i10, int i11) {
        this.f27841a = i10;
        this.f27842b = i11;
    }

    public int a() {
        return this.f27841a | this.f27842b;
    }

    public void b(int i10, int i11) {
        if (i11 == 1) {
            this.f27842b = i10;
        } else {
            this.f27841a = i10;
        }
    }

    public void c(int i10) {
        if (i10 == 1) {
            this.f27842b = 0;
        } else {
            this.f27841a = 0;
        }
    }
}
