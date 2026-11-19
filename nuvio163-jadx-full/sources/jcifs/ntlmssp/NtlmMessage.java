package jcifs.ntlmssp;

import jcifs.internal.smb1.trans.SmbComTransaction;

/* JADX INFO: loaded from: classes5.dex */
public abstract class NtlmMessage implements NtlmFlags {
    protected static final int NTLMSSP_TYPE1 = 1;
    protected static final int NTLMSSP_TYPE2 = 2;
    protected static final int NTLMSSP_TYPE3 = 3;
    private static final String OEM_ENCODING = "Cp850";
    protected static final String UNI_ENCODING = "UTF-16LE";
    private int flags;
    protected static final byte[] NTLMSSP_SIGNATURE = {78, SmbComTransaction.TRANS_CALL_NAMED_PIPE, 76, 77, SmbComTransaction.TRANS_WAIT_NAMED_PIPE, SmbComTransaction.TRANS_WAIT_NAMED_PIPE, 80, 0};
    protected static final byte[] NTLMSSP_VERSION = {6, 1, 0, 0, 0, 0, 0, 15};

    public static String getOEMEncoding() {
        return "Cp850";
    }

    public static byte[] readSecurityBuffer(byte[] bArr, int i10) {
        int uShort = readUShort(bArr, i10);
        byte[] bArr2 = new byte[uShort];
        System.arraycopy(bArr, readULong(bArr, i10 + 4), bArr2, 0, uShort);
        return bArr2;
    }

    public static int readULong(byte[] bArr, int i10) {
        return ((bArr[i10 + 3] & 255) << 24) | (bArr[i10] & 255) | ((bArr[i10 + 1] & 255) << 8) | ((bArr[i10 + 2] & 255) << 16);
    }

    public static int readUShort(byte[] bArr, int i10) {
        return ((bArr[i10 + 1] & 255) << 8) | (bArr[i10] & 255);
    }

    public static int writeSecurityBuffer(byte[] bArr, int i10, byte[] bArr2) {
        int length = bArr2 != null ? bArr2.length : 0;
        if (length == 0) {
            return i10 + 4;
        }
        writeUShort(bArr, i10, length);
        writeUShort(bArr, i10 + 2, length);
        return i10 + 4;
    }

    public static int writeSecurityBufferContent(byte[] bArr, int i10, int i11, byte[] bArr2) {
        writeULong(bArr, i11, i10);
        if (bArr2 == null || bArr2.length <= 0) {
            return 0;
        }
        System.arraycopy(bArr2, 0, bArr, i10, bArr2.length);
        return bArr2.length;
    }

    public static void writeULong(byte[] bArr, int i10, int i11) {
        bArr[i10] = (byte) (i11 & 255);
        bArr[i10 + 1] = (byte) ((i11 >> 8) & 255);
        bArr[i10 + 2] = (byte) ((i11 >> 16) & 255);
        bArr[i10 + 3] = (byte) ((i11 >> 24) & 255);
    }

    public static void writeUShort(byte[] bArr, int i10, int i11) {
        bArr[i10] = (byte) (i11 & 255);
        bArr[i10 + 1] = (byte) ((i11 >> 8) & 255);
    }

    public boolean getFlag(int i10) {
        return (i10 & getFlags()) != 0;
    }

    public int getFlags() {
        return this.flags;
    }

    public void setFlag(int i10, boolean z2) {
        int flags;
        if (z2) {
            flags = i10 | getFlags();
        } else {
            flags = (i10 ^ (-1)) & getFlags();
        }
        setFlags(flags);
    }

    public void setFlags(int i10) {
        this.flags = i10;
    }

    public abstract byte[] toByteArray();
}
