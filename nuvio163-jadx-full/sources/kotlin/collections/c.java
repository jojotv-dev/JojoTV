package kotlin.collections;

import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.ArrayIteratorKt;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class c implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f31698b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f31699c;

    public /* synthetic */ c(Object obj, int i10) {
        this.f31698b = i10;
        this.f31699c = obj;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f31698b) {
            case 0:
                return ArrayIteratorKt.iterator((Object[]) this.f31699c);
            default:
                return ((Iterable) this.f31699c).iterator();
        }
    }
}
