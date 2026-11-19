package qj;

/* JADX INFO: loaded from: classes5.dex */
public final class h {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final g[] f50828d = new g[0];

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public g[] f50829a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f50830b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public boolean f50831c;

    public h() {
        this(10);
    }

    public static g[] b(g[] gVarArr) {
        return gVarArr.length < 1 ? f50828d : (g[]) gVarArr.clone();
    }

    public final void a(g gVar) {
        if (gVar == null) {
            throw new NullPointerException("'element' cannot be null");
        }
        g[] gVarArr = this.f50829a;
        int length = gVarArr.length;
        int i10 = this.f50830b + 1;
        if (this.f50831c | (i10 > length)) {
            g[] gVarArr2 = new g[Math.max(gVarArr.length, (i10 >> 1) + i10)];
            System.arraycopy(this.f50829a, 0, gVarArr2, 0, this.f50830b);
            this.f50829a = gVarArr2;
            this.f50831c = false;
        }
        this.f50829a[this.f50830b] = gVar;
        this.f50830b = i10;
    }

    public final g c(int i10) {
        if (i10 < this.f50830b) {
            return this.f50829a[i10];
        }
        throw new ArrayIndexOutOfBoundsException(i10 + " >= " + this.f50830b);
    }

    public final g[] d() {
        int i10 = this.f50830b;
        if (i10 == 0) {
            return f50828d;
        }
        g[] gVarArr = this.f50829a;
        if (gVarArr.length == i10) {
            this.f50831c = true;
            return gVarArr;
        }
        g[] gVarArr2 = new g[i10];
        System.arraycopy(gVarArr, 0, gVarArr2, 0, i10);
        return gVarArr2;
    }

    public h(int i10) {
        if (i10 < 0) {
            throw new IllegalArgumentException("'initialCapacity' must not be negative");
        }
        this.f50829a = i10 == 0 ? f50828d : new g[i10];
        this.f50830b = 0;
        this.f50831c = false;
    }
}
