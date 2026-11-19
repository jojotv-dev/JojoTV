package gf;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class t2 implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f19954b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Function1 f19955c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ int f19956e;

    public /* synthetic */ t2(int i10, int i11, Function1 function1) {
        this.f19954b = i11;
        this.f19955c = function1;
        this.f19956e = i10;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f19954b) {
            case 0:
                this.f19955c.invoke(Integer.valueOf(this.f19956e));
                break;
            case 1:
                this.f19955c.invoke(Integer.valueOf(this.f19956e));
                break;
            case 2:
                this.f19955c.invoke(Integer.valueOf(this.f19956e));
                break;
            case 3:
                this.f19955c.invoke(Integer.valueOf(this.f19956e));
                break;
            case 4:
                this.f19955c.invoke(Integer.valueOf(this.f19956e));
                break;
            default:
                this.f19955c.invoke(Integer.valueOf(this.f19956e));
                break;
        }
        return Unit.INSTANCE;
    }
}
