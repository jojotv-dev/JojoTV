package q;

import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public final class a extends f {

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public final HashMap f45264m = new HashMap();

    @Override // q.f
    public final c a(Object obj) {
        return (c) this.f45264m.get(obj);
    }

    @Override // q.f
    public final Object b(Object obj) {
        Object objB = super.b(obj);
        this.f45264m.remove(obj);
        return objB;
    }
}
