package x0;

import kotlin.NoWhenBranchMatchedException;

/* JADX INFO: loaded from: classes.dex */
public abstract class v0 {
    public static final h a(h hVar, h hVar2) {
        int iOrdinal = hVar2.ordinal();
        if (iOrdinal == 0) {
            return h.f62765b;
        }
        if (iOrdinal != 1) {
            if (iOrdinal == 2) {
                return h.f62767e;
            }
            throw new NoWhenBranchMatchedException();
        }
        int iOrdinal2 = hVar.ordinal();
        if (iOrdinal2 == 0) {
            return h.f62765b;
        }
        if (iOrdinal2 == 1) {
            return h.f62766c;
        }
        if (iOrdinal2 == 2) {
            return h.f62767e;
        }
        throw new NoWhenBranchMatchedException();
    }
}
