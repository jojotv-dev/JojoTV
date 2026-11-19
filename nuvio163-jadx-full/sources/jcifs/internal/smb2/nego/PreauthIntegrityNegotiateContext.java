package jcifs.internal.smb2.nego;

import jcifs.Configuration;
import jcifs.internal.util.SMBUtil;

/* JADX INFO: loaded from: classes5.dex */
public class PreauthIntegrityNegotiateContext implements NegotiateContextRequest, NegotiateContextResponse {
    public static final int HASH_ALGO_SHA512 = 1;
    public static final int NEGO_CTX_PREAUTH_TYPE = 1;
    private int[] hashAlgos;
    private byte[] salt;

    public PreauthIntegrityNegotiateContext(Configuration configuration, int[] iArr, byte[] bArr) {
        this.hashAlgos = iArr;
        this.salt = bArr;
    }

    @Override // jcifs.Decodable
    public int decode(byte[] bArr, int i10, int i11) {
        int int2 = SMBUtil.readInt2(bArr, i10);
        int int3 = SMBUtil.readInt2(bArr, i10 + 2);
        int i12 = i10 + 4;
        this.hashAlgos = new int[int2];
        for (int i13 = 0; i13 < int2; i13++) {
            this.hashAlgos[i13] = SMBUtil.readInt2(bArr, i12);
            i12 += 2;
        }
        byte[] bArr2 = new byte[int3];
        this.salt = bArr2;
        System.arraycopy(bArr, i12, bArr2, 0, int3);
        return (i12 + int3) - i10;
    }

    @Override // jcifs.Encodable
    public int encode(byte[] bArr, int i10) {
        int[] iArr = this.hashAlgos;
        SMBUtil.writeInt2(iArr != null ? iArr.length : 0L, bArr, i10);
        byte[] bArr2 = this.salt;
        SMBUtil.writeInt2(bArr2 != null ? bArr2.length : 0L, bArr, i10 + 2);
        int length = i10 + 4;
        int[] iArr2 = this.hashAlgos;
        if (iArr2 != null) {
            for (int i11 : iArr2) {
                SMBUtil.writeInt2(i11, bArr, length);
                length += 2;
            }
        }
        byte[] bArr3 = this.salt;
        if (bArr3 != null) {
            System.arraycopy(bArr3, 0, bArr, length, bArr3.length);
            length += this.salt.length;
        }
        return length - i10;
    }

    @Override // jcifs.internal.smb2.nego.NegotiateContextRequest, jcifs.internal.smb2.nego.NegotiateContextResponse
    public int getContextType() {
        return 1;
    }

    public int[] getHashAlgos() {
        return this.hashAlgos;
    }

    public byte[] getSalt() {
        return this.salt;
    }

    @Override // jcifs.Encodable
    public int size() {
        int[] iArr = this.hashAlgos;
        int length = (iArr != null ? iArr.length * 2 : 0) + 4;
        byte[] bArr = this.salt;
        return length + (bArr != null ? bArr.length : 0);
    }

    public PreauthIntegrityNegotiateContext() {
    }
}
