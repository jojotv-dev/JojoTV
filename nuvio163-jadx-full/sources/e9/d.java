package e9;

/* JADX INFO: loaded from: classes4.dex */
public enum d implements k9.c {
    SMB2_PREAUTH_INTEGRITY_CAPABILITIES(1),
    SMB2_ENCRYPTION_CAPABILITIES(2),
    SMB2_COMPRESSION_CAPABILITIES(4),
    SMB2_NETNAME_NEGOTIATE_CONTEXT_ID(5);


    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f14022b;

    d(long j10) {
        this.f14022b = j10;
    }

    @Override // k9.c
    public final long getValue() {
        return this.f14022b;
    }
}
