package ld;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class b6 implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f32287b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ i6 f32288c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ x f32289e;

    public /* synthetic */ b6(i6 i6Var, x xVar, int i10) {
        this.f32287b = i10;
        this.f32288c = i6Var;
        this.f32289e = xVar;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f32287b) {
            case 0:
                this.f32288c.c(this.f32289e, false);
                break;
            case 1:
                this.f32288c.c(this.f32289e, true);
                break;
            default:
                this.f32288c.c(this.f32289e, true);
                break;
        }
        return Unit.INSTANCE;
    }
}
