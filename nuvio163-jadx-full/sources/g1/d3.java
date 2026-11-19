package g1;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class d3 extends u1.g0 {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public long f18394c;

    public d3(long j10, long j11) {
        super(j10);
        this.f18394c = j11;
    }

    @Override // u1.g0
    public final void a(u1.g0 g0Var) {
        Intrinsics.checkNotNull(g0Var, "null cannot be cast to non-null type androidx.compose.runtime.SnapshotMutableLongStateImpl.LongStateStateRecord");
        this.f18394c = ((d3) g0Var).f18394c;
    }

    @Override // u1.g0
    public final u1.g0 b(long j10) {
        return new d3(j10, this.f18394c);
    }
}
