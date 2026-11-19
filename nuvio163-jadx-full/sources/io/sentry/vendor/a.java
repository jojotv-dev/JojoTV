package io.sentry.vendor;

import java.io.UnsupportedEncodingException;
import jcifs.internal.smb1.ServerMessageBlock;
import jcifs.internal.smb1.trans.SmbComTransaction;

/* JADX INFO: loaded from: classes5.dex */
public abstract class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final byte[] f26303a = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, SmbComTransaction.TRANS_WAIT_NAMED_PIPE, SmbComTransaction.TRANS_CALL_NAMED_PIPE, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, SmbComTransaction.NET_SERVER_ENUM2, 105, 106, 107, 108, 109, 110, 111, 112, ServerMessageBlock.SMB_COM_TREE_DISCONNECT, ServerMessageBlock.SMB_COM_NEGOTIATE, ServerMessageBlock.SMB_COM_SESSION_SETUP_ANDX, ServerMessageBlock.SMB_COM_LOGOFF_ANDX, ServerMessageBlock.SMB_COM_TREE_CONNECT_ANDX, 118, 119, 120, 121, 122, 48, 49, ServerMessageBlock.SMB_COM_TRANSACTION2, 51, ServerMessageBlock.SMB_COM_FIND_CLOSE2, 53, 54, 55, 56, 57, ServerMessageBlock.SMB_COM_ECHO, ServerMessageBlock.SMB_COM_WRITE_ANDX};

    public static byte[] a(byte[] bArr) {
        byte[] bArr2;
        int length = bArr.length;
        int i10 = (length / 3) * 4;
        int i11 = length % 3;
        if (i11 == 1) {
            i10 += 2;
        } else if (i11 == 2) {
            i10 += 3;
        }
        byte[] bArr3 = new byte[i10];
        int i12 = 0;
        int i13 = 0;
        int i14 = -1;
        while (true) {
            int i15 = i12 + 3;
            bArr2 = f26303a;
            if (i15 > length) {
                break;
            }
            int i16 = (bArr[i12 + 2] & 255) | ((bArr[i12] & 255) << 16) | ((bArr[i12 + 1] & 255) << 8);
            bArr3[i13] = bArr2[(i16 >> 18) & 63];
            bArr3[i13 + 1] = bArr2[(i16 >> 12) & 63];
            bArr3[i13 + 2] = bArr2[(i16 >> 6) & 63];
            bArr3[i13 + 3] = bArr2[i16 & 63];
            int i17 = i13 + 4;
            i14--;
            if (i14 == 0) {
                i13 += 5;
                bArr3[i17] = 10;
                i14 = 19;
            } else {
                i13 = i17;
            }
            i12 = i15;
        }
        if (i12 == length - 1) {
            int i18 = (bArr[i12] & 255) << 4;
            bArr3[i13] = bArr2[(i18 >> 6) & 63];
            bArr3[i13 + 1] = bArr2[i18 & 63];
            return bArr3;
        }
        if (i12 == length - 2) {
            int i19 = ((bArr[i12 + 1] & 255) << 2) | ((bArr[i12] & 255) << 10);
            bArr3[i13] = bArr2[(i19 >> 12) & 63];
            bArr3[i13 + 1] = bArr2[(i19 >> 6) & 63];
            bArr3[i13 + 2] = bArr2[i19 & 63];
        }
        return bArr3;
    }

    public static String b(byte[] bArr) {
        try {
            return new String(a(bArr), "US-ASCII");
        } catch (UnsupportedEncodingException e10) {
            throw new AssertionError(e10);
        }
    }
}
