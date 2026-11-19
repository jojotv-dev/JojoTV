package xp;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public final class v {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Class f64283a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Object f64284b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Method f64285c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final List f64286d;

    public v(Class cls, Object obj, Method method, ArrayList arrayList) {
        this.f64283a = cls;
        this.f64284b = obj;
        this.f64285c = method;
        this.f64286d = Collections.unmodifiableList(arrayList);
    }

    public final String toString() {
        return String.format("%s.%s() %s", this.f64283a.getName(), this.f64285c.getName(), this.f64286d);
    }
}
