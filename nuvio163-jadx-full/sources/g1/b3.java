package g1;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class b3 extends u1.g0 {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public float f18377c;

    public b3(long j10, float f10) {
        super(j10);
        this.f18377c = f10;
    }

    @Override // u1.g0
    public final void a(u1.g0 g0Var) {
        Intrinsics.checkNotNull(g0Var, "null cannot be cast to non-null type androidx.compose.runtime.SnapshotMutableFloatStateImpl.FloatStateStateRecord");
        this.f18377c = ((b3) g0Var).f18377c;
    }

    @Override // u1.g0
    public final u1.g0 b(long j10) {
        return new b3(j10, this.f18377c);
    }
}
