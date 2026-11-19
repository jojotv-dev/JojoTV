package jcifs.internal.smb2.ioctl;

import jcifs.Encodable;
import jcifs.internal.util.SMBUtil;

/* JADX INFO: loaded from: classes5.dex */
public class SrvCopychunk implements Encodable {
    private int length;
    private long sourceOffset;
    private long targetOffset;

    public SrvCopychunk(long j10, long j11, int i10) {
        this.sourceOffset = j10;
        this.targetOffset = j11;
        this.length = i10;
    }

    @Override // jcifs.Encodable
    public int encode(byte[] bArr, int i10) {
        SMBUtil.writeInt8(this.sourceOffset, bArr, i10);
        SMBUtil.writeInt8(this.targetOffset, bArr, i10 + 8);
        SMBUtil.writeInt4(this.length, bArr, i10 + 16);
        return (i10 + 24) - i10;
    }

    @Override // jcifs.Encodable
    public int size() {
        return 24;
    }
}
