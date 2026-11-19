package a9;

/* JADX INFO: loaded from: classes4.dex */
public enum a0 implements k9.c {
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_SHARE_CAP_DFS(8),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_SHARE_CAP_CONTINUOUS_AVAILABILITY(16),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_SHARE_CAP_SCALEOUT(32),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_SHARE_CAP_CLUSTER(64),
    SMB2_SHARE_CAP_ASYMMETRIC(128);


    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f610b;

    a0(long j10) {
        this.f610b = j10;
    }

    @Override // k9.c
    public final long getValue() {
        return this.f610b;
    }
}
