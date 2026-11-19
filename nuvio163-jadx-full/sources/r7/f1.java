package r7;

/* JADX INFO: loaded from: classes4.dex */
public final class f1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Object f51253a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Object f51254b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Object f51255c;

    public f1(Object obj, Object obj2, Object obj3) {
        this.f51253a = obj;
        this.f51254b = obj2;
        this.f51255c = obj3;
    }

    public final IllegalArgumentException a() {
        StringBuilder sb2 = new StringBuilder("Multiple entries with same key: ");
        Object obj = this.f51253a;
        sb2.append(obj);
        sb2.append("=");
        sb2.append(this.f51254b);
        sb2.append(" and ");
        sb2.append(obj);
        sb2.append("=");
        sb2.append(this.f51255c);
        return new IllegalArgumentException(sb2.toString());
    }
}
