package jcifs.internal.fscc;

import java.nio.charset.StandardCharsets;
import jcifs.internal.util.SMBUtil;

/* JADX INFO: loaded from: classes5.dex */
public class FileRenameInformation2 implements FileInformation {
    private String fileName;
    private boolean replaceIfExists;

    public FileRenameInformation2() {
    }

    @Override // jcifs.Decodable
    public int decode(byte[] bArr, int i10, int i11) {
        this.replaceIfExists = bArr[i10] != 0;
        int int4 = SMBUtil.readInt4(bArr, i10 + 16);
        int i12 = i10 + 20;
        byte[] bArr2 = new byte[int4];
        System.arraycopy(bArr, i12, bArr2, 0, int4);
        int i13 = i12 + int4;
        this.fileName = new String(bArr2, StandardCharsets.UTF_16LE);
        return i13 - i10;
    }

    @Override // jcifs.Encodable
    public int encode(byte[] bArr, int i10) {
        bArr[i10] = this.replaceIfExists ? (byte) 1 : (byte) 0;
        byte[] bytes = this.fileName.getBytes(StandardCharsets.UTF_16LE);
        SMBUtil.writeInt4(bytes.length, bArr, i10 + 16);
        int i11 = i10 + 20;
        System.arraycopy(bytes, 0, bArr, i11, bytes.length);
        return (i11 + bytes.length) - i10;
    }

    @Override // jcifs.internal.fscc.FileInformation
    public byte getFileInformationLevel() {
        return (byte) 10;
    }

    @Override // jcifs.Encodable
    public int size() {
        return (this.fileName.length() * 2) + 20;
    }

    public FileRenameInformation2(String str, boolean z2) {
        this.fileName = str;
        this.replaceIfExists = z2;
    }
}
