package ce;

import kotlin.jvm.functions.Function0;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class b implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f6775b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ c f6776c;

    public /* synthetic */ b(c cVar, int i10) {
        this.f6775b = i10;
        this.f6776c = cVar;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f6775b) {
            case 0:
                c cVar = this.f6776c;
                i iVar = (i) cVar.f6786j.getAndSet(null);
                if (iVar == null) {
                    return null;
                }
                cVar.f6785i = iVar.f6841a;
                return iVar;
            case 1:
                return Long.valueOf(this.f6776c.k.get());
            default:
                return Integer.valueOf(this.f6776c.f6785i);
        }
    }
}
