package t;

import io.ktor.http.ContentType;
import java.util.Map;
import java.util.Set;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public class b0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f56955a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final u.b f56956b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final io.sentry.hints.j f56957c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public int f56958d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f56959e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public int f56960f;

    public b0(int i10) {
        this.f56955a = i10;
        if (i10 <= 0) {
            u.a.c("maxSize <= 0");
            throw null;
        }
        this.f56956b = new u.b();
        this.f56957c = new io.sentry.hints.j();
    }

    public final Object a(Object key) {
        Intrinsics.checkNotNullParameter(key, "key");
        synchronized (this.f56957c) {
            u.b bVar = this.f56956b;
            bVar.getClass();
            Intrinsics.checkNotNullParameter(key, "key");
            Object obj = bVar.f57936a.get(key);
            if (obj != null) {
                this.f56959e++;
                return obj;
            }
            this.f56960f++;
            Intrinsics.checkNotNullParameter(key, "key");
            return null;
        }
    }

    public final Object b(Object key, Object value) {
        Object oldValue;
        Object key2;
        Object oldValue2;
        Intrinsics.checkNotNullParameter(key, "key");
        Intrinsics.checkNotNullParameter(value, "value");
        synchronized (this.f56957c) {
            try {
                int i10 = this.f56958d;
                Intrinsics.checkNotNullParameter(key, "key");
                Intrinsics.checkNotNullParameter(value, "value");
                this.f56958d = i10 + 1;
                u.b bVar = this.f56956b;
                bVar.getClass();
                Intrinsics.checkNotNullParameter(key, "key");
                Intrinsics.checkNotNullParameter(value, "value");
                oldValue = bVar.f57936a.put(key, value);
                if (oldValue != null) {
                    int i11 = this.f56958d;
                    Intrinsics.checkNotNullParameter(key, "key");
                    Intrinsics.checkNotNullParameter(oldValue, "value");
                    this.f56958d = i11 - 1;
                }
                Unit unit = Unit.INSTANCE;
            } catch (Throwable th2) {
                throw th2;
            }
        }
        if (oldValue != null) {
            Intrinsics.checkNotNullParameter(key, "key");
            Intrinsics.checkNotNullParameter(oldValue, "oldValue");
        }
        int i12 = this.f56955a;
        while (true) {
            synchronized (this.f56957c) {
                try {
                    if (this.f56958d < 0 || (this.f56956b.f57936a.isEmpty() && this.f56958d != 0)) {
                        break;
                    }
                    if (this.f56958d > i12 && !this.f56956b.f57936a.isEmpty()) {
                        Set setEntrySet = this.f56956b.f57936a.entrySet();
                        Intrinsics.checkNotNullExpressionValue(setEntrySet, "<get-entries>(...)");
                        Map.Entry entry = (Map.Entry) CollectionsKt.firstOrNull(setEntrySet);
                        if (entry == null) {
                            return oldValue;
                        }
                        key2 = entry.getKey();
                        oldValue2 = entry.getValue();
                        u.b bVar2 = this.f56956b;
                        bVar2.getClass();
                        Intrinsics.checkNotNullParameter(key2, "key");
                        bVar2.f57936a.remove(key2);
                        int i13 = this.f56958d;
                        Intrinsics.checkNotNullParameter(key2, "key");
                        Intrinsics.checkNotNullParameter(oldValue2, "value");
                        this.f56958d = i13 - 1;
                    }
                    return oldValue;
                } catch (Throwable th3) {
                    throw th3;
                }
            }
            Intrinsics.checkNotNullParameter(key2, "key");
            Intrinsics.checkNotNullParameter(oldValue2, "oldValue");
        }
        Intrinsics.checkNotNullParameter("LruCache.sizeOf() is reporting inconsistent results!", ContentType.Message.TYPE);
        throw new IllegalStateException("LruCache.sizeOf() is reporting inconsistent results!");
    }

    public final Object c(Object key) {
        Object oldValue;
        Intrinsics.checkNotNullParameter(key, "key");
        synchronized (this.f56957c) {
            try {
                u.b bVar = this.f56956b;
                bVar.getClass();
                Intrinsics.checkNotNullParameter(key, "key");
                oldValue = bVar.f57936a.remove(key);
                if (oldValue != null) {
                    int i10 = this.f56958d;
                    Intrinsics.checkNotNullParameter(key, "key");
                    Intrinsics.checkNotNullParameter(oldValue, "value");
                    this.f56958d = i10 - 1;
                }
                Unit unit = Unit.INSTANCE;
            } catch (Throwable th2) {
                throw th2;
            }
        }
        if (oldValue != null) {
            Intrinsics.checkNotNullParameter(key, "key");
            Intrinsics.checkNotNullParameter(oldValue, "oldValue");
        }
        return oldValue;
    }

    public final String toString() {
        String str;
        synchronized (this.f56957c) {
            try {
                int i10 = this.f56959e;
                int i11 = this.f56960f + i10;
                str = "LruCache[maxSize=" + this.f56955a + ",hits=" + this.f56959e + ",misses=" + this.f56960f + ",hitRate=" + (i11 != 0 ? (i10 * 100) / i11 : 0) + "%]";
            } catch (Throwable th2) {
                throw th2;
            }
        }
        return str;
    }
}
