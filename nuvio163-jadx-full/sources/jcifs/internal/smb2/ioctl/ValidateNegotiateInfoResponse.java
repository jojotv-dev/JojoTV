package jcifs.internal.smb2.ioctl;

import jcifs.Decodable;
import jcifs.internal.util.SMBUtil;

/* JADX INFO: loaded from: classes5.dex */
public class ValidateNegotiateInfoResponse implements Decodable {
    private int capabilities;
    private int dialect;
    private int securityMode;
    private byte[] serverGuid = new byte[16];

    @Override // jcifs.Decodable
    public int decode(byte[] bArr, int i10, int i11) {
        this.capabilities = SMBUtil.readInt4(bArr, i10);
        System.arraycopy(bArr, i10 + 4, this.serverGuid, 0, 16);
        this.securityMode = SMBUtil.readInt2(bArr, i10 + 20);
        this.dialect = SMBUtil.readInt2(bArr, i10 + 22);
        return (i10 + 24) - i10;
    }

    public int getCapabilities() {
        return this.capabilities;
    }

    public int getDialect() {
        return this.dialect;
    }

    public int getSecurityMode() {
        return this.securityMode;
    }

    public byte[] getServerGuid() {
        return this.serverGuid;
    }
}
