package l3;

import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class w implements Comparable {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final w f31902c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final w f31903e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final w f31904i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final w f31905m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final w f31906n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static final w f31907o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public static final w f31908p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public static final w f31909q;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public static final w f31910r;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    public static final List f31911s;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f31912b;

    static {
        w wVar = new w(100);
        w wVar2 = new w(200);
        w wVar3 = new w(300);
        w wVar4 = new w(400);
        f31902c = wVar4;
        w wVar5 = new w(500);
        f31903e = wVar5;
        w wVar6 = new w(600);
        f31904i = wVar6;
        w wVar7 = new w(700);
        w wVar8 = new w(800);
        w wVar9 = new w(900);
        f31905m = wVar4;
        f31906n = wVar5;
        f31907o = wVar6;
        f31908p = wVar7;
        f31909q = wVar8;
        f31910r = wVar9;
        f31911s = CollectionsKt.listOf((Object[]) new w[]{wVar, wVar2, wVar3, wVar4, wVar5, wVar6, wVar7, wVar8, wVar9});
    }

    public w(int i10) {
        this.f31912b = i10;
        boolean z2 = false;
        if (1 <= i10 && i10 < 1001) {
            z2 = true;
        }
        if (z2) {
            return;
        }
        n3.a.a("Font weight can be in range [1, 1000]. Current value: " + i10);
    }

    @Override // java.lang.Comparable
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public final int compareTo(w wVar) {
        return Intrinsics.compare(this.f31912b, wVar.f31912b);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof w) {
            return this.f31912b == ((w) obj).f31912b;
        }
        return false;
    }

    public final int hashCode() {
        return this.f31912b;
    }

    public final String toString() {
        return o2.a.p(new StringBuilder("FontWeight(weight="), this.f31912b, ')');
    }
}
