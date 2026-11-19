package ne;

import java.util.HashSet;

/* JADX INFO: loaded from: classes4.dex */
public final class i9 implements g1.c0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ HashSet f40705a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f40706b;

    public i9(HashSet hashSet, int i10) {
        this.f40705a = hashSet;
        this.f40706b = i10;
    }

    @Override // g1.c0
    public final void dispose() {
        this.f40705a.remove(Integer.valueOf(this.f40706b));
    }
}
