package ci;

import java.util.ArrayList;
import java.util.Iterator;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes3.dex */
public final class h implements s {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final ArrayList f7317a;

    public h(ArrayList predicates) {
        Intrinsics.checkNotNullParameter(predicates, "predicates");
        this.f7317a = predicates;
    }

    @Override // ci.s
    public final boolean test(Object obj) {
        ArrayList arrayList = this.f7317a;
        if (arrayList.isEmpty()) {
            return true;
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            if (!((s) it.next()).test(obj)) {
                return false;
            }
        }
        return true;
    }
}
