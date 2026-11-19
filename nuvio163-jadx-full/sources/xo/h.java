package xo;

/* JADX INFO: loaded from: classes5.dex */
public final class h {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public int f64096a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f64097b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f64098c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public long f64099d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public String f64100e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public String f64101f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public String f64102g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public int f64103h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public h f64104i;

    public h(int i10, h hVar) {
        this.f64096a = i10;
        this.f64097b = hVar.f64097b;
        this.f64098c = hVar.f64098c;
        this.f64099d = hVar.f64099d;
        this.f64100e = hVar.f64100e;
        this.f64101f = hVar.f64101f;
        this.f64102g = hVar.f64102g;
        this.f64103h = hVar.f64103h;
    }

    /* JADX WARN: Code duplicated, block: B:20:0x0046  */
    public final void a(int i10, String str, String str2, String str3) {
        int iHashCode;
        int iHashCode2;
        this.f64097b = i10;
        this.f64100e = str;
        this.f64101f = str2;
        this.f64102g = str3;
        if (i10 == 1) {
            iHashCode = str.hashCode();
            iHashCode2 = (iHashCode + i10) & Integer.MAX_VALUE;
        } else if (i10 != 12) {
            if (i10 == 16 || i10 == 30) {
                iHashCode = str.hashCode();
            } else {
                if (i10 == 7) {
                    this.f64098c = 0;
                } else if (i10 != 8) {
                    iHashCode = str3.hashCode() * str2.hashCode() * str.hashCode();
                }
                iHashCode = str.hashCode();
            }
            iHashCode2 = (iHashCode + i10) & Integer.MAX_VALUE;
        } else {
            iHashCode2 = ((str2.hashCode() * str.hashCode()) + i10) & Integer.MAX_VALUE;
        }
        this.f64103h = iHashCode2;
    }
}
