package a9;

/* JADX INFO: loaded from: classes4.dex */
public enum p implements k9.c {
    SMB2_GLOBAL_CAP_DFS(1),
    SMB2_GLOBAL_CAP_LEASING(2),
    SMB2_GLOBAL_CAP_LARGE_MTU(4),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_GLOBAL_CAP_MULTI_CHANNEL(8),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_GLOBAL_CAP_PERSISTENT_HANDLES(16),
    SMB2_GLOBAL_CAP_DIRECTORY_LEASING(32),
    SMB2_GLOBAL_CAP_ENCRYPTION(64);


    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f673b;

    p(long j10) {
        this.f673b = j10;
    }

    @Override // k9.c
    public final long getValue() {
        return this.f673b;
    }
}
