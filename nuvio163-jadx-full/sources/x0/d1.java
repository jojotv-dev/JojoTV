package x0;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class d1 implements Function1 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f62734b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Function0 f62735c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Function0 f62736e;

    public /* synthetic */ d1(Function0 function0, Function0 function1, int i10) {
        this.f62734b = i10;
        this.f62735c = function0;
        this.f62736e = function1;
    }

    @Override // kotlin.jvm.functions.Function1
    public final Object invoke(Object obj) {
        p0.g gVar = (p0.g) obj;
        switch (this.f62734b) {
            case 0:
                this.f62735c.invoke();
                Function0 function0 = this.f62736e;
                if (function0 != null ? ((Boolean) function0.invoke()).booleanValue() : true) {
                    gVar.close();
                }
                break;
            default:
                this.f62735c.invoke();
                Function0 function1 = this.f62736e;
                if (function1 != null ? ((Boolean) function1.invoke()).booleanValue() : true) {
                    gVar.close();
                }
                break;
        }
        return Unit.INSTANCE;
    }
}
