package a9;

/* JADX INFO: loaded from: classes4.dex */
public enum u implements k9.c {
    SMB2_OPLOCK_LEVEL_NONE(0),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_OPLOCK_LEVEL_II(1),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_OPLOCK_LEVEL_EXCLUSIVE(8),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_OPLOCK_LEVEL_BATCH(9),
    SMB2_OPLOCK_LEVEL_LEASE(255);


    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f701b;

    u(long j10) {
        this.f701b = j10;
    }

    @Override // k9.c
    public final long getValue() {
        return this.f701b;
    }
}
