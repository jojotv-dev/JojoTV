package a9;

/* JADX INFO: loaded from: classes4.dex */
public enum s implements k9.c {
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_FLAGS_SERVER_TO_REDIR(1),
    SMB2_FLAGS_ASYNC_COMMAND(2),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_FLAGS_RELATED_OPERATIONS(4),
    SMB2_FLAGS_SIGNED(8),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_FLAGS_PRIORITY_MASK(112),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_FLAGS_DFS_OPERATIONS(268435456),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_FLAGS_REPLAY_OPERATION(536870912);


    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f696b;

    s(long j10) {
        this.f696b = j10;
    }

    @Override // k9.c
    public final long getValue() {
        return this.f696b;
    }
}
