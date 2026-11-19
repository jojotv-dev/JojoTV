package bd;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class j5 implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f5002b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ e6 f5003c;

    public /* synthetic */ j5(e6 e6Var, int i10) {
        this.f5002b = i10;
        this.f5003c = e6Var;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        int i10;
        switch (this.f5002b) {
            case 0:
                e6 e6Var = this.f5003c;
                synchronized (e6Var.f4776x) {
                    e6Var.f4777y++;
                }
                return Unit.INSTANCE;
            default:
                e6 e6Var2 = this.f5003c;
                synchronized (e6Var2.f4776x) {
                    i10 = e6Var2.f4777y;
                }
                return Integer.valueOf(i10);
        }
    }
}
