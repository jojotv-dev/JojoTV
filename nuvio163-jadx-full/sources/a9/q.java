package a9;

/* JADX INFO: loaded from: classes4.dex */
public enum q {
    SMB2_NEGOTIATE(0),
    SMB2_SESSION_SETUP(1),
    SMB2_LOGOFF(2),
    SMB2_TREE_CONNECT(3),
    SMB2_TREE_DISCONNECT(4),
    SMB2_CREATE(5),
    SMB2_CLOSE(6),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_FLUSH(7),
    SMB2_READ(8),
    SMB2_WRITE(9),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_LOCK(10),
    SMB2_IOCTL(11),
    SMB2_CANCEL(12),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_ECHO(13),
    SMB2_QUERY_DIRECTORY(14),
    SMB2_CHANGE_NOTIFY(15),
    SMB2_QUERY_INFO(16),
    SMB2_SET_INFO(17),
    SMB2_OPLOCK_BREAK(18);


    /* JADX INFO: renamed from: z, reason: collision with root package name */
    public static final q[] f690z = new q[19];

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f691b;

    static {
        for (q qVar : values()) {
            f690z[qVar.f691b] = qVar;
        }
    }

    q(int i10) {
        this.f691b = i10;
    }
}
