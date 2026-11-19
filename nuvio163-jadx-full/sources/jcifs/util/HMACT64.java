package jcifs.util;

import java.security.MessageDigest;

/* JADX INFO: loaded from: classes5.dex */
class HMACT64 extends MessageDigest implements Cloneable {
    private static final int BLOCK_LENGTH = 64;
    private static final byte IPAD = 54;
    private static final byte OPAD = 92;
    private byte[] ipad;
    private MessageDigest md5;
    private byte[] opad;

    public HMACT64(byte[] bArr) {
        super("HMACT64");
        this.ipad = new byte[64];
        this.opad = new byte[64];
        int iMin = Math.min(bArr.length, 64);
        for (int i10 = 0; i10 < iMin; i10++) {
            this.ipad[i10] = (byte) (54 ^ bArr[i10]);
            this.opad[i10] = (byte) (92 ^ bArr[i10]);
        }
        while (iMin < 64) {
            this.ipad[iMin] = IPAD;
            this.opad[iMin] = OPAD;
            iMin++;
        }
        this.md5 = Crypto.getMD5();
        engineReset();
    }

    @Override // java.security.MessageDigest, java.security.MessageDigestSpi
    public Object clone() {
        try {
            return new HMACT64(this);
        } catch (CloneNotSupportedException e10) {
            throw new IllegalStateException(e10.getMessage());
        }
    }

    @Override // java.security.MessageDigestSpi
    public byte[] engineDigest() {
        byte[] bArrDigest = this.md5.digest();
        this.md5.update(this.opad);
        return this.md5.digest(bArrDigest);
    }

    @Override // java.security.MessageDigestSpi
    public int engineGetDigestLength() {
        return this.md5.getDigestLength();
    }

    @Override // java.security.MessageDigestSpi
    public void engineReset() {
        this.md5.reset();
        this.md5.update(this.ipad);
    }

    @Override // java.security.MessageDigestSpi
    public void engineUpdate(byte b5) {
        this.md5.update(b5);
    }

    @Override // java.security.MessageDigestSpi
    public void engineUpdate(byte[] bArr, int i10, int i11) {
        this.md5.update(bArr, i10, i11);
    }

    @Override // java.security.MessageDigestSpi
    public int engineDigest(byte[] bArr, int i10, int i11) {
        byte[] bArrDigest = this.md5.digest();
        this.md5.update(this.opad);
        this.md5.update(bArrDigest);
        try {
            return this.md5.digest(bArr, i10, i11);
        } catch (Exception unused) {
            throw new IllegalStateException();
        }
    }

    private HMACT64(HMACT64 hmact64) {
        super("HMACT64");
        this.ipad = new byte[64];
        this.opad = new byte[64];
        this.ipad = hmact64.ipad;
        this.opad = hmact64.opad;
        this.md5 = (MessageDigest) hmact64.md5.clone();
    }
}
