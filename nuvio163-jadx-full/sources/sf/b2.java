package sf;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class b2 implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f54534b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Function0 f54535c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Function0 f54536e;

    public /* synthetic */ b2(Function0 function0, Function0 function1, int i10) {
        this.f54534b = i10;
        this.f54535c = function0;
        this.f54536e = function1;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f54534b) {
            case 0:
                this.f54535c.invoke();
                this.f54536e.invoke();
                break;
            default:
                this.f54535c.invoke();
                this.f54536e.invoke();
                break;
        }
        return Unit.INSTANCE;
    }
}
