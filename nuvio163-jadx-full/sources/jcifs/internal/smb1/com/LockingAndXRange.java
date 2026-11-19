package jcifs.internal.smb1.com;

import jcifs.Decodable;
import jcifs.Encodable;
import jcifs.internal.util.SMBUtil;

/* JADX INFO: loaded from: classes5.dex */
public class LockingAndXRange implements Encodable, Decodable {
    private long byteOffset;
    private final boolean largeFile;
    private long lengthInBytes;
    private int pid;

    public LockingAndXRange(boolean z2) {
        this.largeFile = z2;
    }

    @Override // jcifs.Decodable
    public int decode(byte[] bArr, int i10, int i11) {
        if (!this.largeFile) {
            this.pid = SMBUtil.readInt2(bArr, i10);
            this.byteOffset = SMBUtil.readInt4(bArr, i10 + 2);
            this.lengthInBytes = SMBUtil.readInt4(bArr, i10 + 6);
            return 10;
        }
        this.pid = SMBUtil.readInt2(bArr, i10);
        this.byteOffset = (SMBUtil.readInt4(bArr, i10 + 4) << 32) | SMBUtil.readInt4(bArr, i10 + 8);
        this.lengthInBytes = SMBUtil.readInt4(bArr, i10 + 16) | (SMBUtil.readInt4(bArr, i10 + 12) << 32);
        return 20;
    }

    @Override // jcifs.Encodable
    public int encode(byte[] bArr, int i10) {
        if (!this.largeFile) {
            SMBUtil.writeInt2(this.pid, bArr, i10);
            SMBUtil.writeInt4(this.byteOffset, bArr, i10 + 2);
            SMBUtil.writeInt4(this.lengthInBytes, bArr, i10 + 6);
            return 10;
        }
        SMBUtil.writeInt2(this.pid, bArr, i10);
        SMBUtil.writeInt4(this.byteOffset >> 32, bArr, i10 + 4);
        SMBUtil.writeInt4(this.byteOffset, bArr, i10 + 8);
        SMBUtil.writeInt4(this.lengthInBytes >> 32, bArr, i10 + 12);
        SMBUtil.writeInt4(this.lengthInBytes, bArr, i10 + 16);
        return 20;
    }

    public long getByteOffset() {
        return this.byteOffset;
    }

    public long getLengthInBytes() {
        return this.lengthInBytes;
    }

    public int getPid() {
        return this.pid;
    }

    @Override // jcifs.Encodable
    public int size() {
        return this.largeFile ? 20 : 10;
    }
}
