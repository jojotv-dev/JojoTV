package jcifs.internal.dfs;

import java.nio.charset.StandardCharsets;
import jcifs.Encodable;
import jcifs.internal.util.SMBUtil;

/* JADX INFO: loaded from: classes5.dex */
public class DfsReferralRequestBuffer implements Encodable {
    private final int maxReferralLevel;
    private final String path;

    public DfsReferralRequestBuffer(String str, int i10) {
        this.path = str;
        this.maxReferralLevel = i10;
    }

    @Override // jcifs.Encodable
    public int encode(byte[] bArr, int i10) {
        SMBUtil.writeInt2(this.maxReferralLevel, bArr, i10);
        int i11 = i10 + 2;
        byte[] bytes = this.path.getBytes(StandardCharsets.UTF_16LE);
        System.arraycopy(bytes, 0, bArr, i11, bytes.length);
        int length = i11 + bytes.length;
        SMBUtil.writeInt2(0L, bArr, length);
        return (length + 2) - i10;
    }

    @Override // jcifs.Encodable
    public int size() {
        return (this.path.length() * 2) + 4;
    }
}
