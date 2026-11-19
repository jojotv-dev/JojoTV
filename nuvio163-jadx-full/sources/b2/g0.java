package b2;

import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

/* JADX INFO: loaded from: classes.dex */
public final class g0 extends Lambda implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f4226b;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public g0(int i10) {
        super(0);
        this.f4226b = i10;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        return Integer.valueOf(this.f4226b);
    }
}
