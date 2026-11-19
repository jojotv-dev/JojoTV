package jcifs.pac;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.kerberos.KerberosKey;
import jcifs.pac.kerberos.KerberosConstants;

/* JADX INFO: loaded from: classes5.dex */
public class PacMac {
    private static final String HMAC_KEY = "HMAC";
    private static final byte[] MD5_CONSTANT = "signaturekey\u0000".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] ZERO_IV = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public static byte[] calculateMac(int i10, Map<Integer, KerberosKey> map, byte[] bArr) throws PACDecodingException {
        try {
            if (i10 == -138) {
                KerberosKey kerberosKey = map.get(23);
                if (kerberosKey != null) {
                    return calculateMacArcfourHMACMD5(17, kerberosKey, bArr);
                }
                throw new PACDecodingException("Missing key");
            }
            if (i10 != 15 && i10 != 16) {
                throw new PACDecodingException("Invalid MAC algorithm");
            }
            KerberosKey kerberosKey2 = i10 == 15 ? map.get(17) : map.get(18);
            if (kerberosKey2 != null) {
                return calculateMacHMACAES(17, kerberosKey2, bArr);
            }
            throw new PACDecodingException("Missing key");
        } catch (GeneralSecurityException e10) {
            throw new PACDecodingException("Failed to calculate MAC", e10);
        }
    }

    public static byte[] calculateMacArcfourHMACMD5(int i10, Key key, byte[] bArr) throws NoSuchAlgorithmException, InvalidKeyException {
        int iMapArcfourMD5KeyUsage = mapArcfourMD5KeyUsage(i10);
        Mac mac = Mac.getInstance(KerberosConstants.HMAC_ALGORITHM);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        mac.init(key);
        byte[] bArrDoFinal = mac.doFinal(MD5_CONSTANT);
        try {
            messageDigest.update((byte) (iMapArcfourMD5KeyUsage & 255));
            messageDigest.update((byte) ((iMapArcfourMD5KeyUsage >> 8) & 255));
            messageDigest.update((byte) ((iMapArcfourMD5KeyUsage >> 16) & 255));
            messageDigest.update((byte) ((iMapArcfourMD5KeyUsage >> 24) & 255));
            byte[] bArrDigest = messageDigest.digest(bArr);
            mac.reset();
            mac.init(new SecretKeySpec(bArrDoFinal, HMAC_KEY));
            return mac.doFinal(bArrDigest);
        } finally {
            Arrays.fill(bArrDoFinal, 0, bArrDoFinal.length, (byte) 0);
        }
    }

    public static byte[] calculateMacHMACAES(int i10, KerberosKey kerberosKey, byte[] bArr) throws BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        byte[] bArr2 = new byte[12];
        byte[] bArrDeriveKeyAES = deriveKeyAES(kerberosKey, new byte[]{(byte) ((i10 >> 24) & 255), (byte) ((i10 >> 16) & 255), (byte) ((i10 >> 8) & 255), (byte) (i10 & 255), -103});
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(bArrDeriveKeyAES, HMAC_KEY));
            System.arraycopy(mac.doFinal(bArr), 0, bArr2, 0, 12);
            return bArr2;
        } finally {
            Arrays.fill(bArrDeriveKeyAES, 0, bArrDeriveKeyAES.length, (byte) 0);
        }
    }

    private static int carry_add(byte[] bArr, byte[] bArr2, int i10, int i11) {
        int length = bArr.length;
        int i12 = length << 3;
        int i13 = ((((i11 / length) * (i12 + 13)) + (i12 - 1)) + ((length - (i11 % length)) << 3)) % i12;
        int i14 = i13 >>> 3;
        int length2 = i11 % bArr2.length;
        int i15 = i10 + (bArr2[length2] & 255) + ((((bArr[(length - i14) % length] & 255) | ((bArr[((length - 1) - i14) % length] & 255) << 8)) >>> ((i13 & 7) + 1)) & 255);
        bArr2[length2] = (byte) (i15 & 255);
        return i15 >>> 8;
    }

    public static byte[] deriveKeyAES(KerberosKey kerberosKey, byte[] bArr) throws BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        byte[] encoded = kerberosKey.getEncoded();
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(encoded, "AES");
        byte[] bArr2 = ZERO_IV;
        cipher.init(1, secretKeySpec, new IvParameterSpec(bArr2, 0, bArr2.length));
        if (bArr.length != cipher.getBlockSize()) {
            bArr = expandNFold(bArr, cipher.getBlockSize());
        }
        int length = encoded.length;
        byte[] bArr3 = new byte[length];
        int i10 = 0;
        while (i10 < length) {
            bArr = cipher.doFinal(bArr);
            int iMin = Math.min(length - i10, bArr.length);
            System.arraycopy(bArr, 0, bArr3, i10, iMin);
            i10 += iMin;
        }
        return bArr3;
    }

    public static byte[] expandNFold(byte[] bArr, int i10) {
        byte[] bArr2 = new byte[i10];
        int iCarry_add = 0;
        for (int iLcm = lcm(i10, bArr.length) - 1; iLcm >= 0; iLcm--) {
            iCarry_add = carry_add(bArr, bArr2, iCarry_add, iLcm);
        }
        if (iCarry_add != 0) {
            for (int i11 = i10 - 1; i11 >= 0; i11--) {
                int i12 = iCarry_add + (bArr2[i11] & 255);
                bArr2[i11] = (byte) (i12 & 255);
                iCarry_add = i12 >>> 8;
            }
        }
        return bArr2;
    }

    private static int lcm(int i10, int i11) {
        int i12 = i10;
        int i13 = i11;
        while (i13 != 0) {
            int i14 = i12 % i13;
            i12 = i13;
            i13 = i14;
        }
        return (i10 * i11) / i12;
    }

    private static int mapArcfourMD5KeyUsage(int i10) {
        if (i10 == 3 || i10 == 9 || i10 == 23) {
            return 13;
        }
        return i10;
    }
}
