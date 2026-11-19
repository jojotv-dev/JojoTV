package jcifs.util;

import jcifs.Encodable;

/* JADX INFO: loaded from: classes5.dex */
public class ByteEncodable implements Encodable {
    private byte[] bytes;
    private int len;
    private int off;

    public ByteEncodable(byte[] bArr, int i10, int i11) {
        this.bytes = bArr;
        this.off = i10;
        this.len = i11;
    }

    @Override // jcifs.Encodable
    public int encode(byte[] bArr, int i10) {
        System.arraycopy(this.bytes, this.off, bArr, i10, this.len);
        return this.len;
    }

    @Override // jcifs.Encodable
    public int size() {
        return this.len;
    }
}
