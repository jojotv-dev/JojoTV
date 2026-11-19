package lm;

/* JADX INFO: loaded from: classes5.dex */
public final class a {

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public static final a f34142f = new a("uov-is", 16, 160, 64, 0);

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static final a f34143g = new a("uov-is-pkc", 16, 160, 64, 1);

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public static final a f34144h = new a("uov-is-pkc-skc", 16, 160, 64, 2);

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final a f34145i = new a("uov-ip", 256, 112, 44, 0);

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public static final a f34146j = new a("uov-ip-pkc", 256, 112, 44, 1);
    public static final a k = new a("uov-ip-pkc-skc", 256, 112, 44, 2);

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    public static final a f34147l = new a("uov-iii", 256, 184, 72, 0);

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final a f34148m = new a("uov-iii-pkc", 256, 184, 72, 1);

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final a f34149n = new a("uov-iii-pkc-skc", 256, 184, 72, 2);

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static final a f34150o = new a("uov-v", 256, 244, 96, 0);

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public static final a f34151p = new a("uov-v-pkc", 256, 244, 96, 1);

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public static final a f34152q = new a("uov-v-pkc-skc", 256, 244, 96, 2);

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f34153a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f34154b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f34155c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final int f34156d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final int f34157e;

    public a(String str, int i10, int i11, int i12, int i13) {
        this.f34153a = str;
        this.f34154b = i10;
        this.f34155c = i12;
        this.f34156d = i11 - i12;
        this.f34157e = i13;
    }

    public final int a(int i10) {
        return this.f34154b == 16 ? (i10 + 1) >>> 1 : i10;
    }
}
