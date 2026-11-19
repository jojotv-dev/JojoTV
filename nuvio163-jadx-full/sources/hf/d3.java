package hf;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class d3 implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f21450b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Function0 f21451c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Function0 f21452e;

    public /* synthetic */ d3(Function0 function0, Function0 function1, int i10) {
        this.f21450b = i10;
        this.f21451c = function0;
        this.f21452e = function1;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f21450b) {
            case 0:
                this.f21451c.invoke();
                this.f21452e.invoke();
                break;
            default:
                this.f21451c.invoke();
                this.f21452e.invoke();
                break;
        }
        return Unit.INSTANCE;
    }
}
