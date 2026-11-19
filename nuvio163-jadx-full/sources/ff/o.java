package ff;

import g1.n1;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/* JADX INFO: loaded from: classes4.dex */
public final class o implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ boolean f18012b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ int f18013c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Function0 f18014e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ n1 f18015i;

    public o(int i10, n1 n1Var, Function0 function0, boolean z2) {
        this.f18012b = z2;
        this.f18013c = i10;
        this.f18014e = function0;
        this.f18015i = n1Var;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        if (this.f18012b && this.f18013c > 0) {
            this.f18014e.invoke();
            this.f18015i.setValue(Boolean.FALSE);
        }
        return Unit.INSTANCE;
    }
}
