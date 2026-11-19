package r7;

import java.util.Iterator;
import java.util.Map;

/* JADX INFO: loaded from: classes4.dex */
public final class z1 extends v2 {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ int f51385c;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ z1(Iterator it, int i10) {
        super(it);
        this.f51385c = i10;
    }

    @Override // r7.v2
    public final Object a(Object obj) {
        switch (this.f51385c) {
            case 0:
                return ((Map.Entry) obj).getKey();
            default:
                return ((Map.Entry) obj).getValue();
        }
    }
}
