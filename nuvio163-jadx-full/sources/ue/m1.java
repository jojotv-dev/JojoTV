package ue;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

/* JADX INFO: loaded from: classes4.dex */
public final class m1 implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f59020b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Function1 f59021c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ kf.l0 f59022e;

    public /* synthetic */ m1(Function1 function1, kf.l0 l0Var, int i10) {
        this.f59020b = i10;
        this.f59021c = function1;
        this.f59022e = l0Var;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f59020b) {
            case 0:
                this.f59021c.invoke(this.f59022e);
                break;
            default:
                this.f59021c.invoke(this.f59022e);
                break;
        }
        return Unit.INSTANCE;
    }
}
