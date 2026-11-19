package jcifs.internal.smb2.nego;

import jcifs.Configuration;
import jcifs.internal.util.SMBUtil;

/* JADX INFO: loaded from: classes5.dex */
public class EncryptionNegotiateContext implements NegotiateContextRequest, NegotiateContextResponse {
    public static final int CIPHER_AES128_CCM = 1;
    public static final int CIPHER_AES128_GCM = 2;
    public static final int NEGO_CTX_ENC_TYPE = 2;
    private int[] ciphers;

    public EncryptionNegotiateContext(Configuration configuration, int[] iArr) {
        this.ciphers = iArr;
    }

    @Override // jcifs.Decodable
    public int decode(byte[] bArr, int i10, int i11) {
        int int2 = SMBUtil.readInt2(bArr, i10);
        int i12 = i10 + 2;
        this.ciphers = new int[int2];
        for (int i13 = 0; i13 < int2; i13++) {
            this.ciphers[i13] = SMBUtil.readInt2(bArr, i12);
            i12 += 2;
        }
        return i12 - i10;
    }

    @Override // jcifs.Encodable
    public int encode(byte[] bArr, int i10) {
        int[] iArr = this.ciphers;
        SMBUtil.writeInt2(iArr != null ? iArr.length : 0L, bArr, i10);
        int i11 = i10 + 2;
        int[] iArr2 = this.ciphers;
        if (iArr2 != null) {
            for (int i12 : iArr2) {
                SMBUtil.writeInt2(i12, bArr, i11);
                i11 += 2;
            }
        }
        return i11 - i10;
    }

    public int[] getCiphers() {
        return this.ciphers;
    }

    @Override // jcifs.internal.smb2.nego.NegotiateContextRequest, jcifs.internal.smb2.nego.NegotiateContextResponse
    public int getContextType() {
        return 2;
    }

    @Override // jcifs.Encodable
    public int size() {
        int[] iArr = this.ciphers;
        return (iArr != null ? iArr.length * 2 : 0) + 4;
    }

    public EncryptionNegotiateContext() {
    }
}
