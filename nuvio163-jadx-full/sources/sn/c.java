package sn;

import androidx.media3.extractor.metadata.dvbsi.AppInfoTableDecoder;
import com.liskovsoft.sharedutils.helpers.PermissionHelpers;

/* JADX INFO: loaded from: classes5.dex */
public abstract class c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final short[] f56894a = new short[128];

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final byte[] f56895b;

    static {
        byte[] bArr = new byte[112];
        f56895b = bArr;
        byte[] bArr2 = new byte[128];
        a(bArr2, (byte) 1, 0, 15);
        a(bArr2, (byte) 2, 16, 31);
        a(bArr2, (byte) 3, 32, 63);
        a(bArr2, (byte) 0, 64, 65);
        a(bArr2, (byte) 4, 66, 95);
        a(bArr2, (byte) 5, 96, 96);
        a(bArr2, (byte) 6, 97, 108);
        a(bArr2, (byte) 7, 109, 109);
        a(bArr2, (byte) 6, 110, 111);
        a(bArr2, (byte) 8, 112, 112);
        a(bArr2, (byte) 9, PermissionHelpers.REQUEST_MIC, 115);
        a(bArr2, (byte) 10, AppInfoTableDecoder.APPLICATION_INFORMATION_TABLE_ID, AppInfoTableDecoder.APPLICATION_INFORMATION_TABLE_ID);
        a(bArr2, (byte) 0, 117, 127);
        a(bArr, (byte) -2, 0, bArr.length - 1);
        a(bArr, (byte) -1, 8, 11);
        a(bArr, (byte) 0, 24, 27);
        a(bArr, (byte) 16, 40, 43);
        a(bArr, (byte) 0, 58, 59);
        a(bArr, (byte) 0, 72, 73);
        a(bArr, (byte) 16, 89, 91);
        a(bArr, (byte) 16, 104, 104);
        byte[] bArr3 = {0, 0, 0, 0, 31, 15, 15, 15, 7, 7, 7};
        byte[] bArr4 = {-2, -2, -2, -2, 0, 48, 16, 64, 80, 32, 96};
        for (int i10 = 0; i10 < 128; i10++) {
            byte b5 = bArr2[i10];
            f56894a[i10] = (short) (bArr4[b5] | ((bArr3[b5] & i10) << 8));
        }
    }

    public static void a(byte[] bArr, byte b5, int i10, int i11) {
        while (i10 <= i11) {
            bArr[i10] = b5;
            i10++;
        }
    }
}
