package q7;

/* JADX INFO: loaded from: classes.dex */
public final class o implements m {

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final androidx.media3.datasource.b f45766i = new androidx.media3.datasource.b(2);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Object f45767b = new Object();

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public volatile m f45768c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public Object f45769e;

    public o(m mVar) {
        this.f45768c = mVar;
    }

    @Override // q7.m
    public final Object get() {
        m mVar = this.f45768c;
        androidx.media3.datasource.b bVar = f45766i;
        if (mVar != bVar) {
            synchronized (this.f45767b) {
                try {
                    if (this.f45768c != bVar) {
                        Object obj = this.f45768c.get();
                        this.f45769e = obj;
                        this.f45768c = bVar;
                        return obj;
                    }
                } catch (Throwable th2) {
                    throw th2;
                }
            }
        }
        return this.f45769e;
    }

    public final String toString() {
        Object obj = this.f45768c;
        StringBuilder sb2 = new StringBuilder("Suppliers.memoize(");
        if (obj == f45766i) {
            obj = "<supplier that returned " + this.f45769e + ">";
        }
        sb2.append(obj);
        sb2.append(")");
        return sb2.toString();
    }
}
