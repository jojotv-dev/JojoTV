package qe;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class g3 implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f48984b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ String f48985c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ g1.n1 f48986e;

    public /* synthetic */ g3(int i10, g1.n1 n1Var, String str) {
        this.f48984b = i10;
        this.f48985c = str;
        this.f48986e = n1Var;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f48984b) {
            case 0:
                this.f48986e.setValue("header:" + this.f48985c);
                break;
            default:
                this.f48986e.setValue(this.f48985c);
                break;
        }
        return Unit.INSTANCE;
    }
}
