package tg;

/* JADX INFO: loaded from: classes.dex */
public final class a implements c {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final Object f57889c = new Object();

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public volatile c f57890a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public volatile Object f57891b = f57889c;

    public a(c cVar) {
        this.f57890a = cVar;
    }

    public static a a(c cVar) {
        if (cVar instanceof a) {
            return (a) cVar;
        }
        cVar.getClass();
        return new a(cVar);
    }

    public static c b(c cVar) {
        return cVar instanceof a ? cVar : new a(cVar);
    }

    @Override // tg.c
    public final Object get() {
        Object obj;
        Object obj2 = this.f57891b;
        Object obj3 = f57889c;
        if (obj2 != obj3) {
            return obj2;
        }
        synchronized (this) {
            obj = this.f57891b;
            if (obj == obj3) {
                obj = this.f57890a.get();
                Object obj4 = this.f57891b;
                if (obj4 != obj3 && obj4 != obj) {
                    throw new IllegalStateException("Scoped provider was invoked recursively returning different results: " + obj4 + " & " + obj + ". This is likely due to a circular dependency.");
                }
                this.f57891b = obj;
                this.f57890a = null;
            }
        }
        return obj;
    }
}
