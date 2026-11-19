package g1;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class l2 implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f18495b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ r2 f18496c;

    public /* synthetic */ l2(r2 r2Var, int i10) {
        this.f18495b = i10;
        this.f18496c = r2Var;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f18495b) {
            case 0:
                this.f18496c.E();
                break;
            default:
                this.f18496c.E();
                break;
        }
        return Unit.INSTANCE;
    }
}
