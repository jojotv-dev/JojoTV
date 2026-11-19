package androidx.lifecycle;

import java.io.Closeable;
import java.util.Iterator;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public abstract class z0 {
    private final i5.c impl = new i5.c();

    public final void addCloseable(String key, AutoCloseable closeable) {
        AutoCloseable autoCloseable;
        Intrinsics.checkNotNullParameter(key, "key");
        Intrinsics.checkNotNullParameter(closeable, "closeable");
        i5.c cVar = this.impl;
        if (cVar != null) {
            Intrinsics.checkNotNullParameter(key, "key");
            Intrinsics.checkNotNullParameter(closeable, "closeable");
            if (cVar.f23790d) {
                i5.c.b(closeable);
                return;
            }
            synchronized (cVar.f23787a) {
                autoCloseable = (AutoCloseable) cVar.f23788b.put(key, closeable);
            }
            i5.c.b(autoCloseable);
        }
    }

    public final void clear$lifecycle_viewmodel_release() {
        i5.c cVar = this.impl;
        if (cVar != null && !cVar.f23790d) {
            cVar.f23790d = true;
            synchronized (cVar.f23787a) {
                try {
                    Iterator it = cVar.f23788b.values().iterator();
                    while (it.hasNext()) {
                        i5.c.b((AutoCloseable) it.next());
                    }
                    Iterator it2 = cVar.f23789c.iterator();
                    while (it2.hasNext()) {
                        i5.c.b((AutoCloseable) it2.next());
                    }
                    cVar.f23789c.clear();
                    Unit unit = Unit.INSTANCE;
                } catch (Throwable th2) {
                    throw th2;
                }
            }
        }
        onCleared();
    }

    public final <T extends AutoCloseable> T getCloseable(String key) {
        T t10;
        Intrinsics.checkNotNullParameter(key, "key");
        i5.c cVar = this.impl;
        if (cVar == null) {
            return null;
        }
        Intrinsics.checkNotNullParameter(key, "key");
        synchronized (cVar.f23787a) {
            t10 = (T) cVar.f23788b.get(key);
        }
        return t10;
    }

    public void onCleared() {
    }

    public void addCloseable(AutoCloseable closeable) {
        Intrinsics.checkNotNullParameter(closeable, "closeable");
        i5.c cVar = this.impl;
        if (cVar != null) {
            cVar.a(closeable);
        }
    }

    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Replaced by `AutoCloseable` overload.")
    public /* synthetic */ void addCloseable(Closeable closeable) {
        Intrinsics.checkNotNullParameter(closeable, "closeable");
        i5.c cVar = this.impl;
        if (cVar != null) {
            cVar.a(closeable);
        }
    }
}
