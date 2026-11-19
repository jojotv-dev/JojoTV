package pf;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class j implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f45016b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ boolean f45017c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Function1 f45018e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ boolean f45019i;

    public /* synthetic */ j(boolean z2, Function1 function1, boolean z10, int i10) {
        this.f45016b = i10;
        this.f45017c = z2;
        this.f45018e = function1;
        this.f45019i = z10;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f45016b) {
            case 0:
                if (!this.f45017c) {
                    this.f45018e.invoke(Boolean.valueOf(!this.f45019i));
                }
                break;
            default:
                if (!this.f45017c) {
                    this.f45018e.invoke(Boolean.valueOf(!this.f45019i));
                }
                break;
        }
        return Unit.INSTANCE;
    }
}
