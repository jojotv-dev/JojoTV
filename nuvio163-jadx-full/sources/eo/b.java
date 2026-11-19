package eo;

import java.util.Iterator;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes5.dex */
public class b extends a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final List f15811e;

    /* JADX WARN: Illegal instructions before constructor call */
    public b(co.a type, List children) {
        Intrinsics.checkNotNullParameter(type, "type");
        Intrinsics.checkNotNullParameter(children, "children");
        a aVar = (a) CollectionsKt.firstOrNull(children);
        int i10 = aVar != null ? aVar.f15808b : 0;
        a aVar2 = (a) CollectionsKt.lastOrNull(children);
        super(type, i10, aVar2 != null ? aVar2.f15809c : 0);
        this.f15811e = children;
        Iterator it = children.iterator();
        while (it.hasNext()) {
            a aVar3 = (a) it.next();
            if (aVar3 != null) {
                aVar3.f15810d = this;
            }
        }
    }

    @Override // eo.a
    public final List a() {
        return this.f15811e;
    }
}
