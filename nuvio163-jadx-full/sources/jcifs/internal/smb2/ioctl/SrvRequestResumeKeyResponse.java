package jcifs.internal.smb2.ioctl;

import jcifs.Decodable;
import jcifs.internal.SMBProtocolDecodingException;
import jcifs.internal.util.SMBUtil;

/* JADX INFO: loaded from: classes5.dex */
public class SrvRequestResumeKeyResponse implements Decodable {
    private byte[] resumeKey;

    @Override // jcifs.Decodable
    public int decode(byte[] bArr, int i10, int i11) throws SMBProtocolDecodingException {
        if (i11 < 24) {
            throw new SMBProtocolDecodingException("Invalid resume key");
        }
        byte[] bArr2 = new byte[24];
        this.resumeKey = bArr2;
        System.arraycopy(bArr, i10, bArr2, 0, 24);
        SMBUtil.readInt4(bArr, i10 + 24);
        return (i10 + 28) - i10;
    }

    public byte[] getResumeKey() {
        return this.resumeKey;
    }
}
