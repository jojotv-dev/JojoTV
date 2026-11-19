package jcifs.internal.smb2.ioctl;

import jcifs.Decodable;
import jcifs.internal.util.SMBUtil;

/* JADX INFO: loaded from: classes5.dex */
public class SrvPipePeekResponse implements Decodable {
    private byte[] data;
    private int messageLength;
    private int namedPipeState;
    private int numberOfMessages;
    private int readDataAvailable;

    @Override // jcifs.Decodable
    public int decode(byte[] bArr, int i10, int i11) {
        this.namedPipeState = SMBUtil.readInt4(bArr, i10);
        this.readDataAvailable = SMBUtil.readInt4(bArr, i10 + 4);
        this.numberOfMessages = SMBUtil.readInt4(bArr, i10 + 8);
        this.messageLength = SMBUtil.readInt4(bArr, i10 + 12);
        int i12 = i10 + 16;
        byte[] bArr2 = new byte[i11 - 16];
        this.data = bArr2;
        if (bArr2.length > 0) {
            System.arraycopy(bArr, i12, bArr2, 0, bArr2.length);
        }
        return i12 - i10;
    }

    public byte[] getData() {
        return this.data;
    }

    public int getMessageLength() {
        return this.messageLength;
    }

    public int getNamedPipeState() {
        return this.namedPipeState;
    }

    public int getNumberOfMessages() {
        return this.numberOfMessages;
    }

    public int getReadDataAvailable() {
        return this.readDataAvailable;
    }
}
