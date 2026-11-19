package hf;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

/* JADX INFO: loaded from: classes4.dex */
public final class m0 implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f22252b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Function1 f22253c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ int f22254e;

    public /* synthetic */ m0(int i10, int i11, Function1 function1) {
        this.f22252b = i11;
        this.f22253c = function1;
        this.f22254e = i10;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f22252b) {
            case 0:
                this.f22253c.invoke(Integer.valueOf(this.f22254e));
                break;
            case 1:
                this.f22253c.invoke(Integer.valueOf(this.f22254e));
                break;
            case 2:
                this.f22253c.invoke(Integer.valueOf(this.f22254e));
                break;
            case 3:
                this.f22253c.invoke(Integer.valueOf(this.f22254e));
                break;
            default:
                this.f22253c.invoke(Integer.valueOf(this.f22254e));
                break;
        }
        return Unit.INSTANCE;
    }
}
