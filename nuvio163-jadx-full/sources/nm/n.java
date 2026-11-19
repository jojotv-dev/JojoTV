package nm;

/* JADX INFO: loaded from: classes5.dex */
public abstract class n {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f42985a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public long f42986b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f42987c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public int f42988d;

    public n(int i10) {
        this.f42987c = 0;
        this.f42986b = 0L;
        this.f42988d = 0;
        this.f42985a = i10;
    }

    public byte[] a() {
        byte[] bArr = new byte[32];
        rn.d.p(this.f42985a, 0, bArr);
        rn.d.v(this.f42986b, bArr, 4);
        rn.d.p(this.f42987c, 12, bArr);
        rn.d.p(this.f42988d, 28, bArr);
        return bArr;
    }

    public n(n nVar) {
        this.f42985a = nVar.f42987c;
        this.f42986b = nVar.f42986b;
        this.f42987c = nVar.f42985a;
        this.f42988d = nVar.f42988d;
    }
}
