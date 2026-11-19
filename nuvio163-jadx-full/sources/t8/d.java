package t8;

/* JADX INFO: loaded from: classes4.dex */
public enum d implements k9.c {
    ReferralServers(1),
    StorageServers(2),
    TargetFailback(4);


    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f57251b;

    d(long j10) {
        this.f57251b = j10;
    }

    @Override // k9.c
    public final long getValue() {
        return this.f57251b;
    }
}
