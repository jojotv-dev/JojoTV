package xp;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes3.dex */
public class c implements o {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final c f64172b = new c();

    public List a(Executor executor) {
        return Collections.singletonList(new q(executor));
    }

    public List b() {
        return Collections.EMPTY_LIST;
    }

    public String c(int i10, Method method) {
        return "parameter #" + (i10 + 1);
    }

    @Override // xp.o
    public Object convert(Object obj) {
        return obj.toString();
    }

    public Object d(Method method, Class cls, Object obj, Object[] objArr) {
        throw new AssertionError();
    }

    public boolean e(Method method) {
        return false;
    }
}
