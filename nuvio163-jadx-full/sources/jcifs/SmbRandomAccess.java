package jcifs;

import java.io.DataInput;
import java.io.DataOutput;

/* JADX INFO: loaded from: classes5.dex */
public interface SmbRandomAccess extends DataOutput, DataInput, AutoCloseable {
    @Override // java.lang.AutoCloseable
    void close();

    long getFilePointer();

    long length();

    int read();

    int read(byte[] bArr);

    int read(byte[] bArr, int i10, int i11);

    void seek(long j10);

    void setLength(long j10);
}
