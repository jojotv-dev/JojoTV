package r7;

import java.util.ListIterator;

/* JADX INFO: loaded from: classes.dex */
public final class v1 extends w2 {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ w1 f51365c;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public v1(w1 w1Var, ListIterator listIterator) {
        super(listIterator);
        this.f51365c = w1Var;
    }

    @Override // r7.v2
    public final Object a(Object obj) {
        return this.f51365c.f51372c.apply(obj);
    }
}
