package io.ktor.http.content;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/* JADX INFO: loaded from: classes5.dex */
public final /* synthetic */ class a implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f24405b;

    public /* synthetic */ a(int i10) {
        this.f24405b = i10;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f24405b) {
            case 0:
                return BlockingBridgeKt.isParkingAllowedFunction_delegate$lambda$0();
            default:
                return Unit.INSTANCE;
        }
    }
}
