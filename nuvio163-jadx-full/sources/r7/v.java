package r7;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public abstract class v implements a2 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public transient Collection f51361b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public transient Set f51362c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public transient Collection f51363e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public transient Map f51364i;

    public final boolean b(Object obj, Object obj2) {
        Collection collection = (Collection) a().get(obj);
        return collection != null && collection.contains(obj2);
    }

    public boolean c(Object obj) {
        Iterator it = a().values().iterator();
        while (it.hasNext()) {
            if (((Collection) it.next()).contains(obj)) {
                return true;
            }
        }
        return false;
    }

    public final void d(String str, ArrayList arrayList) {
        if (arrayList.isEmpty()) {
            return;
        }
        get(str).addAll(arrayList);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof a2) {
            return a().equals(((a2) obj).a());
        }
        return false;
    }

    public final int hashCode() {
        return a().hashCode();
    }

    @Override // r7.a2
    public boolean remove(Object obj, Object obj2) {
        Collection collection = (Collection) a().get(obj);
        return collection != null && collection.remove(obj2);
    }

    public final String toString() {
        return a().toString();
    }
}
