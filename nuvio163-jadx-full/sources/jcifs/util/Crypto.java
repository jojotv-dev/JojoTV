package jcifs.util;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import jcifs.CIFSUnsupportedCryptoException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/* JADX INFO: loaded from: classes5.dex */
public final class Crypto {
    private static Provider provider;

    private Crypto() {
    }

    public static byte[] des7to8(byte[] bArr) {
        byte[] bArr2 = new byte[8];
        bArr2[0] = (byte) (bArr[0] & 254);
        bArr2[1] = (byte) ((bArr[0] << 7) | ((bArr[1] & 255) >>> 1));
        bArr2[2] = (byte) ((bArr[1] << 6) | ((bArr[2] & 255) >>> 2));
        bArr2[3] = (byte) ((bArr[2] << 5) | ((bArr[3] & 255) >>> 3));
        bArr2[4] = (byte) ((bArr[3] << 4) | ((bArr[4] & 255) >>> 4));
        bArr2[5] = (byte) ((bArr[4] << 3) | ((bArr[5] & 255) >>> 5));
        bArr2[6] = (byte) ((bArr[5] << 2) | ((bArr[6] & 255) >>> 6));
        bArr2[7] = (byte) (bArr[6] << 1);
        for (int i10 = 0; i10 < 8; i10++) {
            byte b5 = bArr2[i10];
            bArr2[i10] = (byte) (b5 ^ (Integer.bitCount(b5 ^ 1) & 1));
        }
        return bArr2;
    }

    public static Cipher getArcfour(byte[] bArr) {
        try {
            Cipher cipher = Cipher.getInstance("RC4");
            cipher.init(1, new SecretKeySpec(bArr, "RC4"));
            return cipher;
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e10) {
            throw new CIFSUnsupportedCryptoException(e10);
        }
    }

    public static Cipher getDES(byte[] bArr) {
        if (bArr.length == 7) {
            return getDES(des7to8(bArr));
        }
        try {
            Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
            cipher.init(1, new SecretKeySpec(bArr, "DES"));
            return cipher;
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e10) {
            throw new CIFSUnsupportedCryptoException(e10);
        }
    }

    public static MessageDigest getHMACT64(byte[] bArr) {
        return new HMACT64(bArr);
    }

    public static MessageDigest getMD4() {
        try {
            return MessageDigest.getInstance("MD4", getProvider());
        } catch (NoSuchAlgorithmException e10) {
            throw new CIFSUnsupportedCryptoException(e10);
        }
    }

    public static MessageDigest getMD5() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e10) {
            throw new CIFSUnsupportedCryptoException(e10);
        }
    }

    public static Provider getProvider() {
        Provider provider2 = provider;
        if (provider2 != null) {
            return provider2;
        }
        BouncyCastleProvider bouncyCastleProvider = new BouncyCastleProvider();
        provider = bouncyCastleProvider;
        return bouncyCastleProvider;
    }

    public static MessageDigest getSHA512() {
        try {
            return MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e10) {
            throw new CIFSUnsupportedCryptoException(e10);
        }
    }

    public static void initProvider(Provider provider2) {
        if (provider == null) {
            provider = provider2;
        } else {
            throw new CIFSUnsupportedCryptoException("Provider can't be re-initialized. Provider has already been initialized with " + provider.getInfo());
        }
    }
}
