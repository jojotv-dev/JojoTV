package jcifs.internal.smb2.ioctl;

import jcifs.Decodable;
import jcifs.internal.util.SMBUtil;

/* JADX INFO: loaded from: classes5.dex */
public class SrvCopyChunkCopyResponse implements Decodable {
    private int chunkBytesWritten;
    private int chunksWritten;
    private int totalBytesWritten;

    @Override // jcifs.Decodable
    public int decode(byte[] bArr, int i10, int i11) {
        this.chunksWritten = SMBUtil.readInt4(bArr, i10);
        this.chunkBytesWritten = SMBUtil.readInt4(bArr, i10 + 4);
        this.totalBytesWritten = SMBUtil.readInt4(bArr, i10 + 8);
        return (i10 + 12) - i10;
    }

    public int getChunkBytesWritten() {
        return this.chunkBytesWritten;
    }

    public int getChunksWritten() {
        return this.chunksWritten;
    }

    public int getTotalBytesWritten() {
        return this.totalBytesWritten;
    }
}
