package k8;

import java.util.ArrayList;

/* JADX INFO: loaded from: classes4.dex */
public final class a extends ArrayList {
    public final String a(int i10) {
        Object obj = get(i10);
        if (obj instanceof String) {
            return (String) obj;
        }
        return null;
    }

    @Override // java.util.ArrayList, java.util.AbstractList, java.util.List
    public final Object get(int i10) {
        if (i10 < size()) {
            return super.get(i10);
        }
        return null;
    }
}
