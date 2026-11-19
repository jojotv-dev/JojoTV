package kotlin.collections;

import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.ArrayIteratorsKt;

/* JADX INFO: loaded from: classes5.dex */
public final /* synthetic */ class b implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f31696b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f31697c;

    public /* synthetic */ b(Object obj, int i10) {
        this.f31696b = i10;
        this.f31697c = obj;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f31696b) {
            case 0:
                return ArrayIteratorsKt.iterator((int[]) this.f31697c);
            case 1:
                return ArrayIteratorsKt.iterator((double[]) this.f31697c);
            case 2:
                return ArrayIteratorsKt.iterator((char[]) this.f31697c);
            case 3:
                return ArrayIteratorsKt.iterator((short[]) this.f31697c);
            case 4:
                return ArrayIteratorsKt.iterator((float[]) this.f31697c);
            case 5:
                return ArrayIteratorsKt.iterator((boolean[]) this.f31697c);
            case 6:
                return ArrayIteratorsKt.iterator((long[]) this.f31697c);
            default:
                return ArrayIteratorsKt.iterator((byte[]) this.f31697c);
        }
    }
}
