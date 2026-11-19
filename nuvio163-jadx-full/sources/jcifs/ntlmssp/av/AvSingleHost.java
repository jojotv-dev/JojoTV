package jcifs.ntlmssp.av;

import jcifs.Configuration;
import jcifs.internal.util.SMBUtil;

/* JADX INFO: loaded from: classes5.dex */
public class AvSingleHost extends AvPair {
    public AvSingleHost(byte[] bArr) {
        super(8, bArr);
    }

    private static byte[] encode(byte[] bArr, byte[] bArr2) {
        byte[] bArr3 = new byte[48];
        SMBUtil.writeInt4(48, bArr3, 0);
        SMBUtil.writeInt4(0L, bArr3, 4);
        System.arraycopy(bArr, 0, bArr3, 8, 8);
        System.arraycopy(bArr2, 0, bArr3, 16, 32);
        return bArr3;
    }

    public AvSingleHost(Configuration configuration) {
        this(new byte[8], configuration.getMachineId());
    }

    public AvSingleHost(byte[] bArr, byte[] bArr2) {
        this(encode(bArr, bArr2));
    }
}
