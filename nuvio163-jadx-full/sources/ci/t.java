package ci;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.KMutableProperty1;

/* JADX INFO: loaded from: classes3.dex */
public final class t implements ei.a {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final KMutableProperty1 f7333b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final String f7334c;

    public t(KMutableProperty1 property, String name) {
        Intrinsics.checkNotNullParameter(property, "property");
        Intrinsics.checkNotNullParameter(name, "name");
        this.f7333b = property;
        this.f7334c = name;
    }

    public final Object a(Object obj) {
        V v10 = this.f7333b.get(obj);
        if (v10 != 0) {
            return v10;
        }
        throw new IllegalStateException(o2.a.q(new StringBuilder("Field "), this.f7334c, " is not set"));
    }

    @Override // ei.a
    public final Object e(Object obj, Object obj2) {
        KMutableProperty1 kMutableProperty1 = this.f7333b;
        V v10 = kMutableProperty1.get(obj);
        if (v10 == 0) {
            kMutableProperty1.set(obj, obj2);
            return null;
        }
        if (Intrinsics.areEqual(v10, obj2)) {
            return null;
        }
        return v10;
    }

    public /* synthetic */ t(KMutableProperty1 kMutableProperty1) {
        this(kMutableProperty1, kMutableProperty1.getName());
    }
}
