package jcifs.internal.fscc;

import java.util.Date;
import jcifs.internal.util.SMBUtil;
import jcifs.util.Hexdump;

/* JADX INFO: loaded from: classes5.dex */
public class FileBasicInfo implements BasicFileInformation {
    private int attributes;
    private long changeTime;
    private long createTime;
    private long lastAccessTime;
    private long lastWriteTime;

    public FileBasicInfo() {
    }

    @Override // jcifs.Decodable
    public int decode(byte[] bArr, int i10, int i11) {
        this.createTime = SMBUtil.readTime(bArr, i10);
        this.lastAccessTime = SMBUtil.readTime(bArr, i10 + 8);
        this.lastWriteTime = SMBUtil.readTime(bArr, i10 + 16);
        this.changeTime = SMBUtil.readTime(bArr, i10 + 24);
        this.attributes = SMBUtil.readInt4(bArr, i10 + 32);
        return (i10 + 36) - i10;
    }

    @Override // jcifs.Encodable
    public int encode(byte[] bArr, int i10) {
        SMBUtil.writeTime(this.createTime, bArr, i10);
        SMBUtil.writeTime(this.lastAccessTime, bArr, i10 + 8);
        SMBUtil.writeTime(this.lastWriteTime, bArr, i10 + 16);
        SMBUtil.writeTime(this.changeTime, bArr, i10 + 24);
        SMBUtil.writeInt4(this.attributes, bArr, i10 + 32);
        return (i10 + 40) - i10;
    }

    @Override // jcifs.internal.SmbBasicFileInfo
    public int getAttributes() {
        return this.attributes;
    }

    @Override // jcifs.internal.SmbBasicFileInfo
    public long getCreateTime() {
        return this.createTime;
    }

    @Override // jcifs.internal.fscc.FileInformation
    public byte getFileInformationLevel() {
        return (byte) 4;
    }

    @Override // jcifs.internal.SmbBasicFileInfo
    public long getLastAccessTime() {
        return this.lastAccessTime;
    }

    @Override // jcifs.internal.SmbBasicFileInfo
    public long getLastWriteTime() {
        return this.lastWriteTime;
    }

    @Override // jcifs.internal.SmbBasicFileInfo
    public long getSize() {
        return 0L;
    }

    @Override // jcifs.Encodable
    public int size() {
        return 40;
    }

    public String toString() {
        return new String("SmbQueryFileBasicInfo[createTime=" + new Date(this.createTime) + ",lastAccessTime=" + new Date(this.lastAccessTime) + ",lastWriteTime=" + new Date(this.lastWriteTime) + ",changeTime=" + new Date(this.changeTime) + ",attributes=0x" + Hexdump.toHexString(this.attributes, 4) + "]");
    }

    public FileBasicInfo(long j10, long j11, long j12, long j13, int i10) {
        this.createTime = j10;
        this.lastAccessTime = j11;
        this.lastWriteTime = j12;
        this.changeTime = j13;
        this.attributes = i10;
    }
}
