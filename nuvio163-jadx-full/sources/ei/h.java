package ei;

/* JADX INFO: loaded from: classes5.dex */
public final class h implements j {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f15700b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f15701c;

    public /* synthetic */ h(int i10, int i11) {
        this.f15700b = i11;
        this.f15701c = i10;
    }

    @Override // ei.j
    public String a() {
        switch (this.f15700b) {
            case 0:
                return h1.f.j(this.f15701c, " digits", new StringBuilder("expected at least "));
            default:
                return h1.f.j(this.f15701c, " digits", new StringBuilder("expected at most "));
        }
    }

    public int b() {
        switch (this.f15700b) {
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
        }
        return this.f15701c;
    }
}
