package c9;

/* JADX INFO: loaded from: classes4.dex */
public enum v implements k9.c {
    SMB2_NEGOTIATE_SIGNING_ENABLED(1),
    SMB2_NEGOTIATE_SIGNING_REQUIRED(2);


    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f6663b;

    v(long j10) {
        this.f6663b = j10;
    }

    @Override // k9.c
    public final long getValue() {
        return this.f6663b;
    }
}
