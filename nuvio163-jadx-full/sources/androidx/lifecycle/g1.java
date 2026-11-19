package androidx.lifecycle;

import java.util.Iterator;
import java.util.LinkedHashMap;

/* JADX INFO: loaded from: classes.dex */
public final class g1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final LinkedHashMap f2652a = new LinkedHashMap();

    public final void a() {
        LinkedHashMap linkedHashMap = this.f2652a;
        Iterator it = linkedHashMap.values().iterator();
        while (it.hasNext()) {
            ((z0) it.next()).clear$lifecycle_viewmodel_release();
        }
        linkedHashMap.clear();
    }
}
