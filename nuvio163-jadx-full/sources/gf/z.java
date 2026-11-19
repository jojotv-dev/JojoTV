package gf;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

/* JADX INFO: loaded from: classes4.dex */
public final class z implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f20152b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Function1 f20153c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ String f20154e;

    public /* synthetic */ z(String str, int i10, Function1 function1) {
        this.f20152b = i10;
        this.f20153c = function1;
        this.f20154e = str;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f20152b) {
            case 0:
                this.f20153c.invoke(this.f20154e);
                break;
            case 1:
                this.f20153c.invoke(this.f20154e);
                break;
            case 2:
                this.f20153c.invoke(this.f20154e);
                break;
            case 3:
                this.f20153c.invoke(this.f20154e);
                break;
            default:
                this.f20153c.invoke(this.f20154e);
                break;
        }
        return Unit.INSTANCE;
    }
}
