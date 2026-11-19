package jcifs.ntlmssp.av;

import jcifs.internal.util.SMBUtil;

/* JADX INFO: loaded from: classes5.dex */
public class AvFlags extends AvPair {
    public AvFlags(byte[] bArr) {
        super(6, bArr);
    }

    private static byte[] encode(int i10) {
        byte[] bArr = new byte[4];
        SMBUtil.writeInt4(i10, bArr, 0);
        return bArr;
    }

    public int getFlags() {
        return SMBUtil.readInt4(getRaw(), 0);
    }

    public AvFlags(int i10) {
        this(encode(i10));
    }
}
