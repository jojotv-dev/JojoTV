package kotlin.collections.unsigned;

import kotlin.UByteArray;
import kotlin.UIntArray;
import kotlin.ULongArray;
import kotlin.UShortArray;
import kotlin.jvm.functions.Function0;

/* JADX INFO: loaded from: classes5.dex */
public final /* synthetic */ class a implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f31701b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f31702c;

    public /* synthetic */ a(Object obj, int i10) {
        this.f31701b = i10;
        this.f31702c = obj;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f31701b) {
            case 0:
                return UIntArray.m452iteratorimpl((int[]) this.f31702c);
            case 1:
                return ULongArray.m531iteratorimpl((long[]) this.f31702c);
            case 2:
                return UByteArray.m373iteratorimpl((byte[]) this.f31702c);
            default:
                return UShortArray.m636iteratorimpl((short[]) this.f31702c);
        }
    }
}
