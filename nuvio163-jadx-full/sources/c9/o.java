package c9;

/* JADX INFO: loaded from: classes4.dex */
public enum o implements k9.c {
    SMB2_RESTART_SCANS(1),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_RETURN_SINGLE_ENTRY(2),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_INDEX_SPECIFIED(4),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_REOPEN(16);


    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f6646b;

    o(long j10) {
        this.f6646b = j10;
    }

    @Override // k9.c
    public final long getValue() {
        return this.f6646b;
    }
}
