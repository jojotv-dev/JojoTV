package jcifs.internal;

/* JADX INFO: loaded from: classes5.dex */
public interface SMBSigningDigest {
    void sign(byte[] bArr, int i10, int i11, CommonServerMessageBlock commonServerMessageBlock, CommonServerMessageBlock commonServerMessageBlock2);

    boolean verify(byte[] bArr, int i10, int i11, int i12, CommonServerMessageBlock commonServerMessageBlock);
}
