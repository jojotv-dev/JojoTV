package jcifs.internal.fscc;

import java.nio.charset.StandardCharsets;
import jcifs.Encodable;
import jcifs.internal.util.SMBUtil;

/* JADX INFO: loaded from: classes5.dex */
public class FsctlPipeWaitRequest implements Encodable {
    private final byte[] nameBytes;
    private final long timeout;
    private final boolean timeoutSpecified;

    public FsctlPipeWaitRequest(String str) {
        this.nameBytes = str.getBytes(StandardCharsets.UTF_16LE);
        this.timeoutSpecified = false;
        this.timeout = 0L;
    }

    @Override // jcifs.Encodable
    public int encode(byte[] bArr, int i10) {
        SMBUtil.writeInt8(this.timeout, bArr, i10);
        SMBUtil.writeInt4(this.nameBytes.length, bArr, i10 + 8);
        bArr[i10 + 12] = this.timeoutSpecified ? (byte) 1 : (byte) 0;
        int i11 = i10 + 14;
        byte[] bArr2 = this.nameBytes;
        System.arraycopy(bArr2, 0, bArr, i11, bArr2.length);
        return (i11 + this.nameBytes.length) - i10;
    }

    @Override // jcifs.Encodable
    public int size() {
        return this.nameBytes.length + 14;
    }

    public FsctlPipeWaitRequest(String str, long j10) {
        this.nameBytes = str.getBytes(StandardCharsets.UTF_16LE);
        this.timeoutSpecified = true;
        this.timeout = j10;
    }
}
