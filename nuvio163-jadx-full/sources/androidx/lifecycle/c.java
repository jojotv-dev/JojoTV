package androidx.lifecycle;

import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public final class c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f2624a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Method f2625b;

    public c(int i10, Method method) {
        this.f2624a = i10;
        this.f2625b = method;
        method.setAccessible(true);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof c)) {
            return false;
        }
        c cVar = (c) obj;
        return this.f2624a == cVar.f2624a && this.f2625b.getName().equals(cVar.f2625b.getName());
    }

    public final int hashCode() {
        return this.f2625b.getName().hashCode() + (this.f2624a * 31);
    }
}
