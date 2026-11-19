package mf;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

/* JADX INFO: loaded from: classes4.dex */
public final class og implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ Function1 f37527b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ float f37528c;

    public og(Function1 function1, float f10) {
        this.f37527b = function1;
        this.f37528c = f10;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        this.f37527b.invoke(Float.valueOf(this.f37528c));
        return Unit.INSTANCE;
    }
}
