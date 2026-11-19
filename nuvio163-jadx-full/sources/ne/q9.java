package ne;

import kotlin.jvm.functions.Function1;

/* JADX INFO: loaded from: classes4.dex */
public final class q9 implements g1.c0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f41373a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ Function1 f41374b;

    public /* synthetic */ q9(int i10, Function1 function1) {
        this.f41373a = i10;
        this.f41374b = function1;
    }

    @Override // g1.c0
    public final void dispose() {
        switch (this.f41373a) {
            case 0:
                this.f41374b.invoke(null);
                break;
            default:
                this.f41374b.invoke(null);
                break;
        }
    }
}
