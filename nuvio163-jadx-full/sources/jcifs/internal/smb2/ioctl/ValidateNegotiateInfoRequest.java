package jcifs.internal.smb2.ioctl;

import jcifs.Encodable;
import jcifs.internal.util.SMBUtil;

/* JADX INFO: loaded from: classes5.dex */
public class ValidateNegotiateInfoRequest implements Encodable {
    private int capabilities;
    private byte[] clientGuid;
    private int[] dialects;
    private int securityMode;

    public ValidateNegotiateInfoRequest(int i10, byte[] bArr, int i11, int[] iArr) {
        this.capabilities = i10;
        this.clientGuid = bArr;
        this.securityMode = i11;
        this.dialects = iArr;
    }

    @Override // jcifs.Encodable
    public int encode(byte[] bArr, int i10) {
        SMBUtil.writeInt4(this.capabilities, bArr, i10);
        System.arraycopy(this.clientGuid, 0, bArr, i10 + 4, 16);
        SMBUtil.writeInt2(this.securityMode, bArr, i10 + 20);
        SMBUtil.writeInt2(this.dialects.length, bArr, i10 + 22);
        int i11 = i10 + 24;
        for (int i12 : this.dialects) {
            SMBUtil.writeInt2(i12, bArr, i11);
            i11 += 2;
        }
        return i11 - i10;
    }

    @Override // jcifs.Encodable
    public int size() {
        return (this.dialects.length * 2) + 24;
    }
}
