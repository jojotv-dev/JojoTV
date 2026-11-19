package r7;

import java.util.ListIterator;

/* JADX INFO: loaded from: classes4.dex */
public final class x1 extends w2 {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ y1 f51374c;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public x1(y1 y1Var, ListIterator listIterator) {
        super(listIterator);
        this.f51374c = y1Var;
    }

    @Override // r7.v2
    public final Object a(Object obj) {
        return this.f51374c.f51382c.apply(obj);
    }
}
