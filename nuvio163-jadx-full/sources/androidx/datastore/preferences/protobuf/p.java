package androidx.datastore.preferences.protobuf;

import java.util.Collections;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public final class p {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static volatile p f2534b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final p f2535c = new p();

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Map f2536a = Collections.EMPTY_MAP;

    public static p a() {
        p pVar;
        p pVar2 = f2534b;
        if (pVar2 != null) {
            return pVar2;
        }
        synchronized (p.class) {
            try {
                pVar = f2534b;
                if (pVar == null) {
                    Class cls = o.f2530a;
                    p pVar3 = null;
                    if (cls != null) {
                        try {
                            pVar3 = (p) cls.getDeclaredMethod("getEmptyRegistry", null).invoke(null, null);
                        } catch (Exception unused) {
                        }
                    }
                    pVar = pVar3 != null ? pVar3 : f2535c;
                    f2534b = pVar;
                }
            } catch (Throwable th2) {
                throw th2;
            }
        }
        return pVar;
    }
}
