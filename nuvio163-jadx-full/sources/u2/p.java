package u2;

import kotlin.jvm.functions.Function1;

/* JADX INFO: loaded from: classes.dex */
public final class p extends l1 {

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public final /* synthetic */ int f58176n;

    public p(int i10, int i11, int i12) {
        this.f58176n = i12;
        switch (i12) {
            case 1:
                e0((((long) i11) & 4294967295L) | (((long) i10) << 32));
                break;
            case 2:
                e0((((long) i11) & 4294967295L) | (((long) i10) << 32));
                break;
            default:
                e0((((long) i11) & 4294967295L) | (((long) i10) << 32));
                break;
        }
    }

    @Override // u2.l1
    public final void d0(long j10, float f10, Function1 function1) {
        int i10 = this.f58176n;
    }

    private final void n0(long j10, float f10, Function1 function1) {
    }

    private final void q0(long j10, float f10, Function1 function1) {
    }

    private final void t0(long j10, float f10, Function1 function1) {
    }
}
