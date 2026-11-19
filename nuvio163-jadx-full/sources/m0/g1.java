package m0;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class g1 implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f34388b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ k1 f34389c;

    public /* synthetic */ g1(k1 k1Var, int i10) {
        this.f34388b = i10;
        this.f34389c = k1Var;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f34388b) {
            case 0:
                this.f34389c.c();
                break;
            default:
                this.f34389c.onCancel();
                break;
        }
        return Unit.INSTANCE;
    }
}
