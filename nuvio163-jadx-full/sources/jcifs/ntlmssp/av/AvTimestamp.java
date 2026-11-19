package jcifs.ntlmssp.av;

import jcifs.internal.util.SMBUtil;

/* JADX INFO: loaded from: classes5.dex */
public class AvTimestamp extends AvPair {
    public AvTimestamp(byte[] bArr) {
        super(7, bArr);
    }

    private static byte[] encode(long j10) {
        byte[] bArr = new byte[8];
        SMBUtil.writeInt8(j10, bArr, 0);
        return bArr;
    }

    public long getTimestamp() {
        return SMBUtil.readInt8(getRaw(), 0);
    }

    public AvTimestamp(long j10) {
        this(encode(j10));
    }
}
