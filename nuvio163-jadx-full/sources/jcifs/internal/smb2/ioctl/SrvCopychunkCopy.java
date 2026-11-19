package jcifs.internal.smb2.ioctl;

import jcifs.Encodable;
import jcifs.internal.util.SMBUtil;

/* JADX INFO: loaded from: classes5.dex */
public class SrvCopychunkCopy implements Encodable {
    private final SrvCopychunk[] chunks;
    private final byte[] sourceKey;

    public SrvCopychunkCopy(byte[] bArr, SrvCopychunk... srvCopychunkArr) {
        this.sourceKey = bArr;
        this.chunks = srvCopychunkArr;
    }

    @Override // jcifs.Encodable
    public int encode(byte[] bArr, int i10) {
        System.arraycopy(this.sourceKey, 0, bArr, i10, 24);
        SMBUtil.writeInt4(this.chunks.length, bArr, i10 + 24);
        int iEncode = i10 + 32;
        for (SrvCopychunk srvCopychunk : this.chunks) {
            iEncode += srvCopychunk.encode(bArr, iEncode);
        }
        return iEncode - i10;
    }

    @Override // jcifs.Encodable
    public int size() {
        return (this.chunks.length * 24) + 32;
    }
}
