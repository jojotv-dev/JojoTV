package i0;

import kotlin.jvm.functions.Function0;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class y0 implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f23707b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ z0 f23708c;

    public /* synthetic */ y0(z0 z0Var, int i10) {
        this.f23707b = i10;
        this.f23708c = z0Var;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f23707b) {
            case 0:
                return Float.valueOf(this.f23708c.f23715x.e());
            case 1:
                return Float.valueOf(this.f23708c.f23715x.b());
            default:
                z0 z0Var = this.f23708c;
                return Float.valueOf(z0Var.f23715x.d() - z0Var.f23715x.a());
        }
    }
}
