package c9;

/* JADX INFO: loaded from: classes4.dex */
public enum w implements k9.c {
    SMB2_SESSION_FLAG_IS_GUEST(1),
    SMB2_SESSION_FLAG_IS_NULL(2),
    SMB2_SESSION_FLAG_ENCRYPT_DATA(4);


    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f6668b;

    w(long j10) {
        this.f6668b = j10;
    }

    @Override // k9.c
    public final long getValue() {
        return this.f6668b;
    }
}
