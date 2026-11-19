package hj;

/* JADX INFO: loaded from: classes5.dex */
public final class g {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final g f23471e;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public boolean f23472a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public fo.a f23473b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public fo.a f23474c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public fo.a f23475d;

    static {
        g gVar = new g();
        gVar.f23472a = false;
        fo.a aVar = i.f23479a;
        gVar.f23474c = aVar;
        gVar.f23473b = aVar;
        gVar.f23475d = i.f23480b;
        f23471e = gVar;
    }

    public final void a(String str, StringBuilder sb2) {
        if (!this.f23474c.t(str)) {
            sb2.append((CharSequence) str);
            return;
        }
        sb2.append('\"');
        g gVar = h.f23476a;
        if (str != null) {
            this.f23475d.m(str, sb2);
        }
        sb2.append('\"');
    }
}
