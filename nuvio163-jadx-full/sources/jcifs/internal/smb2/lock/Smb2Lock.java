package jcifs.internal.smb2.lock;

import jcifs.Encodable;
import jcifs.internal.util.SMBUtil;

/* JADX INFO: loaded from: classes5.dex */
public class Smb2Lock implements Encodable {
    public static final int SMB2_LOCKFLAG_EXCLUSIVE_LOCK = 2;
    public static final int SMB2_LOCKFLAG_FAIL_IMMEDIATELY = 16;
    public static final int SMB2_LOCKFLAG_SHARED_LOCK = 1;
    public static final int SMB2_LOCKFLAG_UNLOCK = 4;
    private int flags;
    private long length;
    private long offset;

    public Smb2Lock(long j10, long j11, int i10) {
        this.offset = j10;
        this.length = j11;
        this.flags = i10;
    }

    @Override // jcifs.Encodable
    public int encode(byte[] bArr, int i10) {
        SMBUtil.writeInt8(this.offset, bArr, i10);
        SMBUtil.writeInt8(this.length, bArr, i10 + 8);
        SMBUtil.writeInt4(this.flags, bArr, i10 + 16);
        return (i10 + 24) - i10;
    }

    @Override // jcifs.Encodable
    public int size() {
        return 24;
    }
}
