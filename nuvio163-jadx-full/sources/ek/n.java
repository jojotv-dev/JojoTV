package ek;

/* JADX INFO: loaded from: classes5.dex */
public interface n {
    int doFinal(byte[] bArr, int i10);

    String getAlgorithmName();

    int getDigestSize();

    void reset();

    void update(byte b5);

    void update(byte[] bArr, int i10, int i11);
}
